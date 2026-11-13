import me.modmuss50.mpp.ModPublishExtension
import me.modmuss50.mpp.platforms.modrinth.ModrinthEnvironment

plugins {
    id("java-library")
    id("maven-publish")
    id("net.fabricmc.fabric-loom-remap") version "1.17.20"
    id("idea")
    // No version: the root project declares it, for the reason given in stonecutter.gradle.kts
    id("me.modmuss50.mod-publish-plugin")
}

apply(from = rootProject.file("common.gradle.kts"))

fun prop(name: String): String = property(name) as String

repositories {
    // Accessories, owo-lib and endec.
    maven("https://maven.wispforest.io/releases/") { name = "Wisp Forest" }
    // Jankson, a dependency of owo-lib.
    mavenCentral()
    // Elytra Trims, served from its Modrinth files.
    maven("https://api.modrinth.com/maven") {
        name = "Modrinth"
        content { includeGroup("maven.modrinth") }
    }
}

// No mixin block: Loom remaps the mixins' target names directly when it remaps the jar, so no refmap is made.
loom {
    runs {
        // One run directory shared by every target, so worlds and options survive switching between them.
        named("client") { runDirectory.set(rootProject.file("run")) }
        named("server") { runDirectory.set(rootProject.file("run-server")) }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${prop("minecraft_version")}")
    // Official Mojang mappings rather than Yarn, so this loader's half of the source reads the same as the
    // NeoForge half and one shared tree compiles for both.
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${prop("loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${prop("fabric_api_version")}")
    // Accessories is required, and brings owo-lib with it.
    modImplementation("io.wispforest:accessories-fabric:${prop("accessories_version")}")
    // Elytra Trims' API, called only when Elytra Trims is installed, so it stays off the runtime classpath.
    modCompileOnly("maven.modrinth:elytra-trims:${prop("elytra_trims_version")}")
}

// The other loader's code lives in a package of its own so it can simply be left out, along with the mixin
// config that lists it. Excluding them from the source set keeps them out of the sources jar as well.
sourceSets.main.get().java.exclude("**/neoforge/**")
sourceSets.main.get().resources.exclude(prop("mod_id") + "-neoforge.mixins.json")

// The sources jar keeps the Mojang names the code is written in, matching the NeoForge one, rather than being
// remapped to intermediary like the mod jar. Loom copies it unchanged when the two namespaces match.
tasks.named<net.fabricmc.loom.task.RemapSourcesJarTask>("remapSourcesJar") {
    targetNamespace = "named"
}

// Expand the declared properties into the mod metadata template. The shared keys come from
// common.gradle.kts, and fabric.mod.json needs none of its own yet.
@Suppress("UNCHECKED_CAST")
val commonMetadataProperties = extra["commonMetadataProperties"] as Map<String, String>

val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    inputs.properties(commonMetadataProperties)
    expand(commonMetadataProperties)
    from(rootProject.file("src/main/templates/fabric"))
    into(layout.buildDirectory.dir("generated/sources/modMetadata"))
}
sourceSets.main.get().resources.srcDir(generateModMetadata)

// Releasing: this target's uploads, set up in parallel with the other loader's build script. The root project
// holds the shared settings, the changelog and the GitHub release these jars are attached to, all in
// stonecutter.gradle.kts, and common.gradle.kts works out the Minecraft versions.
@Suppress("UNCHECKED_CAST")
val releaseMinecraftVersions = extra["releaseMinecraftVersions"] as List<String>
val releaseMinecraftLabel = extra["releaseMinecraftLabel"] as String
val rootPublishMods = rootProject.the<ModPublishExtension>()

publishMods {
    // A local rather than a script-level value, so the changelog lambda captures a string rather than the
    // whole build script
    val modVersion = prop("mod_version")
    // The remapped jars in build/libs, the ones that load outside the development environment, rather than
    // the ones in build/devlibs that keep Mojang's names
    file = tasks.remapJar.flatMap { it.archiveFile }
    // The portals show no release title above the notes, so they get the version as a heading
    changelog = rootPublishMods.changelog.map { "## $modVersion\n\n$it" }
    type = STABLE
    // The jar's version, such as "1.0.4+1.21.1-fabric". It names the loader, since both loaders
    // ship from this project.
    version = project.version.toString()
    modLoaders.add("fabric")
    dryRun = rootPublishMods.dryRun

    modrinth {
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        projectId = "qWoiYCbs"
        // Titled like the earlier versions, such as "Accessories Compat: Elytra & Totem 1.0.4 for Fabric 1.21.1"
        displayName = "${prop("portal_name")} $modVersion for Fabric $releaseMinecraftLabel"
        minecraftVersions.addAll(releaseMinecraftVersions)
        // Needed on both the client and the server, as every earlier version was listed
        environment = ModrinthEnvironment.CLIENT_AND_SERVER
        additionalFile(tasks.named("remapSourcesJar")) {
            type = SOURCES_JAR
        }
        // fabric.mod.json requires Fabric API alongside Accessories
        requires("accessories", "fabric-api")
    }

    curseforge {
        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
        projectId = "1690485"
        // Titled with the same version number as on Modrinth, such as "1.0.4+1.21.1-fabric"
        displayName = project.version.toString()
        minecraftVersions.addAll(releaseMinecraftVersions)
        client = true
        server = true
        changelogType = "markdown"
        // CurseForge files an additional file under the main one it was uploaded with
        additionalFile(tasks.named("remapSourcesJar")) {
            name = "${project.version}-sources"
        }
        requires("accessories", "fabric-api")
    }

    // Only the main jar goes on GitHub, as with the earlier releases. The root project orders these uploads.
    github {
        accessToken = providers.environmentVariable("GITHUB_TOKEN")
        parent(rootProject.tasks.named("publishGithub"))
    }
}

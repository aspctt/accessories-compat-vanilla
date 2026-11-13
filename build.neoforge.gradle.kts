import me.modmuss50.mpp.ModPublishExtension
import me.modmuss50.mpp.platforms.modrinth.ModrinthEnvironment

plugins {
    id("java-library")
    id("maven-publish")
    id("net.neoforged.moddev") version "2.0.146"
    id("idea")
    // No version: the root project declares it, for the reason given in stonecutter.gradle.kts
    id("me.modmuss50.mod-publish-plugin")
}

apply(from = rootProject.file("common.gradle.kts"))

fun prop(name: String): String = property(name) as String

repositories {
    // Accessories, owo-lib and endec.
    maven("https://maven.wispforest.io/releases/") { name = "Wisp Forest" }
    // Forgified Fabric API, which owo-lib and Accessories build on for NeoForge.
    maven("https://maven.su5ed.dev/releases") { name = "Sinytra" }
    // Jankson, a dependency of owo-lib.
    mavenCentral()
    // Elytra Trims, served from its Modrinth files.
    maven("https://api.modrinth.com/maven") {
        name = "Modrinth"
        content { includeGroup("maven.modrinth") }
    }
}

neoForge {
    version = prop("neo_version")

    parchment {
        mappingsVersion = prop("parchment_mappings_version")
        minecraftVersion = prop("parchment_minecraft_version")
    }

    // Access Transformers are automatically detected at
    // src/main/resources/META-INF/accesstransformer.cfg

    runs {
        create("client") {
            client()
            logLevel = org.slf4j.event.Level.DEBUG
            // One run directory shared by every target, so worlds and options survive switching between them.
            gameDirectory = rootProject.file("run")
        }
        create("server") {
            server()
            programArgument("--nogui")
            logLevel = org.slf4j.event.Level.DEBUG
            gameDirectory = rootProject.file("run-server")
        }
    }

    mods {
        create(prop("mod_id")) {
            sourceSet(sourceSets.main.get())
        }
    }
}

// The other loader's code lives in a package of its own so it can simply be left out.
sourceSets.main.get().java.exclude("**/fabric/**")

dependencies {
    // Accessories is required, and brings owo-lib and Forgified Fabric API's base module with it.
    implementation("io.wispforest:accessories-neoforge:${prop("accessories_version")}")
    // Elytra Trims' API, called only when Elytra Trims is installed, so it stays off the runtime classpath.
    compileOnly("maven.modrinth:elytra-trims:${prop("elytra_trims_version")}")
}

// Expand the declared properties into the mod metadata template. The shared keys come from
// common.gradle.kts; the ones below exist only in neoforge.mods.toml.
@Suppress("UNCHECKED_CAST")
val commonMetadataProperties = extra["commonMetadataProperties"] as Map<String, String>

val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val replaceProperties = commonMetadataProperties + mapOf(
        "neo_version" to prop("neo_version"),
        "neo_version_range" to prop("neo_version_range"),
    )
    inputs.properties(replaceProperties)
    expand(replaceProperties)
    from(rootProject.file("src/main/templates/neoforge"))
    into(layout.buildDirectory.dir("generated/sources/modMetadata"))
}
sourceSets.main.get().resources.srcDir(generateModMetadata)
neoForge.ideSyncTask(generateModMetadata)

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
    file = tasks.jar.flatMap { it.archiveFile }
    // The portals show no release title above the notes, so they get the version as a heading
    changelog = rootPublishMods.changelog.map { "## $modVersion\n\n$it" }
    type = STABLE
    // The jar's version, such as "1.0.4+1.21.1-neoforge". It names the loader, since both loaders
    // ship from this project.
    version = project.version.toString()
    modLoaders.add("neoforge")
    dryRun = rootPublishMods.dryRun

    modrinth {
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        projectId = "qWoiYCbs"
        // Titled like the earlier versions, such as "Accessories Compat: Elytra & Totem 1.0.4 for NeoForge 1.21.1"
        displayName = "${prop("portal_name")} $modVersion for NeoForge $releaseMinecraftLabel"
        minecraftVersions.addAll(releaseMinecraftVersions)
        // Needed on both the client and the server, as every earlier version was listed
        environment = ModrinthEnvironment.CLIENT_AND_SERVER
        additionalFile(tasks.named("sourcesJar")) {
            type = SOURCES_JAR
        }
        requires("accessories")
    }

    curseforge {
        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
        projectId = "1690485"
        // Titled with the same version number as on Modrinth, such as "1.0.4+1.21.1-neoforge"
        displayName = project.version.toString()
        minecraftVersions.addAll(releaseMinecraftVersions)
        client = true
        server = true
        changelogType = "markdown"
        // CurseForge files an additional file under the main one it was uploaded with
        additionalFile(tasks.named("sourcesJar")) {
            name = "${project.version}-sources"
        }
        requires("accessories")
    }

    // Only the main jar goes on GitHub, as with the earlier releases. The root project orders these uploads.
    github {
        accessToken = providers.environmentVariable("GITHUB_TOKEN")
        parent(rootProject.tasks.named("publishGithub"))
    }
}

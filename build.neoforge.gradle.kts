plugins {
    id("java-library")
    id("maven-publish")
    id("net.neoforged.moddev") version "2.0.146"
    id("idea")
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

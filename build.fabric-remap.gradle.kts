plugins {
    id("java-library")
    id("maven-publish")
    id("net.fabricmc.fabric-loom-remap") version "1.17.20"
    id("idea")
}

apply(from = rootProject.file("common.gradle.kts"))

fun prop(name: String): String = property(name) as String

repositories {
    // Accessories, owo-lib and endec.
    maven("https://maven.wispforest.io/releases/") { name = "Wisp Forest" }
    // Jankson, a dependency of owo-lib.
    mavenCentral()
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
}

// The other loader's code lives in a package of its own so it can simply be left out, along with the mixin
// config that lists it.
sourceSets.main.get().java.exclude("**/neoforge/**")
tasks.named<ProcessResources>("processResources") {
    exclude(prop("mod_id") + "-neoforge.mixins.json")
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

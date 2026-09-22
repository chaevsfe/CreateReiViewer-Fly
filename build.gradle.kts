import java.util.zip.ZipFile

plugins {
    id("net.fabricmc.fabric-loom") version "1.16-SNAPSHOT"
}

group = property("maven_group") as String
version = "${property("mod_version")}+fabric-mc${property("minecraft_version")}"

base {
    archivesName.set(property("archives_base_name") as String)
}

loom {
    accessWidenerPath = file("src/main/resources/createreiviewer.accesswidener")
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://api.modrinth.com/maven") {
        content { includeGroup("maven.modrinth") }
    }
    maven("https://maven.shedaniel.me/") {
        content {
            includeGroup("me.shedaniel.cloth")
            includeGroup("me.shedaniel.cloth.api")
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    implementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")
    implementation("maven.modrinth:create-fly:${property("create_fabric_version")}")
    implementation("maven.modrinth:rei:${property("rei_version")}")
    implementation("maven.modrinth:architectury-api:${property("architectury_version")}")
    implementation("me.shedaniel.cloth:basic-math:${property("basic_math_version")}")

    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
    withSourcesJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.withType<AbstractCopyTask>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    val expansions = mapOf(
        "version" to project.version.toString(),
        "minecraft_dependency_version" to project.property("minecraft_dependency_version") as String,
        "fabric_loader_version" to project.property("fabric_loader_version") as String,
        "fabric_api_version_range" to project.property("fabric_api_version_range") as String,
        "create_fabric_version_range" to project.property("create_fabric_version_range") as String,
        "rei_version_breaks" to project.property("rei_version_breaks") as String,
    )
    expansions.forEach { (key, value) -> inputs.property(key, value) }
    filesMatching("fabric.mod.json") { expand(expansions) }
}

tasks.jar {
    from("LICENSE")
    from("NOTICE")
}

tasks.named<Jar>("sourcesJar") {
    from("LICENSE")
    from("NOTICE")
}

val jarContainmentPrefix = "dev/chaevsfe/createreiviewer/"

val checkJarContainment = tasks.register("checkJarContainment") {
    description = "Fails if the mod jar or the sources jar carries a class, a source file or a nested jar outside this mod's own namespace."
    group = "verification"
    val archives = files(
        tasks.named<Jar>("jar").flatMap { it.archiveFile },
        tasks.named<Jar>("sourcesJar").flatMap { it.archiveFile }
    )
    dependsOn(tasks.named("jar"), tasks.named("sourcesJar"))
    inputs.files(archives)
    val prefix = jarContainmentPrefix
    doLast {
        val offenders = mutableListOf<String>()
        var own = 0
        archives.forEach { archive ->
            ZipFile(archive).use { zip ->
                for (name in zip.entries().toList().map { it.name }) {
                    if (name.endsWith(".jar")) {
                        offenders += archive.name + " -> nested jar " + name
                        continue
                    }
                    if (!name.endsWith(".class") && !name.endsWith(".java")) continue
                    if (name.startsWith(prefix)) {
                        own++
                        continue
                    }
                    offenders += archive.name + " -> " + name
                }
            }
        }
        if (offenders.isNotEmpty()) {
            throw GradleException(
                "CreateReiViewer must ship nothing outside " + prefix + "; found " + offenders.size +
                    " foreign entries:\n" + offenders.joinToString("\n") { "  " + it }
            )
        }
        logger.lifecycle("checkJarContainment: $own own class/source entries, 0 foreign, 0 nested jars")
    }
}

tasks.named("check") {
    dependsOn(checkJarContainment)
}

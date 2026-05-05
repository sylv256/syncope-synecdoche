import me.modmuss50.mpp.ReleaseType
import org.gradle.kotlin.dsl.publishMods

plugins {
	alias(libs.plugins.fabric.loom)
	alias(libs.plugins.mod.publish)
	`maven-publish`
	checkstyle
}

val mod_version: String by properties
val mod_id: String by properties
val maven_group: String by properties
val release_channel: String by properties
val repo: String by properties
val comitish: String by properties

version = mod_version
group = maven_group

repositories {
	// Add repositories to retrieve artifacts from in here.
	// You should only use this when depending on other mods because
	// Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
	// See https://docs.gradle.org/current/userguide/declaring_repositories.html
	// for more information about repositories.

	forModule(libs.modmenu) {
		name = "TerraformersMC"
		url = uri("https://maven.terraformersmc.com/")
	}

	forGroup(libs.frappe.asProvider()) {
		name = "Sylv"
		url = uri("https://maven.sylv.gay/releases/")
	}

	forModule(libs.fantasy) {
		name = "Nucleoid"
		url = uri("https://maven.nucleoid.xyz/")
	}
}

loom {
	runtimeOnlyLog4j = true
	runtimeOnlyLwjglGraphics = true
	splitEnvironmentSourceSets()

	mods {
		register("syncope-synecdoche") {
			sourceSet(sourceSets.main.get())
			sourceSet(sourceSets.getByName("client"))
		}
	}
}

fabricApi {
	configureDataGeneration {
		client = true
	}
}

dependencies {
	// Base
	minecraft(libs.minecraft)
	implementation(libs.fabric.loader)

	// Utilities
	implementation(libs.jspecify)

	// Libraries
	implementation(libs.fabric.api)
	api(libs.frappe.ext.terrain.material)
	include(libs.frappe.asProvider())
	runtimeOnly(libs.frappe.asProvider())
	implementationInclude(libs.fantasy)

	// Mod Integrations
//	runtimeOnly(libs.sodium) // Disabled because Sodium won't render w/ Mocha yet
	runtimeOnly(libs.modmenu)
}

tasks.processResources {
	inputs.property("version", version)

	filesMatching("fabric.mod.json") {
		expand("version" to version)
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.release = 25
}

apply(projectDir.resolve("gradle").resolve("package-info.gradle"))

tasks.compileJava {
	dependsOn(":generatePackageInfos")
}

tasks.getByName("compileClientJava") {
	dependsOn(":generateClientPackageInfos")
}

java {
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()

	sourceCompatibility = JavaVersion.VERSION_25
	targetCompatibility = JavaVersion.VERSION_25
}

tasks.getByName<Jar>("sourcesJar") {
	dependsOn(":generatePackageInfos")
}

tasks.jar {
	inputs.property("projectName", project.name)

	from("LICENSE") {
		rename { "${it}_${project.name}" }
	}
}

checkstyle {
	configFile = file("${rootProject.projectDir}/checkstyle.xml")
}

// configure the maven publication
publishing {
	publications {
		register<MavenPublication>("mavenJava") {
			from(components["java"])
		}
	}

	// See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
	repositories {
		// Add repositories to publish to here.
		// Notice: This block does NOT have the same function as the block in the top level.
		// The repositories here will be used for publishing your artifact, not for
		// retrieving dependencies.
	}
}

publishMods {
	changelog = rootProject.file("CHANGELOG.md").readText()
	displayName = "v$mod_version (Minecraft ${libs.minecraft.get().version})"
	version = mod_version
	type = when(release_channel) {
		"alpha" -> ReleaseType.ALPHA
		"beta" -> ReleaseType.BETA
		"stable" -> ReleaseType.STABLE
		else -> throw AssertionError("No release_channel specified")
	}

	github {
		accessToken = providers.environmentVariable("GITHUB_TOKEN")
		repository = repo
		tagName = "v$mod_version"
		commitish = comitish

		allowEmptyFiles = true
	}
}

fun RepositoryHandler.forMaven(
	groups: List<String?>? = null,
	modules: List<String?>? = null,
	inner: Action<MavenArtifactRepository>
) {
	exclusiveContent {
		forRepository {
			maven(inner)
		}

		filter {
			if (groups != null) {
				for ((i, element) in groups.withIndex()) {
					val group = element
					val module = modules?.get(i)

					if (group != null) {
						if (module != null) {
							includeModule(group, module)
						} else {
							includeGroup(group)
						}
					}
				}
			}
		}
	}
}

fun <T : ExternalDependency> RepositoryHandler.forGroup(vararg dependencies: Provider<T>, inner: Action<MavenArtifactRepository>) {
	val modules = dependencies.map { it.get().module }
	forMaven(
		groups = modules.map { it.group },
		inner = inner
	)
}

fun <T : ExternalDependency> RepositoryHandler.forModule(vararg dependencies: Provider<T>, inner: Action<MavenArtifactRepository>) {
	val modules = dependencies.map { it.get().module }
	forMaven(
		groups = modules.map { it.group },
		modules = modules.map { it.name },
		inner = inner
	)
}

/**
 * implementationInclude for modern Gradle
 */
fun DependencyHandler.implementationInclude(dependencyNotation: Any): Dependency? {
	val a = implementation(dependencyNotation)
	val b = include(dependencyNotation)
	if (a != b) throw AssertionError()
	return a
}

fun DependencyHandler.apiInclude(dependencyNotation: Any): Dependency? {
	val a = api(dependencyNotation)
	val b = include(dependencyNotation)
	if (a != b) throw AssertionError()
	return a
}

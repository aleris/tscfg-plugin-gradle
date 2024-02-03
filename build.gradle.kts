import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

plugins {
  kotlin("jvm")
  id("java-gradle-plugin")
  id("maven-publish")
  id("org.jetbrains.dokka")
  id("com.gradle.plugin-publish") version "1.2.1"
}

plugins.withType<JavaPlugin> {
  configure<JavaPluginExtension> {
    withSourcesJar()
    withJavadocJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    toolchain {
      languageVersion = JavaLanguageVersion.of(8)
    }
  }
}

plugins.withId("org.jetbrains.kotlin.jvm") {

  configure<KotlinJvmProjectExtension> {

    jvmToolchain(8)

    compilerOptions {
      freeCompilerArgs.add("-Xjvm-default=all")
    }
  }

  configurations.all {
    resolutionStrategy.eachDependency {
      if (requested.group == "org.jetbrains.kotlin") {
        useVersion(embeddedKotlinVersion)
      }
    }
  }

  dependencies {
    compileOnly(kotlin("stdlib"))

    implementation(libs.tscfg)

    testImplementation(kotlin("stdlib"))
    testImplementation(kotlin("reflect"))

    testImplementation(libs.bundles.test)
  }

  tasks.withType<Test>().configureEach {
    // always execute tests
    outputs.upToDateWhen { false }

    useJUnitPlatform()

    testLogging.showStandardStreams = true
  }
}

plugins.withId("org.jetbrains.dokka") {

  val dokkaVersion: String by extra
  dependencies {
    "dokkaJavadocPlugin"("org.jetbrains.dokka:kotlin-as-java-plugin:$dokkaVersion")
  }

  tasks.withType<Jar>().matching { it.name == "javadocJar" || it.name == "publishPluginJavaDocsJar" }
    .all {
      from(tasks.named("dokkaJavadoc"))
    }

  tasks.withType<org.jetbrains.dokka.gradle.DokkaTask> {
    dokkaSourceSets.all {
      externalDocumentationLink {
        url.set(uri("https://docs.oracle.com/javase/8/docs/api/").toURL())
      }
      reportUndocumented.set(false)

      val sourceSetName = this.name
      val githubUrl = project.extra["github.url"] as String

      sourceLink {
        localDirectory.set(project.file("src/$sourceSetName/kotlin"))
        remoteUrl.set(
          uri("$githubUrl/blob/v${project.version}/${project.projectDir.relativeTo(rootDir)}/src/$sourceSetName/kotlin").toURL()
        )
        remoteLineSuffix.set("#L")
      }
    }
  }

  plugins.withType<JavaGradlePluginPlugin> {
    tasks.withType<org.jetbrains.dokka.gradle.DokkaTask> {
      dokkaSourceSets.all {
        externalDocumentationLink {
          url.set(uri("https://docs.gradle.org/current/javadoc/").toURL())
        }
        externalDocumentationLink {
          url.set(uri("https://docs.groovy-lang.org/latest/html/groovy-jdk/").toURL())
        }
      }
    }
  }
}

plugins.withType<JavaGradlePluginPlugin> {

  val githubUrl = project.extra["github.url"] as String

  @Suppress("UnstableApiUsage")
  with(the<GradlePluginDevelopmentExtension>()) {
    website.set(githubUrl)
    vcsUrl.set(githubUrl)
    isAutomatedPublishing = true
  }
}

gradlePlugin {
  plugins.create("tscfgPlugin") {
      id = "io.github.aleris.plugins.tscfg"
      implementationClass = "io.github.aleris.plugins.tscfg.TscfgPlugin"
  }
}

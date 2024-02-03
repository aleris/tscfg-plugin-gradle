package io.github.aleris.plugins.tscfg

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.io.File

class TscfgPluginTest : FunSpec({
  fun build(dir: File) = GradleRunner.create()
    .withProjectDir(dir)
    .withArguments("generateTscfg")
    .withPluginClasspath()
    .build()

  test("plugin creates generateTscfg task and tscfg extension") {
    val project = ProjectBuilder.builder().build()
    project.plugins.apply("io.github.aleris.plugins.tscfg")

    project.tasks.findByName("generateTscfg") shouldNotBe null
    project.extensions.findByName("tscfg") shouldNotBe null
  }

  test("simple kotlin dsl") {
    val testProjectDir = tempDir()

    testProjectDir.resolve("src/tscfg/application.spec.conf").also {
      it.parentFile.mkdirs()
      it.writeText("""
      # Configuration for API calls
      api {
        # The URI of the API
        #@envvar API_URI
        uri: "string | http://localhost:8080",
      }
      """.trimIndent())
    }

    testProjectDir.resolve("build.gradle.kts").also {
      it.writeText("""
        plugins {
          id("java")
          id("io.github.aleris.plugins.tscfg")
        }
  
        tscfg {
          packageName = "io.github.aleris.plugins.tscfg.example"
          
          files {
            register("src/tscfg/application.spec.conf") {
              outputConfigFileName = "src/main/resources/application.conf"
            }
          }
          
          generateRecords = true
          addGeneratedAnnotation = true
        }
      """.trimIndent())
    }

    val result = build(testProjectDir)

    result.output shouldContain "BUILD SUCCESSFUL"
    result.task(":generateTscfg")?.outcome shouldBe TaskOutcome.SUCCESS

    val configFile = testProjectDir.resolve("src/main/resources/application.conf")
    configFile.exists() shouldBe true
    configFile.readText() shouldContain "uri = \${?API_URI}"

    val root = "build/${TscfgPlugin.GENERATED_SOURCES_ROOT}/java"

    val classFile = testProjectDir.resolve(
      "$root/io/github/aleris/plugins/tscfg/example/ApplicationConfig.java"
    )
    classFile.exists() shouldBe true
    classFile.readText() shouldContain "package io.github.aleris.plugins.tscfg.example;"
    classFile.readText() shouldContain "public record ApplicationConfig("
  }

  test("simple gradle dsl") {
    val testProjectDir = tempDir()

    testProjectDir.resolve("src/tscfg/application.spec.conf").also {
      it.parentFile.mkdirs()
      it.writeText("""
      # Configuration for API calls
      api {
        # The URI of the API
        #@envvar API_URI
        uri: "string | http://localhost:8080",
      }
      """.trimIndent())
    }

    testProjectDir.resolve("build.gradle").also {
      it.writeText("""
        plugins {
          id 'java'
          id 'io.github.aleris.plugins.tscfg'
        }
  
        tscfg {
          packageName = 'io.github.aleris.plugins.tscfg.example'
          
          files {
            'src/tscfg/application.spec.conf' {
              outputConfigFileName = 'src/main/resources/application.conf'
            }
          }
          
          generateRecords = true
          addGeneratedAnnotation = true
        }
      """.trimIndent())
    }

    val result = build(testProjectDir)

    result.output shouldContain "BUILD SUCCESSFUL"

    val firstConfigFile = testProjectDir.resolve("src/main/resources/application.conf")
    firstConfigFile.exists() shouldBe true
    firstConfigFile.readText() shouldContain "uri = \${?API_URI}"

    val root = "build/${TscfgPlugin.GENERATED_SOURCES_ROOT}/java"

    val firstClassFile = testProjectDir.resolve(
      "$root/io/github/aleris/plugins/tscfg/example/ApplicationConfig.java"
    )
    firstClassFile.exists() shouldBe true
    firstClassFile.readText() shouldContain "package io.github.aleris.plugins.tscfg.example;"
    firstClassFile.readText() shouldContain "public record ApplicationConfig("
  }

  test("complex with multiple files and different packages") {
    val testProjectDir = tempDir()
    testProjectDir.resolve("src/tscfg/application.spec.conf").also {
      it.parentFile.mkdirs()
      it.writeText("""
      # Configuration for API calls
      api {
        # The URI of the API
        #@envvar API_URI
        uri: "string | http://localhost:8080",
      }
      """.trimIndent())
    }

    testProjectDir.resolve("src/tscfg/second.spec.conf").also {
      it.parentFile.mkdirs()
      it.writeText("""
      # Configuration for API calls
      api2 {
        # The URI of the API
        #@envvar API2_URI2
        uri2: "string | http://localhost:8080",
      }
      """.trimIndent())
    }

    testProjectDir.resolve("src/tscfg/third.spec.conf").also {
      it.parentFile.mkdirs()
      it.writeText("""
      # Configuration for API calls
      api3 {
        # The URI of the API
        #@envvar API3_URI3
        uri3: "string | http://localhost:8080",
      }
      """.trimIndent())
    }

    testProjectDir.resolve("build.gradle.kts").also {
      it.writeText("""
        plugins {
          id("java")
          id("io.github.aleris.plugins.tscfg")
        }
  
        tscfg {
          packageName = "io.github.aleris.plugins.tscfg.example"

          files {
            register("src/tscfg/application.spec.conf") {
              outputConfigFileName = "src/main/resources/application.conf"
              className = "FirstConfig"
            }
          
            register("src/tscfg/second.spec.conf")
            
            register("src/tscfg/third.spec.conf") {
              packageName = "io.github.aleris.plugins.tscfg.example.third"
            }
          }

          generateRecords = true
          addGeneratedAnnotation = true
        }
      """.trimIndent())
    }

    val result = build(testProjectDir)

    result.output shouldContain "BUILD SUCCESSFUL"

    val firstConfigFile = testProjectDir.resolve("src/main/resources/application.conf")
    firstConfigFile.exists() shouldBe true
    firstConfigFile.readText() shouldContain "uri = \${?API_URI}"

    val root = "build/${TscfgPlugin.GENERATED_SOURCES_ROOT}/java"

    // first
    val firstClassFile = testProjectDir.resolve(
      "$root/io/github/aleris/plugins/tscfg/example/FirstConfig.java"
    )
    firstClassFile.exists() shouldBe true
    firstClassFile.readText() shouldContain "package io.github.aleris.plugins.tscfg.example;"
    firstClassFile.readText() shouldContain "public record FirstConfig("

    // second
    val secondConfigFile = testProjectDir.resolve("src/tscfg/second.conf")
    secondConfigFile.exists() shouldBe true
    secondConfigFile.readText() shouldContain "uri2 = \${?API2_URI2}"

    val secondClassFile = testProjectDir.resolve(
      "$root/io/github/aleris/plugins/tscfg/example/SecondConfig.java"
    )
    secondClassFile.exists() shouldBe true
    firstClassFile.readText() shouldContain "package io.github.aleris.plugins.tscfg.example;"
    secondClassFile.readText() shouldContain "public record SecondConfig("

    // third
    val thirdConfigFile = testProjectDir.resolve("src/tscfg/third.conf")
    thirdConfigFile.exists() shouldBe true
    thirdConfigFile.readText() shouldContain "uri3 = \${?API3_URI3}"

    val thirdClassFile = testProjectDir.resolve(
      "$root/io/github/aleris/plugins/tscfg/example/third/ThirdConfig.java"
    )
    thirdClassFile.exists() shouldBe true
    thirdClassFile.readText() shouldContain "package io.github.aleris.plugins.tscfg.example.third;"
    thirdClassFile.readText() shouldContain "public record ThirdConfig("
  }

  test("no config file generated") {
    val testProjectDir = tempDir()

    testProjectDir.resolve("src/tscfg/application.spec.conf").also {
      it.parentFile.mkdirs()
      it.writeText("""
      # Configuration for API calls
      api {
        # The URI of the API
        #@envvar API_URI
        uri: "string | http://localhost:8080",
      }
      """.trimIndent())
    }

    testProjectDir.resolve("build.gradle.kts").also {
      it.writeText("""
        plugins {
          id("java")
          id("io.github.aleris.plugins.tscfg")
        }
  
        tscfg {
          packageName = "io.github.aleris.plugins.tscfg.example"
          generateConfigFile = false
          
          files {
            register("src/tscfg/application.spec.conf") {
              outputConfigFileName = "src/main/resources/application.conf"
            }
          }
        }
      """.trimIndent())
    }

    val result = build(testProjectDir)

    result.output shouldContain "BUILD SUCCESSFUL"
    result.task(":generateTscfg")?.outcome shouldBe TaskOutcome.SUCCESS

    val configFile = testProjectDir.resolve("src/main/resources/application.conf")
    configFile.exists() shouldBe false

    val root = "build/${TscfgPlugin.GENERATED_SOURCES_ROOT}/java"

    val classFile = testProjectDir.resolve(
      "$root/io/github/aleris/plugins/tscfg/example/ApplicationConfig.java"
    )
    classFile.exists() shouldBe true
  }

  test("output in main source set") {
    val testProjectDir = tempDir()

    testProjectDir.resolve("src/tscfg/application.spec.conf").also {
      it.parentFile.mkdirs()
      it.writeText("""
      # Configuration for API calls
      api {
        # The URI of the API
        #@envvar API_URI
        uri: "string | http://localhost:8080",
      }
      """.trimIndent())
    }

    testProjectDir.resolve("build.gradle.kts").also {
      it.writeText("""
        plugins {
          id("java")
          id("io.github.aleris.plugins.tscfg")
        }
  
        tscfg {
          packageName = "io.github.aleris.plugins.tscfg.example"
          outputInGeneratedResourceSet = false
          
          files {
            register("src/tscfg/application.spec.conf")
          }
        }
      """.trimIndent())
    }

    val result = build(testProjectDir)

    result.output shouldContain "BUILD SUCCESSFUL"
    result.task(":generateTscfg")?.outcome shouldBe TaskOutcome.SUCCESS

    val root = "src/main/java"

    val classFile = testProjectDir.resolve(
      "$root/io/github/aleris/plugins/tscfg/example/ApplicationConfig.java"
    )
    classFile.exists() shouldBe true
  }
})

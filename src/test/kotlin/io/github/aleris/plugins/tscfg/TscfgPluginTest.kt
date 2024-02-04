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

  test("run build basic config with kotlin dsl") {
    val testProjectDir = tempDir()

    testProjectDir.resolve("src/tscfg/application.spec.conf").also {
      it.parentFile.mkdirs()
      it.writeText(TestFixture.CONFIG_SPEC)
    }

    testProjectDir.resolve("build.gradle.kts").also {
      it.writeText("""
        plugins {
          id("java")
          id("io.github.aleris.plugins.tscfg")
        }
  
        tscfg {
          packageName = "com.example"          
          generateRecords = true
          addGeneratedAnnotation = true
          
          files {
            register("application") {
              specFile = file("src/tscfg/application.spec.conf")
              configFile = file("src/main/resources/application.conf")
            }
          }
        }
      """.trimIndent())
    }

    val result = build(testProjectDir)

    result.task(":generateTscfg")?.outcome shouldBe TaskOutcome.SUCCESS

    val configFile = testProjectDir.resolve("src/main/resources/application.conf")
    configFile.exists() shouldBe true
    configFile.readText() shouldContain "uri = \${?API_URI}"

    val root = "build/${TscfgPlugin.GENERATED_SOURCES_ROOT}/java"

    val classFile = testProjectDir.resolve(
      "$root/com/example/ApplicationConfig.java"
    )
    classFile.exists() shouldBe true
    classFile.readText() shouldContain "package com.example;"
    classFile.readText() shouldContain "public record ApplicationConfig("
  }

  test("run build basic config with gradle dsl") {
    val testProjectDir = tempDir()

    testProjectDir.resolve("src/tscfg/application.spec.conf").also {
      it.parentFile.mkdirs()
      it.writeText(TestFixture.CONFIG_SPEC)
    }

    testProjectDir.resolve("build.gradle").also {
      it.writeText("""
        plugins {
          id 'java'
          id 'io.github.aleris.plugins.tscfg'
        }
  
        tscfg {
          packageName = 'com.example'
          generateRecords = true
          addGeneratedAnnotation = true
          
          files {
            'application' {
              specFile = file('src/tscfg/application.spec.conf')
              configFile = file('src/main/resources/application.conf')
            }
          }          
        }
      """.trimIndent())
    }

    val result = build(testProjectDir)

    result.task(":generateTscfg")?.outcome shouldBe TaskOutcome.SUCCESS

    val firstConfigFile = testProjectDir.resolve("src/main/resources/application.conf")
    firstConfigFile.exists() shouldBe true
    firstConfigFile.readText() shouldContain "uri = \${?API_URI}"

    val root = "build/${TscfgPlugin.GENERATED_SOURCES_ROOT}/java"

    val firstClassFile = testProjectDir.resolve(
      "$root/com/example/ApplicationConfig.java"
    )
    firstClassFile.exists() shouldBe true
    firstClassFile.readText() shouldContain "package com.example;"
    firstClassFile.readText() shouldContain "public record ApplicationConfig("
  }

  test("run build with multiple files and different packages") {
    val testProjectDir = tempDir()
    testProjectDir.resolve("src/tscfg/application.spec.conf").also {
      it.parentFile.mkdirs()
      it.writeText(TestFixture.CONFIG_SPEC)
    }

    testProjectDir.resolve("src/tscfg/second.spec.conf").also {
      it.parentFile.mkdirs()
      it.writeText(TestFixture.CONFIG_SPEC)
    }

    testProjectDir.resolve("src/tscfg/third.spec.conf").also {
      it.parentFile.mkdirs()
      it.writeText(TestFixture.CONFIG_SPEC)
    }

    testProjectDir.resolve("build.gradle.kts").also {
      it.writeText("""
        plugins {
          id("java")
          id("io.github.aleris.plugins.tscfg")
        }
  
        tscfg {
          packageName = "com.example"
          generateRecords = true
          addGeneratedAnnotation = true

          files {
            register("application") {
              specFile = file("src/tscfg/application.spec.conf")
              configFile = file("src/main/resources/application.conf")
              className = "FirstConfig"
            }
          
            register("second") {
              specFile = file("src/tscfg/second.spec.conf")
            }
            
            register("third") {
              packageName = "com.example.third"
            }
          }
        }
      """.trimIndent())
    }

    val result = build(testProjectDir)

    result.task(":generateTscfg")?.outcome shouldBe TaskOutcome.SUCCESS

    val firstConfigFile = testProjectDir.resolve("src/main/resources/application.conf")
    firstConfigFile.exists() shouldBe true
    firstConfigFile.readText() shouldContain "uri = \${?API_URI}"

    val root = "build/${TscfgPlugin.GENERATED_SOURCES_ROOT}/java"

    // first
    val firstClassFile = testProjectDir.resolve(
      "$root/com/example/FirstConfig.java"
    )
    firstClassFile.exists() shouldBe true
    firstClassFile.readText() shouldContain "package com.example;"
    firstClassFile.readText() shouldContain "public record FirstConfig("

    // second
    val secondConfigFile = testProjectDir.resolve("src/tscfg/second.conf")
    secondConfigFile.exists() shouldBe true
    secondConfigFile.readText() shouldContain "uri = \${?API_URI}"

    val secondClassFile = testProjectDir.resolve(
      "$root/com/example/SecondConfig.java"
    )
    secondClassFile.exists() shouldBe true
    firstClassFile.readText() shouldContain "package com.example;"
    secondClassFile.readText() shouldContain "public record SecondConfig("

    // third
    val thirdConfigFile = testProjectDir.resolve("src/tscfg/third.conf")
    thirdConfigFile.exists() shouldBe true
    thirdConfigFile.readText() shouldContain "uri = \${?API_URI}"

    val thirdClassFile = testProjectDir.resolve(
      "$root/com/example/third/ThirdConfig.java"
    )
    thirdClassFile.exists() shouldBe true
    thirdClassFile.readText() shouldContain "package com.example.third;"
    thirdClassFile.readText() shouldContain "public record ThirdConfig("
  }

  test("run build without a config file generated") {
    val testProjectDir = tempDir()

    testProjectDir.resolve("src/tscfg/application.spec.conf").also {
      it.parentFile.mkdirs()
      it.writeText(TestFixture.CONFIG_SPEC)
    }

    testProjectDir.resolve("build.gradle.kts").also {
      it.writeText("""
        plugins {
          id("java")
          id("io.github.aleris.plugins.tscfg")
        }
  
        tscfg {
          packageName = "com.example"
          generateConfigFile = false
        }
      """.trimIndent())
    }

    val result = build(testProjectDir)

    result.task(":generateTscfg")?.outcome shouldBe TaskOutcome.SUCCESS

    val configFile = testProjectDir.resolve("src/main/resources/application.conf")
    configFile.exists() shouldBe false

    val root = "build/${TscfgPlugin.GENERATED_SOURCES_ROOT}/java"

    val classFile = testProjectDir.resolve(
      "$root/com/example/ApplicationConfig.java"
    )
    classFile.exists() shouldBe true
  }

  test("run build with output class in main source set") {
    val testProjectDir = tempDir()

    testProjectDir.resolve("src/tscfg/application.spec.conf").also {
      it.parentFile.mkdirs()
      it.writeText(TestFixture.CONFIG_SPEC)
    }

    testProjectDir.resolve("build.gradle.kts").also {
      it.writeText("""
        plugins {
          id("java")
          id("io.github.aleris.plugins.tscfg")
        }
  
        tscfg {
          packageName = "com.example"
          outputInGeneratedResourceSet = false
        }
      """.trimIndent())
    }

    val result = build(testProjectDir)

    result.task(":generateTscfg")?.outcome shouldBe TaskOutcome.SUCCESS

    val root = "src/main/java"

    val classFile = testProjectDir.resolve(
      "$root/com/example/ApplicationConfig.java"
    )
    classFile.exists() shouldBe true
  }
})

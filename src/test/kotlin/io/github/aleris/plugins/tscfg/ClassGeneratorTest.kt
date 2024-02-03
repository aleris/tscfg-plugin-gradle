package io.github.aleris.plugins.tscfg

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain

class ClassGeneratorTest : FunSpec({

  val testFixture = TestFixture()

  test("generate") {
    val configSource = testFixture.sourceConfigFileReader.read(testFixture.configFile)

    val code = ClassGenerator(testFixture.generateTscfgTask).generate(configSource)

    code shouldContain "/* Generated with `generateTscfg` Gradle task */"
    code shouldContain "package ${TestFixture.PACKAGE_NAME};"
  }

  test("generate with annotation") {
    testFixture.configFile.className.set("TestConfig")
    val configSource = testFixture.sourceConfigFileReader.read(testFixture.configFile)
    testFixture.generateTscfgTask.addGeneratedAnnotation.set(true)

    val code = ClassGenerator(testFixture.generateTscfgTask).generate(configSource)

    code shouldContain "@jakarta.annotation.Generated("
  }

  test("generate without annotation") {
    val configSource = testFixture.sourceConfigFileReader.read(testFixture.configFile)
    testFixture.generateTscfgTask.addGeneratedAnnotation.set(false)

    val code = ClassGenerator(testFixture.generateTscfgTask).generate(configSource)

    code shouldContain "/* Generated with `generateTscfg` Gradle task */"
    code shouldNotContain "@jakarta.annotation.Generated("
  }

  test("generate class") {
    testFixture.configFile.className.set("TestConfig")
    testFixture.generateTscfgTask.generateRecords.set(false)
    testFixture.generateTscfgTask.generateGetters.set(false)
    val configSource = testFixture.sourceConfigFileReader.read(testFixture.configFile)
    val code = ClassGenerator(testFixture.generateTscfgTask).generate(configSource)
    code shouldContain "public class TestConfig {"
    code shouldNotContain "getUri()"
  }

  test("generate getters") {
    testFixture.configFile.className.set("TestConfig")
    testFixture.generateTscfgTask.generateRecords.set(false)
    testFixture.generateTscfgTask.generateGetters.set(true)
    val configSource = testFixture.sourceConfigFileReader.read(testFixture.configFile)
    val code = ClassGenerator(testFixture.generateTscfgTask).generate(configSource)
    code shouldContain "public class TestConfig {"
    code shouldContain "getUri()"
  }

  test("generate records") {
    testFixture.configFile.className.set("TestConfig")
    testFixture.generateTscfgTask.generateRecords.set(true)
    val configSource = testFixture.sourceConfigFileReader.read(testFixture.configFile)
    val code = ClassGenerator(testFixture.generateTscfgTask).generate(configSource)
    code shouldContain "public record TestConfig("
  }

  test("task") { }
})

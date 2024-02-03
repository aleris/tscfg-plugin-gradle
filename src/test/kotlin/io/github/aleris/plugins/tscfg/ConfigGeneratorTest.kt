package io.github.aleris.plugins.tscfg

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain

class ConfigGeneratorTest : FunSpec({

  val testFixture = TestFixture()

  test("generate") {
    testFixture.generateTscfgTask.configFileIndent.set("  ")
    val configSource = SourceConfigFileReader(testFixture.project).read(testFixture.configFile)
    val code = ConfigGenerator(testFixture.generateTscfgTask).generate(configSource)
    code shouldContain "## Generated with `generateTscfg` Gradle task"
    code shouldContain "  uri = \${?API_URI}"
  }

  test("generate with bigger indent") {
    testFixture.generateTscfgTask.configFileIndent.set("    ")
    val configSource = SourceConfigFileReader(testFixture.project).read(testFixture.configFile)
    val code = ConfigGenerator(testFixture.generateTscfgTask).generate(configSource)
    code shouldContain "## Generated with `generateTscfg` Gradle task"
    code shouldContain "    uri = \${?API_URI}"
  }

  test("task") {
    ConfigGenerator(testFixture.generateTscfgTask).task shouldNotBe null
  }
})

package io.github.aleris.plugins.tscfg

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class ClassWriterTest : FunSpec({

  val testFixture = TestFixture()

  test("write in generated resource set") {
    testFixture.configFile.className.set("TestConfig")
    testFixture.generateTscfgTask.outputInGeneratedResourceSet.set(true)
    val configSource = testFixture.sourceConfigFileReader.read(testFixture.configFile)
    val classGenerator = ClassGenerator(testFixture.generateTscfgTask)

    ClassWriter(classGenerator).write(configSource)

    val generatedFile = testFixture.testProjectDir.resolve(
      "build/${TscfgPlugin.GENERATED_SOURCES_ROOT}/java/${TestFixture.PACKAGE_PATH}/TestConfig.java"
    )

    generatedFile.exists() shouldBe true
    generatedFile.readText() shouldContain "package ${TestFixture.PACKAGE_NAME};"
  }
})

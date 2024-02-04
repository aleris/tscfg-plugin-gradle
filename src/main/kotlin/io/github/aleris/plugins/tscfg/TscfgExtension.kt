package io.github.aleris.plugins.tscfg

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

/**
 * The tscfg plugin extension.
 * This is the main configuration block for the tscfg plugin.
 */
open class TscfgExtension @Inject constructor(project: Project) {
  private val objects: ObjectFactory = project.objects

  /**
   * The configuration files to be processed by the tscfg plugin.
   * Multiple files can be added inside the `tscfg.files` block.
   * If not specified, the default configuration file "src/tscfg/application.spec.conf" will be used.
   */
  val files: NamedDomainObjectContainer<ConfigFile> = objects.domainObjectContainer(ConfigFile::class.java) { name ->
    objects.newInstance(ConfigFile::class.java, name, this, project)
  }

  /**
   * The package name of the generated config classes.
   * Defaults to a name derived from the Gradle project `group` property.
   * Applies to all generated classes.
   */
  val packageName: Property<String> = objects.property(String::class.java).convention(
    packageNameFromGroup(
      project.group.toString()
    )
  )

  /**
   * Whether to generate the configuration file from the specification file.
   * If set to `false`, only the config class will be generated.
   * Defaults to `true`.
   */
  val generateConfigFile: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

  /**
   * The indentation used in the generated configuration file.
   * Defaults to two spaces.
   */
  val configFileIndent: Property<String> = objects.property(String::class.java).convention("  ")

  /**
   * Whether to include the generated configuration file in the generated resources set.
   * Defaults to `true`.
   *
   * If set to `true`, the configuration file will be generated in project path
   * `build/generated/sources/io/github/aleris/tscfg/java/<package>/<className>.java`.
   * The tscfg directory in generated sources is included in the main java resources set.
   *
   * If set to `false`, the configuration file will be generated in project path
   * `src/main/java/<package>/<className>.java`.
   */
  val outputInGeneratedResourceSet: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

  /**
   * Whether to include the `jakarta.annotation.Generated` annotation in the generated config class.
   * Defaults to `false`.
   *
   * If set to `true`, the annotation will be included in the generated config class.
   * The jakarta dependency is required to use this annotation, and you must include it in your build.
   */
  val addGeneratedAnnotation: Property<Boolean> = objects.property(Boolean::class.java).convention(false)

  /**
   * Whether to generate getters for the config class.
   * Defaults to `false`.
   * Mutually exclusive with `generateRecords`.
   */
  val generateGetters: Property<Boolean> = objects.property(Boolean::class.java).convention(false)

  /**
   * Whether to generate records for the config class.
   * Defaults to `false`.
   * Mutually exclusive with `generateGetters`.
   */
  val generateRecords: Property<Boolean> = objects.property(Boolean::class.java).convention(false)

  /**
   * Whether to use `java.util.Optional` for optional fields in the config class.
   * Defaults to `false`.
   */
  val useOptionals: Property<Boolean> = objects.property(Boolean::class.java).convention(false)

  /**
   * Whether to use `java.time.Duration` for duration fields in the config class.
   * Defaults to `true`.
   */
  val useDurations: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

  companion object {
    /**
     * Derives a package name from the Gradle project group.
     */
    private fun packageNameFromGroup(group: String) = group.replace("[^a-zA-Z-]+", ".").lowercase()
  }
}

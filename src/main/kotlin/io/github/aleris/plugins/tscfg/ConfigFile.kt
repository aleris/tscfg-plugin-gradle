package io.github.aleris.plugins.tscfg

import groovy.json.StringEscapeUtils
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import javax.inject.Inject

/**
 * Represents a configuration file for the tscfg plugin.
 */
open class ConfigFile
/**
 * Constructs a new [ConfigFile] instance.
 *
 * @param name the name acting as a label of the configuration file
 * @param tscfgExtension the tscfg extension used for some of the default values
 * @param project the Gradle project
 */
@Inject constructor(val name: String, tscfgExtension: TscfgExtension, project: Project) {
  private val objects: ObjectFactory = project.objects

  /**
   * The specification or template file used to generate both the configuration file and the config class.
   * The default value is `src/tscfg/<name>.spec.conf` if not specified.
   */
  @get:Input
  val specFile: RegularFileProperty =
    objects.fileProperty().convention(project.layout.projectDirectory.file("src/tscfg/${name}.spec.conf"))

  /**
   * The configuration file that will be generated from the specification file.
   * The default value is `src/main/resources/<name>.conf` if not specified.
   */
  @get:Input
  val configFile: RegularFileProperty = objects.fileProperty()
    .convention(specFile.map { project.layout.projectDirectory.file(configFilePath(it.asFile.path)) })

  /**
   * The package name of the generated config class.
   * Can be used to override the global package name in the upper tscfg block of the configuration.
   */
  @get:Input
  val packageName: Property<String> =
    objects.property(String::class.java).convention(tscfgExtension.packageName.getOrElse(""))

  /**
   * The class name of the generated config class.
   * The default value is derived from the configuration file path.
   * Default is `ApplicationConfig` if not specified.
   *
   * Examples:
   * - `src/tscfg/application.spec.conf` -> `ApplicationConfig`
   * - `src/tscfg/another.spec.conf` -> `AnotherConfig`
   * - `src/tscfg/another.spec.conf` -> `AnotherConfig`
   */
  @get:Input
  val className: Property<String> =
    objects.property(String::class.java).convention(configFile.map { classNameFromConfigFilePath(it.asFile.path) })

  companion object {
    /**
     * Returns the configuration file path from the specification file path
     * by stripping `.spec` or `.template` from the name.
     * Used as default value for the `configFile` property if not specified.
     *
     * @param specFilePath the specification file path
     * @return the configuration file path
     */
    private fun configFilePath(specFilePath: String) =
      specFilePath.replace(".spec.conf", ".conf").replace(".template.conf", ".conf")

    /**
     * Returns a java class name from the configuration file path.
     * Used as default value for the `className` property if not specified.
     * Uses the name of the file without the extension and the first letter capitalized and appends `Config`.
     */
    private fun classNameFromConfigFilePath(path: String) = StringEscapeUtils.escapeJava(path
      .substringAfterLast("/")
      .substringBefore(".")
      .replaceFirstChar { it.uppercase() } + "Config"
    )
  }
}

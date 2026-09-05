import org.jetbrains.amper.plugins.*
import java.io.File
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createParentDirectories
import kotlin.io.path.div
import kotlin.io.path.exists

fun graalBin(): Path {
  val javaHome =
      requireNotNull(System.getenv("JAVA_HOME")) {
        "JAVA_HOME environment variable is not set. Set it to a GraalVM distribution."
      }
  val bin = Path.of(javaHome) / "bin"
  require((bin / nativeImageExecutableName()).exists()) {
    "native-image not found in $bin. Ensure JAVA_HOME points to a GraalVM distribution."
  }
  require((bin / javaExecutableName()).exists()) {
    "java not found in $bin. Ensure JAVA_HOME points to a GraalVM distribution."
  }
  return bin
}

@TaskAction
fun buildNativeImage(
    @Input classpath: Classpath,
    mainClass: String,
    additionalArgs: List<String> = emptyList(),
    @Output output: Path,
) {
  val nativeImageExe = graalBin() / nativeImageExecutableName()

  output.createParentDirectories()

  val command = buildList {
    add(nativeImageExe.toString())
    add("-cp")
    add(classpath.resolvedFiles.joinToString(File.pathSeparator))
    add(mainClass)
    add("-o")
    add(output.toString())
    add("--no-fallback")
    addAll(additionalArgs)
  }

  val process = ProcessBuilder(command).inheritIO().start()

  val exitCode = process.waitFor()
  require(exitCode == 0) { "native-image build failed with exit code $exitCode" }
}

@TaskAction
fun runTracingAgent(
    @Input classpath: Classpath,
    mainClass: String,
    @Output resources: ModuleSources,
) {
  val javaExe = graalBin() / javaExecutableName()

  val outputDir = resources.sourceDirectories.first() / "META-INF/native-image/"
  outputDir.createDirectories()

  val command = buildList {
    add(javaExe.toString())
    add("-agentlib:native-image-agent=config-output-dir=$outputDir")
    add("-cp")
    add(classpath.resolvedFiles.joinToString(File.pathSeparator))
    add(mainClass)
  }

  val process = ProcessBuilder(command).inheritIO().start()

  val exitCode = process.waitFor()
  require(exitCode == 0) { "Tracing agent failed with exit code $exitCode" }
}

@TaskAction
fun graalVMCheck(moduleName: String) {
  graalBin()
}

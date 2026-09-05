import org.jetbrains.amper.plugins.Configurable

@Configurable
interface Settings {
  val version: String
    get() = "25"

  val distribution: GraalVmDistribution
    get() = GraalVmDistribution.COMMUNITY

  val acknowledgeOracleLicense: Boolean
    get() = false

  val mainClass: String

  val additionalArgs: List<String>
    get() = emptyList()
}

enum class GraalVmDistribution {
  ORACLE,
  COMMUNITY,
}

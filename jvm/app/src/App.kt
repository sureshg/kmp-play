import ai.tts
import dev.suresh.http.ErrorStatus
import dev.suresh.http.json
import ffm.PosixUser
import ffm.vectorApi
import io.roastedroot.lumis4j.core.Lang
import io.roastedroot.lumis4j.core.Lumis
import io.roastedroot.lumis4j.core.Theme
import kotlinx.schema.generator.json.JsonSchemaConfig
import kotlinx.schema.generator.json.serialization.SerializationClassJsonSchemaGenerator
import kotlinx.schema.json.encodeToString

suspend fun main() {
  System.setProperty("slf4j.internal.verbosity", "WARN")
  println(PosixUser)
  mediaClient()
  vectorApi()
  jsonSchema()
  tts()
  // syntaxHighlight()
}

fun syntaxHighlight() {
  Lumis.builder().build().use {
    val h = it.highlighter().withLang(Lang.KOTLIN).withTheme(Theme.GITHUB_DARK).build()
    println(
        h.highlight(
                $$"""
                fun main(args: Array<String>) =
                    try {
                      initProps()
                      EngineMain.main(args)
                    } catch (e: Throwable) {
                      val log = KtorSimpleLogger("main")
                      log.error("Failed to start the app: ${e.message}", e)
                    }

                """
                    .trimIndent()
            )
            .string()
    )
  }
}

fun jsonSchema() {
  val generator =
      SerializationClassJsonSchemaGenerator(
          jsonSchemaConfig = JsonSchemaConfig.Strict,
      )

  val schema = generator.generateSchema(ErrorStatus.serializer().descriptor)
  val schemaString = schema.encodeToString(json)

  println(schemaString)
}

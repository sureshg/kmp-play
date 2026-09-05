import kotlin.native.Platform
import kotlinx.coroutines.*

actual class Platform {
  actual fun name() = "Kotlin Native ${Platform.osFamily.name}"
}

actual fun runMain(block: suspend CoroutineScope.() -> Unit) = runBlocking(block = block)

import kotlinx.coroutines.*

actual class Platform {
  actual fun name() = "Kotlin Web"
}

actual fun runMain(block: suspend CoroutineScope.() -> Unit) {
  MainScope().launch(block = block)
}

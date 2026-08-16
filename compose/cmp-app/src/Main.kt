import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
  Window(onCloseRequest = ::exitApplication) {
    App()
  }
}

// fun main() = singleWindowApplication(title = "Compose Desktop App") { App() }

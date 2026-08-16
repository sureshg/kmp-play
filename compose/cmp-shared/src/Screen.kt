import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.suresh.gen.Res

@Composable
fun App() {
  var count by remember { mutableStateOf(0) }
  var sharedText by remember { mutableStateOf("") }

  LaunchedEffect(Unit) {
    println("Reading the shared text...")
    sharedText = Res.readBytes("files/shared-text.txt").decodeToString()
  }

  MaterialTheme {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(text = sharedText)
      Button(onClick = { count++ }) { BasicText(text = "Click me!") }
      BasicText(
          text = "Click count: $count",
      )
    }
  }

  SideEffect { println("Side Effect") }
}

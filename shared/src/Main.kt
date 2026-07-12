import dev.suresh.MediaApiClient
import dev.suresh.Video
import io.github.oshai.kotlinlogging.KotlinLoggingConfiguration

fun main() = runMain { mediaClient() }

suspend fun mediaClient() {
  KotlinLoggingConfiguration.logStartupMessage = false
  println("Kotlin ${KotlinVersion.CURRENT} - ${Platform().name()}")
  val client = MediaApiClient()
  println(client)
  val images = client.images()
  println("Images: ${images.size}")
  val videos = client.videos()
  println("Videos: ${videos.size}")

  val testVideo =
      Video(
          description = "The first Blender Open Movie from 2006",
          sources =
              [
                  "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
              ],
          subtitle = "By Blender Foundation",
          thumb =
              "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ElephantsDream.jpg",
          title = "Elephant Dream",
      )

  runCatching { client.addVideo(testVideo) }
      .onSuccess { println("Add video: ${it.status}") }
      .onFailure { println("Add video failed: ${it.message}") }

  runCatching { client.updateVideo(testVideo) }
      .onSuccess { println("Update video: ${it.status}") }
      .onFailure { println("Update video failed: ${it.message}") }
}

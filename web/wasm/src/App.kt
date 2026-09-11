import dev.suresh.MediaApiClient
import kotlinx.browser.document
import kotlinx.html.*
import kotlinx.html.dom.append

private const val IMAGE_ROOT = "https://suresh.dev/media-api/"

suspend fun main() {
  val client = MediaApiClient()
  val images = client.images()
  val videos = client.videos()

  val app = document.getElementById("app") ?: error("App container is unavailable")

  app.append {
    main(classes = "gallery") {
      h1 { +"Media Gallery" }

      section {
        h2 { +"Images" }
        div(classes = "media-grid") {
          images.forEach { image ->
            figure {
              img(src = "$IMAGE_ROOT${image.path}", alt = "${image.category} by ${image.author}") {
                attributes["loading"] = "lazy"
              }
              figcaption {
                strong { +image.category }
                span { +image.author }
              }
            }
          }
        }
      }

      section {
        h2 { +"Videos" }
        div(classes = "media-grid") {
          videos.forEach { item ->
            figure {
              video {
                controls = true
                src = item.sources.first()
                poster = item.poster ?: item.thumb
              }
              figcaption {
                strong { +item.title }
                span { +item.subtitle }
              }
            }
          }
        }
      }
    }
  }
}

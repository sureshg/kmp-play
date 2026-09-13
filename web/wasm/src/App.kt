import dev.suresh.MediaApiClient
import kotlinx.browser.document
import kotlinx.html.*
import kotlinx.html.dom.append

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
              img(
                  src = "https://suresh.dev/media-api/${image.path}",
                  alt = "${image.category} by ${image.author}",
              ) {
                loading = ImgLoading.lazy
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

      footer {
        a(
            href = "https://kotlin-toolchain.org/dev/user-guide/product-types/wasm-js-app/",
            classes = "toolchain-link",
        ) {
          target = ATarget.blank
          rel = "noopener noreferrer"
          img(src = "https://kotlin-toolchain.org/latest/images/amper-icon.svg", alt = "")
          +"Developed using Kotlin Toolchain"
        }
      }
    }
  }
}

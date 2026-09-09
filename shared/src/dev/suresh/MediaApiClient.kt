package dev.suresh

import dev.suresh.http.Retry
import dev.suresh.http.Timeout
import dev.suresh.http.httpClient
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.resources.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@Resource("/media-api/images.json") class ImgRes

@Resource("/media-api/videos.json") class VideoRes

@Resource("/multipart") class MultiPartRes

@Serializable
@JsonIgnoreUnknownKeys
data class Image(
    val category: String,
    val path: String,
    val author: String,
)

@Serializable
data class Video(
    val description: String,
    val sources: List<String>,
    val subtitle: String,
    val thumb: String,
    val title: String,
    val poster: String? = null,
)

data class MediaApiClient(val timeout: Timeout = Timeout(), val retry: Retry = Retry()) :
    AutoCloseable {

  private val log = KotlinLogging.logger {}

  private val client =
      httpClient(
              name = "Media API Client",
              timeout = timeout,
              retry = retry,
              httpLogger = log,
          )
          .config {
            defaultRequest { url("https://suresh.dev/") }

            // Replace the whole plugin config instead of merging them
            // installOrReplace(DefaultRequest) { url("https://suresh.dev/") }

            // install(Auth) {
            //   basic {
            //     sendWithoutRequest { true }
            //     credentials { BasicAuthCredentials(username = "", password = "") }
            //   }
            // }
          }

  suspend fun images() = client.get(ImgRes()).body<List<Image>>()

  suspend fun videos() = client.get(VideoRes()).body<List<Video>>()

  suspend fun addVideo(video: Video) =
      client.post(VideoRes()) {
        contentType(ContentType.Application.Json)
        setBody(video)
      }

  suspend fun updateVideo(video: Video) =
      client.put(VideoRes()) {
        contentType(ContentType.Application.Json)
        setBody(video)
      }

  override fun close() = client.close()
}

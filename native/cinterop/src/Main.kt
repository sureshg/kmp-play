@file:OptIn(ExperimentalForeignApi::class)

import dev.suresh.custom.*
import kotlinx.cinterop.*
import libcurl.*

fun main(args: Array<String>) {
  println(customGreeting)
  fetchAndPrintUrl("https://suresh.dev/media-api/images.json")
}

fun fetchAndPrintUrl(url: String) {
  val curl = curl_easy_init()
  if (curl != null) {
    curl_easy_setopt(curl, CURLOPT_URL, url)
    curl_easy_setopt(curl, CURLOPT_FOLLOWLOCATION, 1L)
    val res = curl_easy_perform(curl)
    if (res != CURLE_OK) {
      println("curl_easy_perform() failed ${curl_easy_strerror(res)?.toKString()}")
    }
    curl_easy_cleanup(curl)
  }
}

val customGreeting
  get() = getGreeting()?.toKString()

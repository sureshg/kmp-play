package ai

import com.qxotic.jinfer.langchain4j.JinferChatModel
import com.qxotic.jinfer.langchain4j.JinferSpeechModel
import dev.langchain4j.data.message.AudioContent
import dev.langchain4j.data.message.TextContent
import dev.langchain4j.data.message.UserMessage
import java.util.concurrent.CountDownLatch
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.LineEvent

fun tts() {
  val text = "Kotlin is a modern, statically typed programming language!"
  JinferSpeechModel.builder()
      .model("simonfxr/kokoro.cpp-GGUF:Q8_0")
      .companion("voice", "simonfxr/kokoro.cpp-GGUF/voices/kokoro-voice-af_heart.gguf")
      // .model("remixerdec/Inflect-Nano-v2-GGUF:Q8_0")
      // .companion("lexicon", "remixerdec/Inflect-Nano-v2-GGUF/lexicon.bin")
      .build()
      .use { model ->
        val ttsRes = model.synthesize(text)
        AudioSystem.getAudioInputStream(ttsRes.audio().binaryData().inputStream()).use { ais ->
          AudioSystem.getClip().use { clip ->
            val latch = CountDownLatch(1)
            clip.addLineListener { evt ->
              if (evt.type == LineEvent.Type.STOP) latch.countDown()
            }
            clip.open(ais)
            println("Text to play:\n$text")
            clip.start()
            latch.await()
          }
        }
      }
}

fun whisper() {
  JinferChatModel.builder().model("memoravox/whisper-large-v3-turbo-gguf:Q8_0").build().use { model
    ->
    val message =
        UserMessage.from(
            TextContent.from("Transcribe this recording."),
            AudioContent.from("https://suresh.dev/media-api/kotlin.wav"),
        )
    val res = model.chat(message)
    println(res.aiMessage().text())
  }
}

package ai

import com.qxotic.jinfer.langchain4j.*
import dev.langchain4j.data.message.AudioContent
import dev.langchain4j.data.message.TextContent
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.decision.request.*
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
      .use { tts ->
        val ttsRes = tts.synthesize(text)
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

fun system1() {
  // modern-bert, ggmlc
  JinferDecisionModel.builder().model("LiquidAI/LFM2.5-350M-GGUF:Q8_0").build().use { shield ->
    val questions =
        mapOf(
            "spoiler" to YesNoQuestion.from("Does this give away the movie's big twist?"),
        )

    for (comment in
        [
            "The asteroid field chase is the best scene.",
            "No way, Vader is Luke's father?!",
            "Han Solo is the coolest character.",
        ]) {
      val req = DecisionRequest.builder().questions(questions).input(comment).build()
      val probability = shield.decide(req).yesNo("spoiler").probability()
      val text = if (probability > 0.5) "[SPOILER]" else comment
      println("$probability  $text")
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

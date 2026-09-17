package ai

import com.qxotic.jinfer.langchain4j.JinferSpeechModel
import kotlin.io.path.*

fun tts() {
  JinferSpeechModel.builder()
      .model("simonfxr/kokoro.cpp-GGUF:Q8_0")
      .companion("voice", "simonfxr/kokoro.cpp-GGUF/voices/kokoro-voice-af_heart.gguf")
      // .model("remixerdec/Inflect-Nano-v2-GGUF:Q8_0")
      // .companion("lexicon", "remixerdec/Inflect-Nano-v2-GGUF/lexicon.bin")
      .build()
      .use {
        val audio =
            it.synthesize("Kotlin is a modern, statically typed programming language!").audio()
        val audioPath = Path("build/kotlin.wav")
        audioPath.writeBytes(audio.binaryData())
        println("Speech file: ${audioPath.absolutePathString()}")
      }
}

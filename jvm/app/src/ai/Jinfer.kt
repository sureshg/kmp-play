package ai

import com.qxotic.jinfer.langchain4j.JinferSpeechModel

fun tts() {
  JinferSpeechModel.builder()
      .model("simonfxr/kokoro.cpp-GGUF:Q8_0")
      .companion("voice", "simonfxr/kokoro.cpp-GGUF/voices/kokoro-voice-af_heart.gguf")
      // .model("remixerdec/Inflect-Nano-v2-GGUF:Q8_0")
      // .companion("lexicon", "remixerdec/Inflect-Nano-v2-GGUF/lexicon.bin")
      .build()
      .use {
        println(
            it.synthesize(
                    """
                    Kotlin is a modern, statically typed programming language developed by JetBrains, 
                    the company behind popular IDEs like IntelliJ IDEA. It is designed to run on the 
                    Java Virtual Machine (JVM) and is fully interoperable with Java, allowing developers 
                    to use Java libraries and frameworks seamlessly within Kotlin projects.
                    """
                        .trimIndent()
                )
                .audio()
        )
      }
}

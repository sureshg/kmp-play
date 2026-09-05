package genai

import kotlin.io.path.Path
import org.pitest.voices.Chorus
import org.pitest.voices.ChorusConfig
import org.pitest.voices.alba.Alba
import org.pitest.voices.us.EnUsDictionary

fun voices() {
  val chorusCfg = ChorusConfig(EnUsDictionary.en_us())
  Chorus(chorusCfg).use {
    val voice = it.voice(Alba.albaMedium())
    val audio =
        voice.say(
            """
            Kotlin is a modern, statically typed programming language developed by JetBrains, 
            the company behind popular IDEs like IntelliJ IDEA. It is designed to run on the 
            Java Virtual Machine (JVM) and is fully interoperable with Java, allowing developers 
            to use Java libraries and frameworks seamlessly within Kotlin projects.
            """
                .trimIndent()
        )
    println(audio.toString())
    audio.save(Path("build/kotlin.mp3"))
  }
}

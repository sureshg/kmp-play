import kotlin.concurrent.thread
import kotlin.test.Test
import org.jetbrains.lincheck.Lincheck
import org.jetbrains.lincheck.LincheckSettings
import org.junit.jupiter.api.Tag

class AppTest {

  val settings = LincheckSettings {
    invocationTimeoutMs = 10_000
    modelChecking {
      partialOrderReduction = true
    }
  }

  @Test
  @Tag("unit")
  fun unitTest() {
    println("Running App unitTest")
  }

  @Test
  @Tag("integration")
  fun integrationTest() {
    println("Running App integrationTest")
  }

  @Test
  @Tag("smoke")
  fun smokeTest() {
    println("Running App smokeTest")
  }

  @Test
  fun linCheck() =
      Lincheck.runConcurrentTest(invocations = 1_000, settings) {
        var counter: Int = 0
        val thread1 = thread { ++counter }
        val thread2 = thread { ++counter }
        thread1.join()
        thread2.join()
        check(counter == 2)
      }
}

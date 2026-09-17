import kotlin.test.Test
import org.junit.jupiter.api.Tag

class AppTest {

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
}

import org.jetbrains.kotlinx.dataframe.api.*

fun main() {
  println("DataFrame Example!")
  val weatherData =
      dataFrameOf(
          "time" to columnOf(0, 1, 2, 4, 5, 7, 8, 9),
          "temperature" to columnOf(12.0, 14.2, 15.1, 15.9, 17.9, 15.6, 14.2, 24.3),
          "humidity" to columnOf(0.5, 0.32, 0.11, 0.89, 0.68, 0.57, 0.56, 0.5),
      )
  weatherData.filter { temperature > 15.0 }.print()
}

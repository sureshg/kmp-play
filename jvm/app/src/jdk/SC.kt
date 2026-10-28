package jdk

import java.util.concurrent.Callable
import java.util.concurrent.StructuredTaskScope
import java.util.concurrent.StructuredTaskScope.Joiner
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

data class Customer(val name: String, val email: String, val address: String)

data class Order(val no: Long)

data class OrderSummary(val customer: Customer, val order: Order)

fun structuredConcurrency() {
  val error = false

  val tf = Thread.ofVirtual().name("worker-", 0).factory()
  val timeout = 2.seconds.toJavaDuration()
  val orderSummary =
      StructuredTaskScope.open(Joiner.allSuccessfulOrThrow<Any>()) { cfg ->
            cfg.withTimeout(timeout).withThreadFactory(tf).withName("MyScope")
          }
          .use {
            val customer = it.fork(Callable { fetchCustomer(error = error) })
            val order = it.fork(Callable { fetchOrder(error = error) })
            it.join()
            OrderSummary(customer.get(), order.get())
          }

  println(orderSummary)
}

fun fetchCustomer(error: Boolean = false) =
    StructuredTaskScope.open<String>().use {
      val name = it.fork(Callable { fetchCustomerDetail("name", "Suresh", error) })
      val email = it.fork(Callable { fetchCustomerDetail("email", "suresh@example.com", error) })
      val address = it.fork(Callable { fetchCustomerDetail("address", "12 Main St", error) })
      it.join()
      Customer(name.get(), email.get(), address.get())
    }

fun fetchCustomerDetail(detail: String, value: String, error: Boolean) =
    try {
      Thread.sleep(Random.nextLong(1500))
      if (error) error("Error fetching customer $detail!")
      value
    } catch (ie: InterruptedException) {
      println("Fetch customer $detail interrupted!")
      throw ie
    }

fun fetchOrder(error: Boolean = false) =
    try {
      Thread.sleep(Random.nextLong(1500))
      if (error) error("Error fetching order!")
      Order(Random.nextLong(500))
    } catch (ie: InterruptedException) {
      println("Fetch order interrupted!")
      throw ie
    }

fun <T> runConcurrently(tasks: List<Callable<T>>): List<T> =
    StructuredTaskScope.open(Joiner.allSuccessfulOrThrow<T>()) {
          it.withName("Scope")
        }
        .use { scope ->
          tasks.forEach(scope::fork)
          scope.join()
        }

fun <T> race(tasks: List<Callable<T>>): T =
    StructuredTaskScope.open(Joiner.anySuccessfulOrThrow<T>()) {
          it.withName("Scope")
        }
        .use { scope ->
          tasks.forEach(scope::fork)
          scope.join()
        }

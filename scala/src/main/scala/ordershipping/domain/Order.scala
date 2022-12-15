package ordershipping.domain

import ordershipping.domain.OrderStatus.OrderStatus

import scala.collection.mutable

// TODO : make it private
class Order(
    var total: Double = 0,
    var currency: String,
    var items: mutable.MutableList[OrderItem] = mutable.MutableList.empty,
    var tax: Double = 0,
    var status: OrderStatus,
    var id: Int
)

object Order {
  def create(): Order = {
    new Order(
      total = 0d,
      currency = "EUR",
      items = mutable.MutableList.empty,
      tax = 0d,
      status = OrderStatus.Created,
      id = 1
    )
  }
}

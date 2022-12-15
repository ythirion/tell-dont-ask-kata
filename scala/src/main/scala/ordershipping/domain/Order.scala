package ordershipping.domain

import ordershipping.domain.OrderItem.createOrderItem
import ordershipping.domain.OrderStatus.OrderStatus

// TODO : make it private
class Order(
             var currency: String = "",
             var items: List[OrderItem] = List.empty,
             var status: OrderStatus,
             var id: Int
           ) {
  def total: Double = items.map(i => i.taxedAmount).sum

  def tax: Double = items.map(i => i.tax).sum
}

object Order {
  def create(items: Map[Product, Int]): Order = {
    val order = new Order(
      currency = "EUR",
      items = List.empty,
      status = OrderStatus.Created,
      id = 1
    )

    order.items = items.map {
      case (product, quantity) => createOrderItem(product, quantity)
    }.toList

    order
  }
}

package ordershipping.domain

import ordershipping.domain.OrderItem.createOrderItem
import ordershipping.domain.OrderStatus.OrderStatus

// TODO : make it private
class Order(
    var total: Double = 0,
    var currency: String = "",
    var items: List[OrderItem] = List.empty,
    var tax: Double = 0,
    var status: OrderStatus,
    var id: Int
)

object Order {
  def create(items: Map[Product, Int]): Order = {
    val order = new Order(
      total = 0d,
      currency = "EUR",
      items = List.empty,
      tax = 0d,
      status = OrderStatus.Created,
      id = 1
    )

    order.items = items.map {
      case (product, quantity) => createOrderItem(product, quantity)
    }.toList

    // TODO : calculate them instead of storing them
    order.total = order.items.map(i => i.taxedAmount).sum
    order.tax += order.items.map(i => i.tax).sum

    order
  }

}

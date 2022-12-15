package builders

import ordershipping.domain.Order
import ordershipping.domain.OrderStatus._

class OrderBuilder {
  private var status: OrderStatus = Created

  def approved(): OrderBuilder = withStatus(Approved)

  def shipped(): OrderBuilder = withStatus(Shipped)

  private def withStatus(status: OrderStatus): OrderBuilder = {
    this.status = status
    this
  }

  def rejected(): OrderBuilder = withStatus(Rejected)

  def build(): Order = {
    val order = Order.create()
    order.status = status
    order
  }
}

object OrderBuilder {
  def anOrder(): OrderBuilder = new OrderBuilder
}

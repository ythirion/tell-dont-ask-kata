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
    val createdOrder = Order.create(Map.empty)

    status match {
      case Approved => createdOrder.approve()
      case Rejected => createdOrder.reject()
      case Shipped =>
        createdOrder.approve()
        createdOrder.ship(_ => {})
      case _ =>
    }
    createdOrder
  }
}

object OrderBuilder {
  def anOrder(): OrderBuilder = new OrderBuilder
}

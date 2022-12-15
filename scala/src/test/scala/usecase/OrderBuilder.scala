package usecase

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

  def build(): Order = new Order(status = status, id = 1)
}

object OrderBuilder {
  def anOrder(): OrderBuilder = new OrderBuilder
}

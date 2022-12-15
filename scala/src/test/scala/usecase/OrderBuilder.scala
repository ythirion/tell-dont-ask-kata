package usecase

import ordershipping.domain.OrderStatus._
import ordershipping.domain.{Order, OrderStatus}
class OrderBuilder {
  def build(): Order = new Order(status = Created, id = 1)
}

object OrderBuilder {
  def anOrder(): OrderBuilder = new OrderBuilder
}

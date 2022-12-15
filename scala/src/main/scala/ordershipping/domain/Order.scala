package ordershipping.domain

import ordershipping.domain.OrderItem.createOrderItem
import ordershipping.domain.OrderStatus.{
  Approved,
  Created,
  OrderStatus,
  Rejected,
  Shipped
}
// TODO : this layer should not access those exceptions -> move them inside Domain
import ordershipping.usecase.{
  ApprovedOrderCannotBeRejectedException,
  RejectedOrderCannotBeApprovedException,
  ShippedOrdersCannotBeChangedException
}

// TODO : make it private
class Order(
    var currency: String = "",
    var items: List[OrderItem] = List.empty,
    var status: OrderStatus,
    var id: Int
) {

  def approve(): Unit = {
    ifNotShipped { order =>
      order.status match {
        case Rejected => throw new RejectedOrderCannotBeApprovedException
        case _        => order.status = Approved
      }
    }
  }

  def reject(): Unit = {
    ifNotShipped { order =>
      order.status match {
        case Approved => throw new ApprovedOrderCannotBeRejectedException
        case _        => order.status = Rejected
      }
    }
  }

  private def ifNotShipped(continueWith: Order => Unit): Unit = {
    if (status == Shipped)
      throw new ShippedOrdersCannotBeChangedException
    continueWith(this)
  }

  def total: Double = items.map(i => i.taxedAmount).sum

  def tax: Double = items.map(i => i.tax).sum
}

object Order {
  def create(items: Map[Product, Int]): Order = {
    val order = new Order(
      currency = "EUR",
      items = List.empty,
      status = Created,
      id = 1
    )

    order.items = items.map {
      case (product, quantity) => createOrderItem(product, quantity)
    }.toList

    order
  }
}

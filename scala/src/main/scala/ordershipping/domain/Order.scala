package ordershipping.domain

import ordershipping.domain.OrderItem.createOrderItem
import ordershipping.domain.OrderStatus.{
  Approved,
  Created,
  OrderStatus,
  Rejected,
  Shipped
}
import ordershipping.service.ShipmentService
import ordershipping.usecase.{
  OrderCannotBeShippedException,
  OrderCannotBeShippedTwiceException
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

  // TODO : decouple this class from Service and Use Case exceptions
  def ship(shipmentService: ShipmentService): Unit = {
    status match {
      case Created | Rejected => throw new OrderCannotBeShippedException
      case Shipped            => throw new OrderCannotBeShippedTwiceException
      case _ =>
        shipmentService.ship(this)
        status = OrderStatus.Shipped
    }
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

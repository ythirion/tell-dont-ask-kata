package ordershipping.domain

import ordershipping.domain.OrderItem.createOrderItem
import ordershipping.domain.OrderStatus.{Approved, Created, OrderStatus, Rejected, Shipped}

import scala.collection.immutable

class Order private(
                     val currency: String = "",
                     val items: Seq[OrderItem],
                     var status: OrderStatus,
                     val id: Int
                   ) {
  def approve(): Unit = {
    ifNotShipped { order =>
      order.status match {
        case Rejected => throw new RejectedOrderCannotBeApprovedException
        case _ => order.status = Approved
      }
    }
  }

  def reject(): Unit = {
    ifNotShipped { order =>
      order.status match {
        case Approved => throw new ApprovedOrderCannotBeRejectedException
        case _ => order.status = Rejected
      }
    }
  }

  private def ifNotShipped(continueWith: Order => Unit): Unit = {
    if (status == Shipped)
      throw new ShippedOrdersCannotBeChangedException
    continueWith(this)
  }

  // Break the dependency between Order and ShipmentService with Dependency Inversion using func arg
  def ship(ship: Order => Unit): Unit = {
    status match {
      case Created | Rejected => throw new OrderCannotBeShippedException
      case Shipped => throw new OrderCannotBeShippedTwiceException
      case _ =>
        ship(this)
        status = OrderStatus.Shipped
    }
  }

  def total: Double = items.map(i => i.taxedAmount).sum

  def tax: Double = items.map(i => i.tax).sum
}

object Order {
  def create(items: Map[Product, Int]): Order =
    new Order(
      currency = "EUR",
      items = toOrderItems(items),
      status = Created,
      id = 1
    )

  private def toOrderItems(items: Map[Product, Int]): immutable.Seq[OrderItem] =
    items.map {
      case (product, quantity) => createOrderItem(product, quantity)
    }.toList
}

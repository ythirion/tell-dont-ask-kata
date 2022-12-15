package ordershipping.domain

import ordershipping.domain.OrderItem.createOrderItem
import ordershipping.domain.OrderStatus.{Approved, Created, OrderStatus, Rejected, Shipped}

import scala.collection.immutable

class Order private (
    private val currency: String,
    private val items: Seq[OrderItem],
    private var _status: OrderStatus = Created,
    val id: Int
) {
  def status: OrderStatus = this._status

  def approve(): Unit = {
    ifNotShipped { order =>
      order._status match {
        case Rejected => throw new RejectedOrderCannotBeApprovedException
        case _        => order._status = Approved
      }
    }
  }

  def reject(): Unit = {
    ifNotShipped { order =>
      order._status match {
        case Approved => throw new ApprovedOrderCannotBeRejectedException
        case _        => order._status = Rejected
      }
    }
  }

  private def ifNotShipped(continueWith: Order => Unit): Unit = {
    if (_status == Shipped)
      throw new ShippedOrdersCannotBeChangedException
    continueWith(this)
  }

  // Break the dependency between Order and ShipmentService with Dependency Inversion using func arg
  def ship(ship: Order => Unit): Unit = {
    _status match {
      case Created | Rejected => throw new OrderCannotBeShippedException
      case Shipped            => throw new OrderCannotBeShippedTwiceException
      case _ =>
        ship(this)
        _status = OrderStatus.Shipped
    }
  }

  override def toString: String =
    s"""
       |Status:${_status}
       |Currency:$currency
       |Items:${items.mkString("{", " ; ", "}")}
       |Tax:$tax
       |Total:$total
       |""".stripMargin

  def total: Double = items.map(i => i.taxedAmount).sum

  def tax: Double = items.map(i => i.tax).sum
}

object Order {
  def create(items: Map[Product, Int]): Order =
    new Order(
      currency = "EUR",
      items = toOrderItems(items),
      _status = Created,
      id = 1
    )

  private def toOrderItems(items: Map[Product, Int]): immutable.Seq[OrderItem] =
    items.map {
      case (product, quantity) => createOrderItem(product, quantity)
    }.toList
}

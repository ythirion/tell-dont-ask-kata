package doubles

import ordershipping.domain.Order
import ordershipping.usecase.ports.ShipmentService

class TestShipmentService extends ShipmentService {
  private var orderToShip: Order = _

  override def ship(order: Order): Unit = orderToShip = order

  def shippedOrder(): Order = orderToShip
}

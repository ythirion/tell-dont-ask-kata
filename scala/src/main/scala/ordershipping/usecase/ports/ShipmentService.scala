package ordershipping.usecase.ports

import ordershipping.domain.Order

trait ShipmentService {
  def ship(order: Order): Unit
}

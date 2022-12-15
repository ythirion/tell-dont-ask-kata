package ordershipping.usecase.shipment

import ordershipping.usecase.ports.ShipmentService
import ordershipping.usecase.ports.repository.OrderRepository

class OrderShipmentUseCase(
    val orderRepository: OrderRepository,
    val shipmentService: ShipmentService
) {
  def run(request: OrderShipmentRequest): Unit = {
    orderRepository
      .getById(request.orderId)
      .foreach(order => {
        order.ship(order => shipmentService.ship(order))
        orderRepository.save(order)
      })
  }
}

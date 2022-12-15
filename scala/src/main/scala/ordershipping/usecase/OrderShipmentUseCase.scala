package ordershipping.usecase

import ordershipping.repository.OrderRepository
import ordershipping.service.ShipmentService

class OrderShipmentUseCase(
    val orderRepository: OrderRepository,
    val shipmentService: ShipmentService
) {
  def run(request: OrderShipmentRequest): Unit = {
    orderRepository
      .getById(request.orderId)
      .foreach(order => {
        order.ship(shipmentService)
        orderRepository.save(order)
      })
  }
}

package ordershipping.usecase.approval

import ordershipping.repository.OrderRepository

class OrderApprovalUseCase(val orderRepository: OrderRepository) {
  def run(request: OrderApprovalRequest): Unit = {
    orderRepository
      .getById(request.orderId)
      .foreach(order => {
        if (request.approved) order.approve() else order.reject()
        orderRepository.save(order)
      })
  }
}

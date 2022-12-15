package ordershipping.usecase

import ordershipping.domain.Order
import ordershipping.repository.{OrderRepository, ProductCatalog}

class OrderCreationUseCase(
                            val orderRepository: OrderRepository,
                            val productCatalog: ProductCatalog
                          ) {
  def run(request: SellItemsRequest): Unit = {
    // TODO : simplify it -> split
    val items = request.requests
      .map(request => {
        productCatalog.getByName(request.productName) match {
          case Some(p) => (p, request.quantity)
          case _ => throw new UnknownProductException
        }
      })
      .toMap

    val order = Order.create(items)

    orderRepository.save(order)
  }

  private def roundAt(p: Int)(n: Double): Double = {
    val s = math pow(10, p)
    (math round n * s) / s
  }
}

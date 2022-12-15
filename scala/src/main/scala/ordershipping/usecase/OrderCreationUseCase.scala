package ordershipping.usecase

import ordershipping.domain.{Order, Product}
import ordershipping.repository.{OrderRepository, ProductCatalog}

class OrderCreationUseCase(
    val orderRepository: OrderRepository,
    val productCatalog: ProductCatalog
) {
  def run(request: SellItemsRequest): Unit = {
    orderRepository.save(
      Order.create(
        toProductQuantities(request)
      )
    )
  }

  private def toProductQuantities(
      request: SellItemsRequest
  ): Map[Product, Int] =
    request.requests
      .map(toProductQuantity)
      .toMap

  private def toProductQuantity(
      sellItemRequest: SellItemRequest
  ): (Product, Int) =
    productCatalog.getByName(sellItemRequest.productName) match {
      case Some(p) => (p, sellItemRequest.quantity)
      case _       => throw new UnknownProductException
    }

  private def roundAt(p: Int)(n: Double): Double = {
    val s = math pow (10, p)
    (math round n * s) / s
  }
}

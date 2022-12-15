package ordershipping.usecase.creation

import ordershipping.domain.{Order, Product}
import ordershipping.usecase.ports.repository.{OrderRepository, ProductCatalog}

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
}

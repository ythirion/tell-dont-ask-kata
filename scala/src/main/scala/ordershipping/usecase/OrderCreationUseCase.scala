package ordershipping.usecase

import ordershipping.domain.{Order, OrderItem}
import ordershipping.repository.{OrderRepository, ProductCatalog}

class OrderCreationUseCase(
    val orderRepository: OrderRepository,
    val productCatalog: ProductCatalog
) {
  def run(request: SellItemsRequest): Unit = {
    val order = Order.create()

    for (itemRequest <- request.requests) {
      val product = productCatalog.getByName(itemRequest.productName)

      if (product.isEmpty)
        throw new UnknownProductException
      else {
        product.foreach(p => {
          val unitaryTax =
            roundAt(2)((p.price / 100) * p.category.taxPercentage)
          val unitaryTaxedAmount = roundAt(2)(p.price + unitaryTax)
          val taxedAmount =
            roundAt(2)(unitaryTaxedAmount * itemRequest.quantity)
          val taxAmount = roundAt(2)(unitaryTax * itemRequest.quantity)

          val orderItem = new OrderItem(
            product = p,
            quantity = itemRequest.quantity,
            taxedAmount = taxedAmount,
            tax = taxAmount
          )

          order.items += orderItem
          order.total += taxedAmount
          order.tax += taxAmount
        })
      }
    }
    orderRepository.save(order)
  }

  private def roundAt(p: Int)(n: Double): Double = {
    val s = math pow (10, p)
    (math round n * s) / s
  }
}

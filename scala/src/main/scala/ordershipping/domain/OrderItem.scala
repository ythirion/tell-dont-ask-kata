package ordershipping.domain

import ordershipping.domain.Math.roundAt

class OrderItem private(
                         val product: Product,
                         val quantity: Int,
                         val taxedAmount: Double,
                         val tax: Double
                       ) {
  override def toString: String =
    s"Name:${product.name}, Price:${product.price}, Quantity:$quantity, Tax:$tax, TaxedAmount:$taxedAmount"
}

object OrderItem {
  def createOrderItem(product: Product, quantity: Int): OrderItem =
    new OrderItem(
      product = product,
      quantity = quantity,
      taxedAmount = calculateTaxedAmount(product, quantity),
      tax = calculateTaxAmount(product, quantity)
    )

  private def calculateTaxAmount(product: Product, quantity: Int): Double =
    roundAt(2)(product.unitaryTax * quantity)

  private def calculateTaxedAmount(product: Product, quantity: Int): Double =
    roundAt(2)(product.unitaryTaxedAmount * quantity)
}

package ordershipping.domain

class OrderItem private (
    val product: ordershipping.domain.Product,
    var quantity: Int,
    var taxedAmount: Double,
    var tax: Double
)

object OrderItem {
  def createOrderItem(product: Product, quantity: Int): OrderItem = {
    val unitaryTax =
      roundAt(2)((product.price / 100) * product.category.taxPercentage)
    val unitaryTaxedAmount = roundAt(2)(product.price + unitaryTax)
    val taxedAmount = roundAt(2)(unitaryTaxedAmount * quantity)
    val taxAmount = roundAt(2)(unitaryTax * quantity)

    new OrderItem(
      product = product,
      quantity = quantity,
      taxedAmount = taxedAmount,
      tax = taxAmount
    )
  }

  private def roundAt(p: Int)(n: Double): Double = {
    val s = math pow (10, p)
    (math round n * s) / s
  }
}

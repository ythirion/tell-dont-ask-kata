package ordershipping.domain

import ordershipping.domain.Math.roundAt

final class Product(
    val name: String,
    val price: Double,
    val category: Category
) {
  def unitaryTaxedAmount: Double = roundAt(2)(price + unitaryTax)

  def unitaryTax: Double = roundAt(2)((price / 100) * category.taxPercentage)
}

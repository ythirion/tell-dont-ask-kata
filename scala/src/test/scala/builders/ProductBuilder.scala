package builders

import ordershipping.domain.{Category, Product}

class ProductBuilder private(
                              private val name: String,
                              private val category: Category
                            ) {
  private var price: Double = 0d

  def build(): Product = new Product(name, price, category)

  def costing(price: Double): ProductBuilder = {
    this.price = price
    this
  }
}

object ProductBuilder {
  private val food = new Category(name = "food", taxPercentage = 10)

  def food(name: String): ProductBuilder = new ProductBuilder(name, food)
}

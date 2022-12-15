package doubles

import ordershipping.domain
import ordershipping.usecase.ports.repository.ProductCatalog

class InMemoryProductCatalog(val products: List[ordershipping.domain.Product])
    extends ProductCatalog {
  override def getByName(name: String): Option[domain.Product] =
    products.find(p => p.name == name)
}

package ordershipping.usecase.ports.repository

trait ProductCatalog {
  def getByName(name: String): Option[ordershipping.domain.Product]
}

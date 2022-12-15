package usecase

import builders.ProductBuilder._
import doubles.{InMemoryProductCatalog, TestOrderRepository}
import ordershipping.usecase.creation.{OrderCreationUseCase, SellItemRequest, SellItemsRequest, UnknownProductException}
import org.approvaltests.Approvals._
import org.junit.jupiter.api.{BeforeEach, Test}
import org.scalatest.Assertions.assertThrows

class OrderCreationUseCaseTest {
  private val productCatalog = new InMemoryProductCatalog(
    List(
      food("salad").costing(3.56).build(),
      food("tomato").costing(4.65).build()
    )
  )
  private var orderRepository: TestOrderRepository = _
  private var useCase: OrderCreationUseCase = _

  @BeforeEach
  def setup(): Unit = {
    orderRepository = new TestOrderRepository()
    useCase = new OrderCreationUseCase(
      orderRepository = orderRepository,
      productCatalog = productCatalog
    )
  }

  @Test
  def OrderCreationUseCaseShouldSellMultipleItems(): Unit = {
    val saladRequest = SellItemRequest(productName = "salad", quantity = 2)
    val tomatoRequest = SellItemRequest(productName = "tomato", quantity = 3)

    val request = SellItemsRequest(
      List(saladRequest, tomatoRequest)
    )

    useCase.run(request)

    verify(
      orderRepository.savedOrder()
    )
  }

  @Test
  def OrderCreationUseCaseShouldFailWithUnknownProduct: Unit = {
    val request = SellItemsRequest(
      List(SellItemRequest(productName = "unknown product", quantity = 0))
    )
    assertThrows[UnknownProductException] {
      useCase.run(request)
    }
  }
}

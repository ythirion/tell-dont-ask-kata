package usecase

import builders.ProductBuilder._
import doubles.{InMemoryProductCatalog, TestOrderRepository}
import ordershipping.domain.OrderStatus
import ordershipping.usecase.creation.{OrderCreationUseCase, SellItemRequest, SellItemsRequest, UnknownProductException}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class OrderCreationUseCaseTest
    extends AnyFlatSpec
    with Matchers
    with BeforeAndAfterEach {
  private val productCatalog = new InMemoryProductCatalog(
    List(
      food("salad").costing(3.56).build(),
      food("tomato").costing(4.65).build()
    )
  )
  private var orderRepository: TestOrderRepository = _
  private var useCase: OrderCreationUseCase = _

  override def beforeEach(): Unit = {
    orderRepository = new TestOrderRepository()
    useCase = new OrderCreationUseCase(
      orderRepository = orderRepository,
      productCatalog = productCatalog
    )
  }

  "order creation use case" should "sell multiple items" in {
    val saladRequest = SellItemRequest(productName = "salad", quantity = 2)
    val tomatoRequest = SellItemRequest(productName = "tomato", quantity = 3)

    val request = SellItemsRequest(
      List(saladRequest, tomatoRequest)
    )

    useCase.run(request)

    val insertedOrder = orderRepository.savedOrder()
    insertedOrder.status shouldBe OrderStatus.Created
    insertedOrder.total shouldBe 23.20
    insertedOrder.tax shouldBe 2.13
    insertedOrder.currency shouldBe "EUR"
    insertedOrder.items.length shouldBe 2
    insertedOrder.items.head.product.name shouldBe "salad"
    insertedOrder.items.head.product.price shouldBe 3.56
    insertedOrder.items.head.quantity shouldBe 2
    insertedOrder.items.head.taxedAmount shouldBe 7.84
    insertedOrder.items.head.tax shouldBe 0.72

    insertedOrder.items(1).product.name shouldBe "tomato"
    insertedOrder.items(1).product.price shouldBe 4.65
    insertedOrder.items(1).quantity shouldBe 3
    insertedOrder.items(1).taxedAmount shouldBe 15.36
    insertedOrder.items(1).tax shouldBe 1.41
  }

  "order creation use case" should "unknown product" in {
    val request = SellItemsRequest(
      List(SellItemRequest(productName = "unknown product", quantity = 0))
    )
    assertThrows[UnknownProductException] {
      useCase.run(request)
    }
  }
}

package usecase

import builders.OrderBuilder
import builders.OrderBuilder.anOrder
import doubles.{TestOrderRepository, TestShipmentService}
import ordershipping.domain.{Order, OrderStatus}
import ordershipping.usecase.{OrderCannotBeShippedException, OrderCannotBeShippedTwiceException, OrderShipmentRequest, OrderShipmentUseCase}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.reflect.ClassTag

class OrderShipmentUseCaseTest
    extends AnyFlatSpec
    with Matchers
    with BeforeAndAfterEach {
  private var orderRepository: TestOrderRepository = _
  private var shipmentService: TestShipmentService = _
  private var useCase: OrderShipmentUseCase = _

  override def beforeEach(): Unit = {
    orderRepository = new TestOrderRepository
    shipmentService = new TestShipmentService
    useCase = new OrderShipmentUseCase(
      orderRepository = orderRepository,
      shipmentService = shipmentService
    )
  }

  "order shipment use case" should "ship approved order" in {
    val approvedOrder = anOrder().approved().build()
    existingOrder(approvedOrder)

    useCase.run(
      createShipmentRequest(approvedOrder)
    )

    assertSavedOrder { savedOrder =>
      savedOrder.status shouldBe OrderStatus.Shipped
      shipmentService.shippedOrder() shouldBe approvedOrder
    }
  }

  "order shipment use case" should "can not ship created order" in {
    failFor[OrderCannotBeShippedException](
      anOrder()
    )
  }

  "order shipment use case" should "can not ship rejected order" in {
    failFor[OrderCannotBeShippedException](
      anOrder().rejected()
    )
  }

  "order shipment use case" should "can not ship again a shipped order" in {
    failFor[OrderCannotBeShippedTwiceException](
      anOrder().shipped()
    )
  }

  private def failFor[T <: Exception](
      orderBuilder: OrderBuilder
  )(implicit c: ClassTag[T]): Unit = {
    val order = orderBuilder.build()

    existingOrder(order)

    assertThrows[T] {
      useCase.run(
        createShipmentRequest(order)
      )
    }
    assertSavedOrder { savedOrder =>
      savedOrder shouldBe null
      shipmentService.shippedOrder() shouldBe null
    }
  }

  private def createShipmentRequest(order: Order): OrderShipmentRequest =
    OrderShipmentRequest(orderId = order.id)

  private def existingOrder(order: Order): Unit =
    orderRepository.addOrder(order)

  private def assertSavedOrder(assertions: Order => Unit): Unit =
    assertions(orderRepository.savedOrder())

}

package usecase

import builders.OrderBuilder.anOrder
import doubles.{TestOrderRepository, TestShipmentService}
import ordershipping.domain.OrderStatus
import ordershipping.usecase.{OrderCannotBeShippedException, OrderCannotBeShippedTwiceException, OrderShipmentRequest, OrderShipmentUseCase}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

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
    val initialOrder = anOrder().approved().build()
    orderRepository.addOrder(initialOrder)
    val request = OrderShipmentRequest(orderId = 1)

    useCase.run(request)

    val savedOrder = orderRepository.savedOrder()
    savedOrder.status shouldBe OrderStatus.Shipped
    shipmentService.shippedOrder() shouldBe initialOrder
  }

  "order shipment use case" should "can not ship created order" in {
    val initialOrder = anOrder().build()
    orderRepository.addOrder(initialOrder)
    val request = OrderShipmentRequest(orderId = 1)

    assertThrows[OrderCannotBeShippedException] {
      useCase.run(request)
    }
    orderRepository.savedOrder() shouldBe null
    shipmentService.shippedOrder() shouldBe null
  }

  "order shipment use case" should "can not ship rejected order" in {
    val initialOrder = anOrder().rejected().build()
    orderRepository.addOrder(initialOrder)
    val request = OrderShipmentRequest(orderId = 1)

    assertThrows[OrderCannotBeShippedException] {
      useCase.run(request)
    }
    orderRepository.savedOrder() shouldBe null
    shipmentService.shippedOrder() shouldBe null
  }

  "order shipment use case" should "can not ship again a shipped order" in {
    val initialOrder = anOrder().shipped().build()
    orderRepository.addOrder(initialOrder)
    val request = OrderShipmentRequest(orderId = 1)

    assertThrows[OrderCannotBeShippedTwiceException] {
      useCase.run(request)
    }
    orderRepository.savedOrder() shouldBe null
    shipmentService.shippedOrder() shouldBe null
  }
}

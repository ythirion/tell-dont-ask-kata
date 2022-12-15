package usecase

import builders.OrderBuilder
import builders.OrderBuilder.anOrder
import doubles.TestOrderRepository
import ordershipping.domain._
import ordershipping.usecase._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.reflect.ClassTag

class OrderApprovalUseCaseTest
    extends AnyFlatSpec
    with Matchers
    with BeforeAndAfterEach {
  private var orderRepository: TestOrderRepository = _
  private var useCase: OrderApprovalUseCase = _

  override def beforeEach(): Unit = {
    orderRepository = new TestOrderRepository
    useCase = new OrderApprovalUseCase(orderRepository)
  }

  "order approval use case" should "approve existing order" in {
    approveOrderSuccessfully(anOrder()) { savedOrder =>
      savedOrder.status shouldBe OrderStatus.Approved
    }
  }

  "order approval use case" should "reject existing order" in {
    rejectOrderSuccessfully(anOrder()) { savedOrder =>
      savedOrder.status shouldBe OrderStatus.Rejected
    }
  }

  "order approval use case" should "can not approve rejected order" in {
    approveOrderFailFor[RejectedOrderCannotBeApprovedException](
      anOrder().rejected()
    )
  }

  "order approval use case" should "can not reject approved order" in {
    rejectOrderFailFor[ApprovedOrderCannotBeRejectedException](
      anOrder().approved()
    )
  }

  "order approval use case" should "can not reject shipped order" in {
    rejectOrderFailFor[ShippedOrdersCannotBeChangedException](
      anOrder().shipped()
    )
  }

  private def approveOrderSuccessfully(orderBuilder: OrderBuilder)(
      assertions: Order => Unit
  ): Unit = successFor(orderBuilder.build(), approved = true)(assertions)

  private def successFor(
      order: Order,
      approved: Boolean
  )(assertions: Order => Unit): Unit = {
    existingOrder(order)
    useCase.run(createApprovalRequestFor(order, approved))

    val savedOrder = orderRepository.savedOrder()

    savedOrder shouldBe order
    assertions(savedOrder)
  }

  private def createApprovalRequestFor(order: Order, approved: Boolean) =
    OrderApprovalRequest(orderId = order.id, approved = approved)

  private def existingOrder(order: Order): Unit =
    orderRepository.addOrder(order)

  private def rejectOrderSuccessfully(orderBuilder: OrderBuilder)(
      assertions: Order => Unit
  ): Unit = successFor(orderBuilder.build(), approved = false)(assertions)

  private def approveOrderFailFor[T <: Exception](orderBuilder: OrderBuilder)(
      implicit c: ClassTag[T]
  ): Unit = failFor[T](orderBuilder.build(), approve = true)

  private def rejectOrderFailFor[T <: Exception](orderBuilder: OrderBuilder)(
      implicit c: ClassTag[T]
  ): Unit = failFor[T](orderBuilder.build(), approve = false)

  private def failFor[T <: Exception](order: Order, approve: Boolean)(implicit
      c: ClassTag[T]
  ): Unit = {
    existingOrder(order)

    assertThrows[T] {
      useCase.run(
        createApprovalRequestFor(order, approve)
      )
    }
    orderRepository.savedOrder() shouldBe null
  }
}

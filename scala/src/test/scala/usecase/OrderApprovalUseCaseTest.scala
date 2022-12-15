package usecase

import builders.OrderBuilder.anOrder
import doubles.TestOrderRepository
import ordershipping.domain.{Order, OrderStatus}
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
    approveOrderSuccessfully(anOrder().build()) { savedOrder =>
      savedOrder.status shouldBe OrderStatus.Approved
    }
  }

  "order approval use case" should "reject existing order" in {
    rejectOrderSuccessfully(anOrder().build()) { savedOrder =>
      savedOrder.status shouldBe OrderStatus.Rejected
    }
  }

  "order approval use case" should "can not approve rejected order" in {
    approveOrderFailFor[RejectedOrderCannotBeApprovedException](
      anOrder().rejected().build()
    )
  }

  "order approval use case" should "can not reject approved order" in {
    rejectOrderFailFor[ApprovedOrderCannotBeRejectedException](
      anOrder()
        .approved()
        .build()
    )
  }

  "order approval use case" should "can not reject shipped order" in {
    rejectOrderFailFor[ShippedOrdersCannotBeChangedException](
      anOrder()
        .shipped()
        .build()
    )
  }

  private def approveOrderFailFor[T <: Exception](order: Order)(implicit
                                                                c: ClassTag[T]
  ): Unit = failFor[T](order, approve = true)

  private def rejectOrderFailFor[T <: Exception](order: Order)(implicit
                                                               c: ClassTag[T]
  ): Unit = failFor[T](order, approve = false)

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

  private def approveOrderSuccessfully(order: Order)(
    assertions: Order => Unit
  ): Unit = successFor(order, approved = true)(assertions)

  private def rejectOrderSuccessfully(order: Order)(
    assertions: Order => Unit
  ): Unit = successFor(order, approved = false)(assertions)

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
}

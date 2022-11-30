package ar.edu.unq.desapp.grupoN.desapp.model

import ar.edu.unq.desapp.grupoN.desapp.model.OperationStatus.*
import org.hibernate.annotations.GenericGenerator
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity
data class Operation(
    @Id @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "BINARY(16)")
    var id: UUID?,
    /** usuario que inició la operación*/
    @OneToOne
    @JoinColumn(name="userId")
    val user: User,
    /** aviso sobre el que user intenta operar */
    @OneToOne
    @JoinColumn(name="advertisementId")
    val advertisement: Advertisement,
    val creationTimestamp: Instant = Instant.now(),
    var updateTimestamp: Instant = creationTimestamp,
    @Column(columnDefinition = "VARCHAR(50)")
    @Enumerated(EnumType.STRING)
    var status: OperationStatus = STARTED,
    @Embedded
    @AttributeOverride( name = "currency", column = Column(name = "priceCloseCurrency"))
    @AttributeOverride( name = "value", column = Column(name = "priceCloseValue"))
    var cryptoPriceClose: CurrencyAmount? = null
) {
    /** no llamar directamente, usar state() */
    @Transient
    private var _state: OperationStatusState? = null

    private fun state(): OperationStatusState {
        if (_state == null || _state?.status != status) {
            _state = when (status) {
                STARTED -> OperationStatusState.Started(this)
                INTERESTED_USER_DEPOSIT -> OperationStatusState.InterestedUserDeposit(this)
                COMPLETED -> OperationStatusState.Completed(this)
                CANCELLED -> OperationStatusState.Cancelled(this)
            }
        }
        return _state!!
    }

    fun updateStatus(userId: Int, newStatus: OperationStatus) {
        throwIfCannotBeUpdatedToNewStatus(newStatus)
        throwIfStatusCannotBeUpdatedByUser(userId, newStatus)
        status = newStatus
        updateTimestamp = Instant.now()
        if (newStatus == COMPLETED) {
            advertisement.setAsCompleted()
            cryptoPriceClose = CurrencyAmount(
                CurrencyCode.USD,
                advertisement.cryptoPrice.value * advertisement.cryptoAmount
            )
        }
    }

    private fun throwIfStatusCannotBeUpdatedByUser(userId: Int, newStatus: OperationStatus) {
        if (!state().userCanUpdateStatus(userId, newStatus))
            throw OperationException.operationStatusCannotBeUpdatedByUser(id!!, status, userId)
    }

    private fun throwIfCannotBeUpdatedToNewStatus(newStatus: OperationStatus) {
        if (!state().newStatusIsValid(newStatus))
            throw OperationException.operationInvalidStatus(id!!, status, newStatus)
    }

    fun duration(): Duration = Duration.between(creationTimestamp, updateTimestamp)
    fun isClosed(): Boolean = state().isClosed()
    fun wasSuccessfullyCompleted() = status == COMPLETED
    fun wasCancelled() = status == CANCELLED

}

enum class OperationType {
    BUY, SELL
}

enum class OperationStatus {
    /** Se creó una nueva operación */
    STARTED,
    /** El usuario interesado en el aviso hace el depósito */
    INTERESTED_USER_DEPOSIT,
    /** Se concreta toda la operación y el usuario dueño del aviso la dá por finalizada */
    COMPLETED,
    /** El usuario que canceló la operación */
    CANCELLED;

    companion object {
        fun previousStatus(status: OperationStatus): OperationStatus? {
            if (status.ordinal < 1)
                return null
            return OperationStatus.values()[status.ordinal - 1]
        }
    }
}

abstract class OperationStatusState(val op: Operation, val status: OperationStatus) {

    open fun isClosed(): Boolean = false

    fun newStatusIsValid(newStatus: OperationStatus): Boolean {
        if (newStatus == CANCELLED)
            return true
        val expectedCurrentStatus = OperationStatus.previousStatus(newStatus)
        return expectedCurrentStatus != null && expectedCurrentStatus == op.status
    }

    abstract fun userCanUpdateStatus(userId: Int, newStatus: OperationStatus): Boolean

    class Started(op: Operation): OperationStatusState(op, STARTED) {
        override fun userCanUpdateStatus(userId: Int, newStatus: OperationStatus) =
            newStatus == CANCELLED ||userId == op.user.id
    }

    class InterestedUserDeposit(op: Operation): OperationStatusState(op, INTERESTED_USER_DEPOSIT) {
        override fun userCanUpdateStatus(userId: Int, newStatus: OperationStatus) =
            newStatus == CANCELLED || userId == op.advertisement.user?.id
    }

    class Completed(op: Operation): OperationStatusState(op, COMPLETED) {
        override fun isClosed(): Boolean = true
        override fun userCanUpdateStatus(userId: Int, newStatus: OperationStatus) =
            false
    }

    class Cancelled(op: Operation): OperationStatusState(op, CANCELLED) {
        override fun isClosed(): Boolean = true
        override fun userCanUpdateStatus(userId: Int, newStatus: OperationStatus) =
            false
    }

}

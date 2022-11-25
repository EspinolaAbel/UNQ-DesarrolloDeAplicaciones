package ar.edu.unq.desapp.grupoN.desapp.model

import ar.edu.unq.desapp.grupoN.desapp.model.dto.OperationDTO
import org.hibernate.annotations.GenericGenerator
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
    //val symbol: Symbol,
    /** "...el usuario indicará el precio del critpoactivo al que desea vender o comprar" */
    val price: Double,
    val operation: OperationType,
    var timestamp: Instant,
    @Column(columnDefinition = "VARCHAR(20)")
    @Enumerated(EnumType.STRING)
    var status: OperationStatus = OperationStatus.IN_PROGRESS
) {
}

enum class OperationType {
    BUY, SELL, CANCEL
}

enum class OperationStatus {
    IN_PROGRESS, FINISHED
}

package ar.edu.unq.desapp.grupoN.desapp.model

import org.hibernate.annotations.GenericGenerator
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity
data class Advertisement(
    @Id @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "BINARY(16)")
    var id: UUID?,
    @OneToOne
    var user: User?,
    @Column(columnDefinition = "VARCHAR(20)")
    @Enumerated(EnumType.STRING)
    var operationType: OperationType,
    var symbol: Symbol,
    var cryptoAmount: Double,
    @Embedded
    @AttributeOverride( name = "currency", column = Column(name = "priceCurrency"))
    @AttributeOverride( name = "value", column = Column(name = "priceValue"))
    var cryptoPrice: CurrencyAmount,
    var active: Boolean = true,
    var creationTimestamp: Instant = Instant.now(),
) {
    fun setAsCompleted() {
        this.active = false
    }
}

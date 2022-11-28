package ar.edu.unq.desapp.grupoN.desapp.model

import org.hibernate.annotations.GenericGenerator
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
    var cryptoPrice: Double,
    var fiatPrice: Double,
    var active: Boolean = true
) {
    fun setAsCompleted() {
        this.active = false
    }
}

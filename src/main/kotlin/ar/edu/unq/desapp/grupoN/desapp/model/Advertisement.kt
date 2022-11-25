package ar.edu.unq.desapp.grupoN.desapp.model

import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToOne

@Entity
data class Advertisement(
    @Id @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "BINARY(16)")
    var id: UUID?,
    @OneToOne
    var user: User?,
    var operationType: OperationType,
    var symbol: Symbol,
    var cryptoAmount: Double,
    var cryptoPrice: Double,
    var fiatPrice: Double
)

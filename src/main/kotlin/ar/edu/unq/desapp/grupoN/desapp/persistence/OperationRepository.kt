package ar.edu.unq.desapp.grupoN.desapp.persistence

import ar.edu.unq.desapp.grupoN.desapp.model.Operation
import ar.edu.unq.desapp.grupoN.desapp.model.OperationStatus
import ar.edu.unq.desapp.grupoN.desapp.model.dto.OperationView
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OperationRepository: JpaRepository<Operation, UUID> {

    @Query("select " +
            "o.timestamp as timestamp, " +
            "o.advertisement.id as advertisement, " +
            "o.advertisement.symbol as crypto, " +
            "o.advertisement.cryptoAmount as amount, " +
            "o.advertisement.cryptoPrice as price, " +
            "concat(o.advertisement.user.name, ' ', o.advertisement.user.lastName) as userName, " +
            "-1 as operations, " + // TODO
            "'' as reputation " + // TODO
        "from Operation o " +
            "where o.status = 'IN_PROGRESS'")
    fun findAllProjection(): List<OperationView>

    fun existsByAdvertisement_IdAndStatus(advertisement: UUID?, inProgress: OperationStatus): Boolean

}

package ar.edu.unq.desapp.grupoN.desapp.persistence

import ar.edu.unq.desapp.grupoN.desapp.model.Operation
import ar.edu.unq.desapp.grupoN.desapp.model.dto.OperationView
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OperationRepository: JpaRepository<Operation, UUID> {

    @Query("select " +
            "o.creationTimestamp as creationTimestamp, " +
            "o.updateTimestamp as updateTimestamp, " +
            "o.advertisement.id as advertisement, " +
            "o.advertisement.symbol as crypto, " +
            "o.advertisement.cryptoAmount as amount, " +
            "o.advertisement.cryptoPrice as price, " +
            "concat(o.advertisement.user.name, ' ', o.advertisement.user.lastName) as userName, " +
            "-1 as operations, " + // TODO
            "'' as reputation " + // TODO
        "from Operation o " +
            "where o.status not in ('COMPLETED', 'CANCELLED')")
    fun findAllProjection(): List<OperationView>

}

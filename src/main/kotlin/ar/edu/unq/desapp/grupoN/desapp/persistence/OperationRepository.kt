package ar.edu.unq.desapp.grupoN.desapp.persistence

import ar.edu.unq.desapp.grupoN.desapp.model.Operation
import ar.edu.unq.desapp.grupoN.desapp.model.dto.CryptoVolumeView
import ar.edu.unq.desapp.grupoN.desapp.model.dto.OperationView
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OperationRepository: JpaRepository<Operation, UUID> {

    @Query(
        "select " +
            "o.creationTimestamp as creationTimestamp, " +
            "o.updateTimestamp as updateTimestamp, " +
            "o.advertisement.id as id, " +
            "o.advertisement.symbol as crypto, " +
            "o.advertisement.cryptoAmount as amount, " +
            "o.advertisement.cryptoPrice as price " +
        "from Operation o " +
        "where o.status not in ('COMPLETED', 'CANCELLED') and (o.user.id = :userId or o.advertisement.user.id = :userId)")
    fun findActiveOperations(@Param("userId") userId: Int): List<OperationView>

    @Query(
        "select " +
            "o.advertisement.symbol as crypto, " +
            "sum(o.advertisement.cryptoAmount) as nominalAmount " +
        "from Operation o " +
        "where o.user.id = :userId or o.advertisement.user.id = :userId " +
        "group by o.advertisement.symbol ")
    fun userVolume(@Param("userId") userId: Int): List<CryptoVolumeView>

}

package ar.edu.unq.desapp.grupoN.desapp.persistence

import ar.edu.unq.desapp.grupoN.desapp.model.Advertisement
import ar.edu.unq.desapp.grupoN.desapp.service.dto.AdvertisementView
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AdvertisementRepository: JpaRepository<Advertisement, UUID> {

    @Query(
        "from Advertisement ad " +
        "where " +
            "ad.id = :adUUID " +
            "and ad.active = true " +
            "and ad.id not in (" +
                "select op.advertisement.id from Operation op where op.advertisement.id = ad.id and op.status != 'CANCELLED' " +
            ") "
    )
    fun findActiveAndNotInUse(@Param("adUUID")adUUID: UUID): Optional<Advertisement>

    @Query(
        "select " +
            "a.creationTimestamp as creationTimestamp, " +
            "a.id as id, " +
            "a.symbol as crypto, " +
            "a.cryptoAmount as amount, " +
            "a.cryptoPrice as price " +
        "from Advertisement a " +
        "where a.active = true and a.user.id = :userId")
    fun findActiveAdvertisements(@Param("userId") userId: Int): List<AdvertisementView>

    @Query("select ad from Operation op right join op.advertisement ad where op is null")
    fun findAvailable(): List<Advertisement>

}

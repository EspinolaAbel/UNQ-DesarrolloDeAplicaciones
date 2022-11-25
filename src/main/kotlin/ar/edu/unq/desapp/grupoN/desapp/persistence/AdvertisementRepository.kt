package ar.edu.unq.desapp.grupoN.desapp.persistence

import ar.edu.unq.desapp.grupoN.desapp.model.Advertisement
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AdvertisementRepository: JpaRepository<Advertisement, UUID> {

    @Query("select ad from Operation op right join op.advertisement ad where op is null")
    fun findAvailable(): List<Advertisement>
}

package ar.edu.unq.desapp.grupoN.desapp.model.mapping

import ar.edu.unq.desapp.grupoN.desapp.model.Advertisement
import ar.edu.unq.desapp.grupoN.desapp.model.CurrencyAmount
import ar.edu.unq.desapp.grupoN.desapp.model.CurrencyCode
import ar.edu.unq.desapp.grupoN.desapp.model.dto.AdvertisementResponseDTO
import ar.edu.unq.desapp.grupoN.desapp.model.dto.CreateAdvertisementDTO
import ar.edu.unq.desapp.grupoN.desapp.model.dto.UserAdvertisementResponseDTO
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import org.mapstruct.Named

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface AdvertisementMapper {

    companion object {
        @JvmStatic @Named("getCurrencyPriceUSD")
        fun getCurrencyPriceUSD(price: Double): CurrencyAmount =
            CurrencyAmount(CurrencyCode.USD, price)
    }

    @Mapping(target="fiatPrice", ignore = true)
    @Mapping(source="user.id", target="userId")
    fun toFullDto(model: Advertisement): AdvertisementResponseDTO

    @Mapping(source="user.id", target="userId")
    @Mapping(target="userName", expression = "java(model.getUser().getName() +\" \"+ model.getUser().getLastName())")
    @Mapping(source="user.closedOperations", target="operations")
    @Mapping(
        source="user.reputation", target="reputation",
        conditionExpression = "java(model.getUser().getClosedOperations() > 0)",
        defaultValue = "Sin operaciones"
    )
    @Mapping(target="fiatPrice", ignore = true)
    fun toUserAdvertisementDTO(model: Advertisement): UserAdvertisementResponseDTO

    @Mapping(target="id", ignore = true)
    @Mapping(target="user", ignore = true)
    @Mapping(target="active", ignore = true)
    @Mapping(target="creationTimestamp", expression = "java(java.time.Instant.now())")
    @Mapping(source="cryptoPrice", target="cryptoPrice", qualifiedByName = ["getCurrencyPriceUSD"])
    fun toNewModel(dto: CreateAdvertisementDTO): Advertisement

    @Mapping(target="user", ignore = true)
    @Mapping(target="active", ignore = true)
    @Mapping(target="creationTimestamp", ignore = true)
    fun toModel(dto: AdvertisementResponseDTO): Advertisement
}
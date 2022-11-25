package ar.edu.unq.desapp.grupoN.desapp.model.mapping

import ar.edu.unq.desapp.grupoN.desapp.model.Advertisement
import ar.edu.unq.desapp.grupoN.desapp.model.dto.AdvertisementDTO
import ar.edu.unq.desapp.grupoN.desapp.model.dto.AdvertisementFullDTO
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface AdvertisementMapper {
    @Mapping(source="user.id", target="userId")
    fun toDto(model: Advertisement): AdvertisementDTO

    @Mapping(source="user.id", target="userId")
    fun toFullDto(model: Advertisement): AdvertisementFullDTO

    @Mapping(target="id", ignore = true)
    @Mapping(target="user", ignore = true)
    fun toNewModel(dto: AdvertisementDTO): Advertisement

    @Mapping(target="user", ignore = true)
    fun toModel(dto: AdvertisementFullDTO): Advertisement
}
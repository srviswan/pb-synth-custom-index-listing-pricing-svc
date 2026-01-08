package com.pb.synth.cib.basket.mapper;

import com.pb.synth.cib.basket.dto.BasketDto;
import com.pb.synth.cib.basket.entity.Basket;
import com.pb.synth.cib.basket.entity.Constituent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BasketMapper {

    BasketDto toDto(Basket basket);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "basket", ignore = true)
    @Mapping(target = "asOf", ignore = true)
    Constituent toEntity(BasketDto.ConstituentDto dto);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "constituents", ignore = true)
    Basket toEntity(BasketDto dto);
}

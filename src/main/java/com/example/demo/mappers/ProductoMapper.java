package com.example.demo.mappers;

import com.example.demo.dtos.producto.ProductoRequestDTO;
import com.example.demo.dtos.producto.ProductoResponseDTO;
import com.example.demo.models.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    public Producto toEntity(ProductoRequestDTO dto){
        if (dto == null) return null;

        return Producto.builder()
                .codigoBarras(dto.getCodigoBarras())
                .nombre(dto.getNombre())
                .unidadMedida(dto.getUnidadMedida())
                .precio(dto.getPrecio())
                .stock(dto.getStock())
                .activo(dto.getActivo())
                .build();
    }

    public ProductoResponseDTO toResponseDTO(Producto entity){
        if (entity == null) return null;

        return ProductoResponseDTO.builder()
                .id(entity.getId())
                .codigoBarras(entity.getCodigoBarras())
                .nombre(entity.getNombre())
                .unidadMedida(entity.getUnidadMedida())
                .precio(entity.getPrecio())
                .stock(entity.getStock())
                .activo(entity.getActivo())
                .build();
    }

}

package com.example.demo.mappers;

import com.example.demo.dtos.cliente.ClienteRequestDTO;
import com.example.demo.dtos.cliente.ClienteResponseDTO;
import com.example.demo.models.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public Cliente toEntity(ClienteRequestDTO dto){
        if (dto == null) return null;

        return Cliente.builder()
                .dni(dto.getDni())
                .nombres(dto.getNombres())
                .apellidos(dto.getApellidos())
                .telefono(dto.getTelefono())
                .activo(dto.getActivo())
                .build();
    }

    public ClienteResponseDTO toResponseDTO(Cliente entity){
        if (entity == null) return null;

        return ClienteResponseDTO.builder()
                .id(entity.getId())
                .dni(entity.getDni())
                .nombres(entity.getNombres())
                .apellidos(entity.getApellidos())
                .telefono(entity.getTelefono())
                .activo(entity.getActivo())
                .build();
    }

}

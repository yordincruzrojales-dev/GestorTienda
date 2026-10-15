package com.example.demo.services;

import com.example.demo.dtos.cliente.ClienteRequestDTO;
import com.example.demo.dtos.cliente.ClienteResponseDTO;
import com.example.demo.exceptions.DuplicateResourceException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.ClienteMapper;
import com.example.demo.models.Cliente;
import com.example.demo.repositories.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Transactional
    public ClienteResponseDTO registrarCliente(ClienteRequestDTO clienteRequestDTO){

        if (clienteRepository.existsByDni(clienteRequestDTO.getDni())){
            throw new DuplicateResourceException("Ya existe un cliente registrado con dni: " + clienteRequestDTO.getDni());
        }

        Cliente clienteNuevo = clienteMapper.toEntity(clienteRequestDTO);

        Cliente clienteGuardado = clienteRepository.save(clienteNuevo);

        return clienteMapper.toResponseDTO(clienteGuardado);
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorDni(String dni){
        Cliente clienteEncontrado = clienteRepository.findByDniAndActivoTrue(dni)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con dni: " + dni));

        return clienteMapper.toResponseDTO(clienteEncontrado);
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorIdDTO(Long id){
        Cliente clienteEncontrado = clienteRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con Id: " + id));

        return clienteMapper.toResponseDTO(clienteEncontrado);
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id){
        return clienteRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con Id: " + id));
    }

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarClientes(){
        return clienteRepository.findByActivoTrue()
                .stream().map(clienteMapper::toResponseDTO).toList();
    }

    @Transactional
    public ClienteResponseDTO actualizarCliente(Long id, ClienteRequestDTO cliente){

        Cliente clienteExistente = buscarPorId(id);

        if (!clienteExistente.getDni().equals(cliente.getDni())
                && clienteRepository.existsByDni(cliente.getDni())) {
            throw new DuplicateResourceException("El nuevo DNI ya se encuentra registrado por otro cliente.");
        }

        if (cliente.getDni() != null){
            clienteExistente.setDni(cliente.getDni());
        }

        if (cliente.getNombres() != null){
            clienteExistente.setNombres(cliente.getNombres());
        }

        if (cliente.getApellidos() != null){
            clienteExistente.setApellidos(cliente.getApellidos());
        }

        if (cliente.getTelefono() != null){
            clienteExistente.setTelefono(cliente.getTelefono());
        }

        if (cliente.getActivo() != null){
            clienteExistente.setActivo(cliente.getActivo());
        }

        Cliente clienteActualizado = clienteRepository.save(clienteExistente);

        return clienteMapper.toResponseDTO(clienteActualizado);
    }

    @Transactional
    public void desactivarCliente(Long id){
        if (!clienteRepository.existsByIdAndActivoTrue(id)) {
            throw new ResourceNotFoundException("No se puede Desactivar. El cliente con ID " + id + " no existe");
        }

        Cliente cliente = buscarPorId(id);
        cliente.setActivo(false);

        clienteRepository.save(cliente);
    }

    @Transactional
    public void reactivarCliente(Long id){
        Cliente cliente = clienteRepository.findByIdAndActivoFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede reactivar, ya que no existe o ya estra activo"));
        cliente.setActivo(true);

        clienteRepository.save(cliente);
    }
}

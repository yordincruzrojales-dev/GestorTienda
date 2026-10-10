package com.example.demo.services;

import com.example.demo.exceptions.DuplicateResourceException;
import com.example.demo.exceptions.ResourceNotFoundException;
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

    @Transactional
    public Cliente registrarCliente(Cliente cliente){
        cliente.setActivo(true);

        if (clienteRepository.existsByDniAndActivoTrue(cliente.getDni())){
            throw new DuplicateResourceException("Ya existe un cliente registrado con dni: " + cliente.getDni());
        }

        return clienteRepository.save(cliente);
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorDni(String dni){
        return clienteRepository.findByDniAndActivoTrue(dni)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con dni: " + dni));
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id){
        return clienteRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con Id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarClientes(){
        return clienteRepository.findByActivoTrue();
    }

    @Transactional
    public Cliente actualizarCliente(Long id, Cliente cliente){

        Cliente clienteExistente = buscarPorId(id);

        if (!clienteExistente.getDni().equals(cliente.getDni())
                && clienteRepository.existsByDniAndActivoTrue(cliente.getDni())) {
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

        return clienteRepository.save(clienteExistente);
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

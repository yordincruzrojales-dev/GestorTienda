package com.example.demo.services;

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
            throw new IllegalArgumentException("Ya existe un cliente registrado con dni: " + cliente.getDni());
        }

        return clienteRepository.save(cliente);
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorDni(String dni){
        return clienteRepository.findByDniAndActivoTrue(dni)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con dni: " + dni));
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id){
        return clienteRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con Id: " + id));
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
            throw new IllegalArgumentException("El nuevo DNI ya se encuentra registrado por otro cliente.");
        }

        clienteExistente.setDni(cliente.getDni());
        clienteExistente.setNombres(cliente.getNombres());
        clienteExistente.setApellidos(cliente.getApellidos());
        clienteExistente.setTelefono(cliente.getTelefono());

        if(cliente.getActivo() != null) {
            clienteExistente.setActivo(cliente.getActivo());
        }

        return clienteRepository.save(clienteExistente);
    }

    @Transactional
    public void desactivarCliente(Long id){
        if (!clienteRepository.existsByIdAndActivoTrue(id)) {
            throw new RuntimeException("No se puede eliminar. El cliente con ID " + id + " no existe");
        }

        Cliente cliente = buscarPorId(id);
        cliente.setActivo(false);

        clienteRepository.save(cliente);
    }
}

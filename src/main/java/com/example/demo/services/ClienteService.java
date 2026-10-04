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
        if (clienteRepository.existsByDni(cliente.getDni())){
            throw new IllegalArgumentException("Ya existe un cliente registrado con dni: " + cliente.getDni());
        }

        return clienteRepository.save(cliente);
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorDni(String dni){
        return clienteRepository.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con dni: " + dni));
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id){
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con Id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarClientes(){
        return clienteRepository.findAll();
    }

    @Transactional
    public Cliente actualizarCliente(Cliente cliente){

        Cliente clienteExistente = buscarPorId(cliente.getId());

        if (!clienteExistente.getDni().equals(cliente.getDni())
                && clienteRepository.existsByDni(cliente.getDni())) {
            throw new IllegalArgumentException("El nuevo DNI ya se encuentra registrado por otro cliente.");
        }

        return clienteRepository.save(cliente);
    }

    @Transactional
    public void eliminar(Long id){
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar. El cliente con ID " + id + " no existe");
        }

        clienteRepository.deleteById(id);
    }
}

package com.example.demo.controllers;

import com.example.demo.models.Cliente;
import com.example.demo.services.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.smartcardio.ResponseAPDU;
import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<Cliente> registrarCliente(@RequestBody Cliente cliente){
        Cliente nuevoCliente = clienteService.registrarCliente(cliente);
        return new ResponseEntity<>(nuevoCliente, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes(){
        List<Cliente> clientes = clienteService.listarClientes();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarCliente(@PathVariable Long id){
        Cliente cliente = clienteService.buscarPorId(id);
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/buscar")
    public ResponseEntity<Cliente> buscarPorDni(@RequestParam String dni){
        Cliente cliente = clienteService.buscarPorDni(dni);
        return ResponseEntity.ok(cliente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizarCliente(@PathVariable Long id, @RequestBody Cliente cliente){
        Cliente clienteModificado = clienteService.actualizarCliente(id, cliente);
        return ResponseEntity.ok(clienteModificado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivarCliente(@PathVariable Long id){
        clienteService.desactivarCliente(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> reactivarCliente(@PathVariable Long id){
        clienteService.reactivarCliente(id);
        return ResponseEntity.noContent().build();
    }
}

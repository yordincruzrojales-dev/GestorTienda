package com.example.demo.repositories;

import com.example.demo.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existeDni(String dni);

    Optional<Cliente> buscarPorDni(String dni);

}

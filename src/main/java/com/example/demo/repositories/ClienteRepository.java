package com.example.demo.repositories;

import com.example.demo.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByDni(String dni);

    Optional<Cliente> findByDni(String dni);

}
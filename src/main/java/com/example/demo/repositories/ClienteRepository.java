package com.example.demo.repositories;

import com.example.demo.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByDniAndActivoTrue(String dni);
    boolean existsByIdAndActivoTrue(Long id);

    List<Cliente> findByActivoTrue();
    List<Cliente> findByActivoFalse();

    Optional<Cliente> findByDniAndActivoTrue(String dni);
    Optional<Cliente> findByIdAndActivoTrue(Long id);

}
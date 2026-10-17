package com.example.demo.repositories;

import com.example.demo.models.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Pruebas de Persistencia para ClienteRepository")
class ClienteRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ClienteRepository clienteRepository;

    private Cliente clienteActivo;
    private Cliente clienteInactivo;

    @BeforeEach
    void setUp() {
        clienteActivo = Cliente.builder()
                .dni("12345678")
                .nombres("Juan")
                .apellidos("Pérez")
                .telefono("987654321")
                .activo(true)
                .build();

        clienteInactivo = Cliente.builder()
                .dni("87654321")
                .nombres("Maria")
                .apellidos("Lopez")
                .telefono("912345678")
                .activo(false)
                .build();
    }

    @Test
    @DisplayName("Debe guardar y buscar un cliente activo por DNI")
    void findByDniAndActivoTrue_Exito() {
        // ARRANGE
        entityManager.persistAndFlush(clienteActivo);

        // ACT
        Optional<Cliente> resultado = clienteRepository.findByDniAndActivoTrue("12345678");

        // ASSERT
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getDni()).isEqualTo("12345678");
        assertThat(resultado.get().getNombres()).isEqualTo("Juan");
        assertThat(resultado.get().getActivo()).isTrue();
    }

    @Test
    @DisplayName("No debe retornar el cliente si está inactivo")
    void findByDniAndActivoTrue_ClienteInactivo_RetornaOptionalVacio() {
        // ARRANGE
        entityManager.persistAndFlush(clienteInactivo);

        // ACT
        Optional<Cliente> resultado = clienteRepository.findByDniAndActivoTrue("87654321");

        // ASSERT
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Debe retornar True cuando existe un cliente con el DNI especificado")
    void existsByDni_RetornaTrue() {
        // ARRANGE
        entityManager.persistAndFlush(clienteActivo);

        // ACT
        boolean existe = clienteRepository.existsByDni("12345678");

        // ASSERT
        assertThat(existe).isTrue();
    }
}
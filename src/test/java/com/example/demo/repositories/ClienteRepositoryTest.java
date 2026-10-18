package com.example.demo.repositories;

import com.example.demo.models.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@DisplayName("Pruebas del Repositorio de Clientes")
class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository clienteRepository;

    private Cliente clienteActivo;
    private Cliente clienteInactivo;

    @BeforeEach
    void setUp() {
        clienteRepository.deleteAll();

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
                .apellidos("Gómez")
                .telefono("912345678")
                .activo(false)
                .build();
    }

    @Nested
    @DisplayName("Métodos ExistBy")
    class ExistsByTests {

        @Test
        @DisplayName("Debe retornar true al verificar existencia de DNI activo cuando existe")
        void existsByDniAndActivoTrue_DeberiaRetornarTrue() {
            clienteRepository.save(clienteActivo);

            boolean existe = clienteRepository.existsByDniAndActivoTrue("12345678");

            assertThat(existe).isTrue();
        }

        @Test
        @DisplayName("Debe retornar false al verificar DNI activo si el cliente está inactivo")
        void existsByDniAndActivoTrue_ClienteInactivo_DeberiaRetornarFalse() {
            clienteRepository.save(clienteInactivo);

            boolean existe = clienteRepository.existsByDniAndActivoTrue("87654321");

            assertThat(existe).isFalse();
        }

        @Test
        @DisplayName("Debe retornar true si el DNI existe independientemente del estado activo/inactivo")
        void existsByDni_DeberiaRetornarTrue() {
            clienteRepository.save(clienteInactivo);

            boolean existe = clienteRepository.existsByDni("87654321");

            assertThat(existe).isTrue();
        }

        @Test
        @DisplayName("Debe retornar true al verificar por ID si el cliente está activo")
        void existsByIdAndActivoTrue_DeberiaRetornarTrue() {
            Cliente guardado = clienteRepository.save(clienteActivo);

            boolean existe = clienteRepository.existsByIdAndActivoTrue(guardado.getId());

            assertThat(existe).isTrue();
        }

        @Test
        @DisplayName("Debe retornar true al verificar por ID si el cliente está inactivo")
        void existsByIdAndActivoFalse_DeberiaRetornarTrue() {
            Cliente guardado = clienteRepository.save(clienteInactivo);

            boolean existe = clienteRepository.existsByIdAndActivoFalse(guardado.getId());

            assertThat(existe).isTrue();
        }
    }

    @Nested
    @DisplayName("Métodos FindBy Listas")
    class FindByListTests {

        @Test
        @DisplayName("Debe listar únicamente los clientes activos")
        void findByActivoTrue_DeberiaRetornarListaActivos() {
            clienteRepository.save(clienteActivo);
            clienteRepository.save(clienteInactivo);

            List<Cliente> activos = clienteRepository.findByActivoTrue();

            assertThat(activos)
                    .hasSize(1)
                    .extracting(Cliente::getDni)
                    .containsExactly("12345678");
        }

        @Test
        @DisplayName("Debe listar únicamente los clientes inactivos")
        void findByActivoFalse_DeberiaRetornarListaInactivos() {
            clienteRepository.save(clienteActivo);
            clienteRepository.save(clienteInactivo);

            List<Cliente> inactivos = clienteRepository.findByActivoFalse();

            assertThat(inactivos)
                    .hasSize(1)
                    .extracting(Cliente::getDni)
                    .containsExactly("87654321");
        }
    }

    @Nested
    @DisplayName("Métodos FindBy Optional")
    class FindByOptionalTests {

        @Test
        @DisplayName("Debe buscar cliente activo por DNI exitosamente")
        void findByDniAndActivoTrue_DeberiaEncontrarCliente() {
            clienteRepository.save(clienteActivo);

            Optional<Cliente> resultado = clienteRepository.findByDniAndActivoTrue("12345678");

            assertThat(resultado).isPresent();
            assertThat(resultado.get().getNombres()).isEqualTo("Juan");
        }

        @Test
        @DisplayName("No debe retornar cliente por DNI si este está inactivo")
        void findByDniAndActivoTrue_ClienteInactivo_DeberiaRetornarEmpty() {
            clienteRepository.save(clienteInactivo);

            Optional<Cliente> resultado = clienteRepository.findByDniAndActivoTrue("87654321");

            assertThat(resultado).isEmpty();
        }

        @Test
        @DisplayName("Debe buscar por ID y activo=true cuando existe")
        void findByIdAndActivoTrue_DeberiaEncontrarCliente() {
            Cliente guardado = clienteRepository.save(clienteActivo);

            Optional<Cliente> resultado = clienteRepository.findByIdAndActivoTrue(guardado.getId());

            assertThat(resultado).isPresent();
            assertThat(resultado.get().getDni()).isEqualTo("12345678");
        }

        @Test
        @DisplayName("Debe buscar por ID y activo=false cuando existe")
        void findByIdAndActivoFalse_DeberiaEncontrarCliente() {
            Cliente guardado = clienteRepository.save(clienteInactivo);

            Optional<Cliente> resultado = clienteRepository.findByIdAndActivoFalse(guardado.getId());

            assertThat(resultado).isPresent();
            assertThat(resultado.get().getDni()).isEqualTo("87654321");
        }
    }
}
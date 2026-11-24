package br.com.baozistore.controller;

import br.com.baozistore.model.Cliente;
import br.com.baozistore.repository.ClienteRepository;
import br.com.baozistore.repository.PedidoRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;

    public ClienteController(ClienteRepository clienteRepository, PedidoRepository pedidoRepository) {
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
    }

    /** POST /clientes - cadastra um cliente. */
    @PostMapping
    public ResponseEntity<Cliente> criar(@Valid @RequestBody Cliente cliente) {
        cliente.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteRepository.save(cliente));
    }

    /** GET /clientes - lista todos os clientes. */
    @GetMapping
    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    /** GET /clientes/{id} - consulta um cliente pelo ID. */
    @GetMapping("/{id}")
    public Cliente buscarPorId(@PathVariable Long id) {
        return buscar(id);
    }

    /** PUT /clientes/{id} - atualiza um cliente (opcional no enunciado). */
    @PutMapping("/{id}")
    public Cliente atualizar(@PathVariable Long id, @Valid @RequestBody Cliente dados) {
        Cliente cliente = buscar(id);
        cliente.setNome(dados.getNome());
        if (dados.getClienteDesde() != null) {
            cliente.setClienteDesde(dados.getClienteDesde());
        }
        return clienteRepository.save(cliente);
    }

    /** DELETE /clientes/{id} - apaga um cliente. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        buscar(id);
        if (pedidoRepository.existsByClienteId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cliente possui pedidos registrados; apague os pedidos antes");
        }
        clienteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Cliente buscar(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cliente " + id + " não encontrado"));
    }
}

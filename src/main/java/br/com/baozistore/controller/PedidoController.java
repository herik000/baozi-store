package br.com.baozistore.controller;

import br.com.baozistore.model.Cliente;
import br.com.baozistore.model.Pedido;
import br.com.baozistore.model.PedidoRequest;
import br.com.baozistore.model.Produto;
import br.com.baozistore.repository.ClienteRepository;
import br.com.baozistore.repository.PedidoRepository;
import br.com.baozistore.repository.ProdutoRepository;
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
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoController(PedidoRepository pedidoRepository,
                            ClienteRepository clienteRepository,
                            ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    /** POST /pedidos - registra um pedido: { "clienteId", "produtoId", "quantidade" }. */
    @PostMapping
    public ResponseEntity<Pedido> criar(@Valid @RequestBody PedidoRequest dados) {
        Pedido pedido = new Pedido(buscarCliente(dados.clienteId()),
                buscarProduto(dados.produtoId()), dados.quantidade());
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoRepository.save(pedido));
    }

    /** GET /pedidos - lista todos os pedidos. */
    @GetMapping
    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    /** GET /pedidos/{id} - consulta um pedido pelo ID. */
    @GetMapping("/{id}")
    public Pedido buscarPorId(@PathVariable Long id) {
        return buscar(id);
    }

    /** PUT /pedidos/{id} - atualiza um pedido (opcional no enunciado). */
    @PutMapping("/{id}")
    public Pedido atualizar(@PathVariable Long id, @Valid @RequestBody PedidoRequest dados) {
        Pedido pedido = buscar(id);
        pedido.setCliente(buscarCliente(dados.clienteId()));
        pedido.setProduto(buscarProduto(dados.produtoId()));
        pedido.setQuantidade(dados.quantidade());
        return pedidoRepository.save(pedido);
    }

    /** DELETE /pedidos/{id} - apaga um pedido. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        buscar(id);
        pedidoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Pedido buscar(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Pedido " + id + " não encontrado"));
    }

    private Cliente buscarCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Cliente " + id + " não existe"));
    }

    private Produto buscarProduto(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Produto " + id + " não existe"));
    }
}

package br.com.baozistore.controller;

import br.com.baozistore.model.Produto;
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
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoRepository produtoRepository;
    private final PedidoRepository pedidoRepository;

    public ProdutoController(ProdutoRepository produtoRepository, PedidoRepository pedidoRepository) {
        this.produtoRepository = produtoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    /** POST /produtos - cadastra um produto. */
    @PostMapping
    public ResponseEntity<Produto> criar(@Valid @RequestBody Produto produto) {
        produto.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoRepository.save(produto));
    }

    /** GET /produtos - lista todos os produtos. */
    @GetMapping
    public List<Produto> listar() {
        return produtoRepository.findAll();
    }

    /** GET /produtos/{id} - consulta um produto pelo ID. */
    @GetMapping("/{id}")
    public Produto buscarPorId(@PathVariable Long id) {
        return buscar(id);
    }

    /** PUT /produtos/{id} - atualiza um produto (opcional no enunciado). */
    @PutMapping("/{id}")
    public Produto atualizar(@PathVariable Long id, @Valid @RequestBody Produto dados) {
        Produto produto = buscar(id);
        produto.setNome(dados.getNome());
        produto.setPreco(dados.getPreco());
        produto.setEstoque(dados.getEstoque());
        return produtoRepository.save(produto);
    }

    /** DELETE /produtos/{id} - apaga um produto. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        buscar(id);
        if (pedidoRepository.existsByProdutoId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Produto possui pedidos registrados; apague os pedidos antes");
        }
        produtoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Produto buscar(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Produto " + id + " não encontrado"));
    }
}

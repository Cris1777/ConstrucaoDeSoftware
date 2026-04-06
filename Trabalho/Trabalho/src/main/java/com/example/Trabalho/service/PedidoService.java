package com.example.Trabalho.service;

import com.example.Trabalho.model.ItemPedido;
import com.example.Trabalho.model.Pedido;
import com.example.Trabalho.model.Produto;
import com.example.Trabalho.model.StatusPedido;
import com.example.Trabalho.repository.PedidoRepository;
import com.example.Trabalho.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(PedidoRepository pedidoRepository, ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public Pedido criarPedido(Pedido pedido) {

        double total = 0.0;

        for (ItemPedido item : pedido.getItens()) {

            Produto produto = produtoRepository.findById(item.getProduto().getId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            if (produto.getEstoque() < item.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente");
            }

            produto.setEstoque(produto.getEstoque() - item.getQuantidade());

            item.setPrecoUnitario(produto.getPreco());
            item.setPedido(pedido);

            total += item.getQuantidade() * produto.getPreco();
        }

        pedido.setTotal(total);
        pedido.setStatus(StatusPedido.CRIADO);
        pedido.setData(LocalDateTime.now());

        return pedidoRepository.save(pedido);
    }

    public void cancelarPedido(Long id) {

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow();

        if (pedido.getStatus() != StatusPedido.CRIADO) {
            throw new RuntimeException("Pedido não pode ser cancelado");
        }

        pedido.setStatus(StatusPedido.CANCELADO);
    }

    public void atualizarStatus(Long id, StatusPedido novoStatus) {

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow();

        if (pedido.getStatus() == StatusPedido.CRIADO && novoStatus == StatusPedido.PAGO) {
            pedido.setStatus(novoStatus);
        } else if (pedido.getStatus() == StatusPedido.PAGO && novoStatus == StatusPedido.ENVIADO) {
            pedido.setStatus(novoStatus);
        } else {
            throw new RuntimeException("Transição inválida");
        }
    }
}
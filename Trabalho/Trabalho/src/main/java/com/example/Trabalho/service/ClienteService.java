package com.example.Trabalho.service;

import com.example.Trabalho.model.Cliente;
import com.example.Trabalho.repository.ClienteRepository;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    public Cliente salvar(Cliente cliente) {

        if (cliente.getEmail() == null || cliente.getEmail().isEmpty()) {
            throw new RuntimeException("Email obrigatório");
        }

        repository.findByEmail(cliente.getEmail())
                .ifPresent(c -> {
                    throw new RuntimeException("Email já cadastrado");
                });

        return repository.save(cliente);
    }
}
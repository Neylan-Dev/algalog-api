package com.neylandev.delivery.application.controller;

import com.neylandev.delivery.application.request.ClientRequestDto;
import com.neylandev.delivery.application.response.ClientResponseDto;
import com.neylandev.delivery.domain.service.ClientService;
import lombok.RequiredArgsConstructor;
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

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public ResponseEntity<List<ClientResponseDto>> listAll() {
        return ResponseEntity.ok(clientService.findAll());
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<ClientResponseDto> findById(@PathVariable Long clientId) {
        return ResponseEntity.ok(clientService.findById(clientId));
    }

    @PostMapping
    public ResponseEntity<ClientResponseDto> create(@RequestBody @Valid ClientRequestDto clientRequestDto) {
        return new ResponseEntity<>(clientService.create(clientRequestDto), HttpStatus.CREATED);
    }

    @PutMapping("/{clientId}")
    public ResponseEntity<ClientResponseDto> update(@PathVariable Long clientId, @RequestBody @Valid ClientRequestDto clientRequestDto) {
        return ResponseEntity.ok().body(clientService.update(clientId, clientRequestDto));
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> delete(@PathVariable Long clientId) {
        clientService.delete(clientId);
        return ResponseEntity.noContent().build();
    }
}
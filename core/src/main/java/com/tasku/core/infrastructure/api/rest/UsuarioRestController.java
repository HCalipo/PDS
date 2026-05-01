package com.tasku.core.infrastructure.api.rest;

import com.tasku.core.application.usuario.usecase.UsuarioUseCaseService;
import com.tasku.core.domain.model.Usuario;
import com.tasku.core.infrastructure.api.rest.request.LoginUserApiRequest;
import com.tasku.core.infrastructure.api.rest.request.RegisterUserApiRequest;
import com.tasku.core.infrastructure.api.rest.response.UsuarioApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioRestController {

    private final UsuarioUseCaseService usuarioUseCaseService;

    public UsuarioRestController(UsuarioUseCaseService usuarioUseCaseService) {
        this.usuarioUseCaseService = usuarioUseCaseService;
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioApiResponse> registrarUsuario(@RequestBody RegisterUserApiRequest request) {
        Usuario u = usuarioUseCaseService.registrarUsuario(request.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(new UsuarioApiResponse(u.email(), u.registrationDate()));
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioApiResponse> iniciarSesion(@RequestBody LoginUserApiRequest request) {
        Usuario u = usuarioUseCaseService.iniciarSesion(request.email());
        return ResponseEntity.ok(new UsuarioApiResponse(u.email(), u.registrationDate()));
    }
}
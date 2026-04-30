package com.tasku.core.infrastructure.api.rest;

import com.tasku.core.application.usuario.usecase.UsuarioUseCaseService;
import com.tasku.core.domain.model.Usuario;
import com.tasku.core.infrastructure.api.rest.request.LoginUserApiRequest;
import com.tasku.core.infrastructure.api.rest.request.RegisterUserApiRequest;
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
    public ResponseEntity<Usuario> registrarUsuario(@RequestBody RegisterUserApiRequest request) {
        Usuario usuarioRegistrado = usuarioUseCaseService.registrarUsuario(request.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioRegistrado);
    }

    @PostMapping("/login")
    public ResponseEntity<Usuario> iniciarSesion(@RequestBody LoginUserApiRequest request) {
        Usuario usuario = usuarioUseCaseService.iniciarSesion(request.email());
        return ResponseEntity.ok(usuario);
    }
}
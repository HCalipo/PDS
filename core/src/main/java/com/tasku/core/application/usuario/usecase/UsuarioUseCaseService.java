package com.tasku.core.application.usuario.usecase;

import com.tasku.core.domain.board.exception.DomainConflictException;
import com.tasku.core.domain.board.exception.DomainNotFoundException;
import com.tasku.core.domain.board.port.UsuarioStore;
import com.tasku.core.domain.model.Email;
import com.tasku.core.domain.model.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioUseCaseService {

    private final UsuarioStore usuarioStore;

    public UsuarioUseCaseService(UsuarioStore usuarioStore) {
        this.usuarioStore = usuarioStore;
    }

    @Transactional
    public Usuario registrarUsuario(String emailStr) {

        Email emailValidado = new Email(emailStr);

       
        if (usuarioStore.findByEmail(emailValidado).isPresent()) {
            throw new DomainConflictException("Ya existe un usuario registrado con este correo electrónico.");
        }

        Usuario nuevoUsuario = Usuario.createNew(emailValidado);
        return usuarioStore.save(nuevoUsuario);
    }

}
package com.tasku.core.domain.board.port;

import com.tasku.core.domain.model.Usuario;
import com.tasku.core.domain.model.Email;

import java.util.Optional;

public interface UsuarioStore {
    Usuario save(Usuario user);

    Optional<Usuario> findByEmail(Email email);
}



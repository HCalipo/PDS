package com.tasku.core.domain.board.port;

import com.tasku.core.domain.model.board.CuentaUsuario;

import java.util.Optional;

public interface UsuarioStore {
    CuentaUsuario save(CuentaUsuario user);

    Optional<CuentaUsuario> findByEmail(String email);
}


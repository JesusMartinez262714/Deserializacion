package org.example;

import java.util.ArrayList;
import java.util.List;

public class BaseDeDatos {
    private List<String> usuariosRegistrados;

    public BaseDeDatos() {
        usuariosRegistrados = new ArrayList<>();


        usuariosRegistrados.add("invitado");
        usuariosRegistrados.add("jesus");
        usuariosRegistrados.add("paola");
        usuariosRegistrados.add("juan");
    }

    public boolean existeUsuario(String nombre) {
        return usuariosRegistrados.contains(nombre);
    }
}
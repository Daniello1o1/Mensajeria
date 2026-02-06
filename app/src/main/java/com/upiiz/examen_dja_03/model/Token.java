package com.upiiz.examen_dja_03.model;

public class Token {
    private String nombre;
    private String token;
    private String rol;

    public Token() {
    }

    public Token(String nombre, String token, String rol) {
        this.nombre = nombre;
        this.token = token;
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}

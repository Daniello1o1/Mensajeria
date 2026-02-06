package com.upiiz.examen_dja_03.model;

public class Usuario {

    private String uid;
    private String usuario;
    private String nombre;
    private String estado;

    private String token;

    public Usuario() {}

    public Usuario(String uid, String usuario, String nombre, String token) {
        this.uid = uid;
        this.usuario = usuario;
        this.nombre = nombre;
        this.estado = "offline";
        this.token = token;
    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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
}

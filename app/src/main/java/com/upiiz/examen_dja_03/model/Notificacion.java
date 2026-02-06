package com.upiiz.examen_dja_03.model;

public class Notificacion {
    private String title;
    private String body;

    public Notificacion(String titulo, String cuerpo) {
        this.title = titulo;
        this.body = cuerpo;
    }

    public String getTitulo() {
        return title;
    }

    public void setTitulo(String titulo) {
        this.title = titulo;
    }

    public String getCuerpo() {
        return body;
    }

    public void setCuerpo(String cuerpo) {
        this.body = cuerpo;
    }
}

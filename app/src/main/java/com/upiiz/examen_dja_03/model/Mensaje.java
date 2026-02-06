package com.upiiz.examen_dja_03.model;

public class Mensaje {

    private String emisor;
    private String texto;
    private long timestamp;

    public Mensaje() {
        // Firebase necesita constructor vacío
    }

    public Mensaje(String emisor, String texto, long timestamp) {
        this.emisor = emisor;
        this.texto = texto;
        this.timestamp = timestamp;
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

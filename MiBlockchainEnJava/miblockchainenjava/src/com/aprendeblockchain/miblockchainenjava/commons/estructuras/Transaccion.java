package com.aprendeblockchain.miblockchainenjava.commons.estructuras;

import com.google.common.primitives.Longs;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Date;

/*
 * La información principal en una transacción incluye:
 * 	- Hash de la transacción
 * 	- El emisor
 * 	- El destinatario
 * 	- La cantidad a ser transferida
 * 	- El timestamp de cuándo fue creada
 * 	- La firma con la clave privada del emisor
 * */

public class Transaccion {

    // Hash de la transacción e identificador único de ésta
    private byte[] hash;

    // Dirección (hash de la clave pÃºblica) del emisor de la transacción
    private byte[] emisor;

    // Dirección (hash de la clave pÃºblica) del destinatario de la transacción
    private byte[] destinatario;

    // Valor a ser transferido
    private double cantidad;

    // Firma con la clave privada para verificar que la transacción fue realmente enviada por el emisor
    private byte[] firma;

    // Timestamp de la creación de la transacción en milisegundos desde el 1/1/1970
    private long timestamp;

    public Transaccion() {
    }

    public Transaccion(byte[] emisor, byte[] receptor, double cantidad, byte[] firma) {
        this.emisor = emisor;
        this.destinatario = receptor;
        this.cantidad = cantidad;
        this.firma = firma;
        this.timestamp = System.currentTimeMillis();
        this.hash = calcularHashTransaccion();
    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public byte[] getEmisor() {
        return emisor;
    }

    public void setEmisor(byte[] emisor) {
        this.emisor = emisor;
    }

    public byte[] getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(byte[] destinatario) {
        this.destinatario = destinatario;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public byte[] getFirma() {
        return firma;
    }

    public void setFirma(byte[] firma) {
        this.firma = firma;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public byte[] getSignableData() {
        return String.valueOf(cantidad).getBytes();
    }

    /**
     * Calcular el hash del contenido de la transacción
     * @return Hash SHA256
     */
    public byte[] calcularHashTransaccion() {
        byte[] infoTransaccion = ArrayUtils.addAll(String.valueOf(cantidad).getBytes());
        infoTransaccion = ArrayUtils.addAll(infoTransaccion, emisor);
        infoTransaccion = ArrayUtils.addAll(infoTransaccion, destinatario);
        infoTransaccion = ArrayUtils.addAll(infoTransaccion, firma);
        infoTransaccion = ArrayUtils.addAll(infoTransaccion, Longs.toByteArray(timestamp));
        return DigestUtils.sha256(infoTransaccion);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transaccion tr = (Transaccion) o;

        return Arrays.equals(hash, tr.hash);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(hash);
    }

    @Override
    public String toString() {
        return "{" + hash + ", " + emisor + ", " + destinatario + ", " + cantidad + ", " + firma + ", " + new Date(timestamp) + "}";
    }
}
package com.aprendeblockchain.miblockchainenjava.commons.estructuras;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.google.common.primitives.Longs;

/*
 * Un bloque está formado por una cabecera de bloque y un contenido.
 * La cabecera incluye:
 * 	- Hash del bloque calculado a partir del contenido de la cabecera
 * 	- Hash del bloque anterior (permite mantener la cadena enlazada)
 * 	- Altura del bloque
 * 	- Timestamp
 * 	- Dificultad prueba de trabajo (en nuestro caso es constante asi que no lo incluimos por ahora).
 * 	- Nonce
 * 	- Raiz arbol de merkle
 * El contenido del bloque está formado por la lista de transacciones incluidas en dicho bloque.
 * */
public class Bloque {

	// Hash del bloque e identificador único de este. Usado para enlazar bloques.
	private byte[] hash;

	// Hash del bloque anterior.
	private byte[] hashBloqueAnterior;

	// Nonce calculado como solución a la prueba de trabajo
	private long nonce;

	// Timestamp de creación del bloque
	private long timestamp;

	// Root del arbol de merkle calculado a partir de las transacciones en el bloque.
	private byte[] raizArbolMerkle;

	// Lista de transacciones incluidas en este bloque
	private List<Transaccion> transacciones;

	public Bloque() {
	}

	public Bloque(byte[] hashBloqueAnterior, List<Transaccion> transacciones, long nonce) {
		this.hashBloqueAnterior = hashBloqueAnterior;
		this.transacciones = transacciones;
		this.nonce = nonce;
		this.timestamp = System.currentTimeMillis();
		this.raizArbolMerkle = calcularRaizArbolMerkle();
		this.hash = calcularHash();
	}

	public byte[] getHash() {
		return hash;
	}

	public void setHash(byte[] hash) {
		this.hash = hash;
	}

	public byte[] getHashBloqueAnterior() {
		return hashBloqueAnterior;
	}

	public void setHashBloqueAnterior(byte[] hashBloqueAnterior) {
		this.hashBloqueAnterior = hashBloqueAnterior;
	}

	public List<Transaccion> getTransacciones() {
		return transacciones;
	}

	public void setTransactions(List<Transaccion> transacciones) {
		this.transacciones = transacciones;
	}

	public byte[] getRaizArbolMerkle() {
		return raizArbolMerkle;
	}

	public void setRaizArbolMerkle(byte[] raizArbolMerkle) {
		this.raizArbolMerkle = raizArbolMerkle;
	}

	public long getNonce() {
		return nonce;
	}

	public void setNonce(long nonce) {
		this.nonce = nonce;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Calcular el hash del bloque a partir de la información de la cabecera del bloque (sin transacciones)
	 * 
	 * @return Hash SHA256
	 */
	public byte[] calcularHash() {
		byte[] hashableData = ArrayUtils.addAll(hashBloqueAnterior, raizArbolMerkle);
		hashableData = ArrayUtils.addAll(hashableData, Longs.toByteArray(nonce));
		hashableData = ArrayUtils.addAll(hashableData, Longs.toByteArray(timestamp));
		return DigestUtils.sha256(hashableData);
	}

	/**
	 * Calcular la raiz del arbol de merkle formado con las transacciones
	 * https://en.wikipedia.org/wiki/Merkle_tree
	 * 
	 * @return Hash SHA256
	 */
	public byte[] calcularRaizArbolMerkle() {
		Queue<byte[]> colaHashes = new LinkedList<>(
				transacciones.stream().map(Transaccion::getHash).collect(Collectors.toList()));
		while (colaHashes.size() > 1) {
			// calcular hash a partir de dos hashes previos
			byte[] info = ArrayUtils.addAll(colaHashes.poll(), colaHashes.poll());
			// añadir a la cola
			colaHashes.add(DigestUtils.sha256(info));
		}
		return colaHashes.poll();
	}

	/**
	 * Numero de ceros al principio del hash del bloque (para la prueba de trabajo)
	 * 
	 * @return int number of leading zeros
	 */
	public int getNumeroDeCerosHash() {
		for (int i = 0; i < getHash().length; i++) {
			if (getHash()[i] != 0) {
				return i;
			}
		}
		return getHash().length;
	}

	public boolean esValido() {
		// la raiz del arbol de Merkle coincide
		if (!Arrays.equals(getRaizArbolMerkle(), calcularRaizArbolMerkle())) {
			System.out.println("Raiz Merkle inválida");
			return false;
		}

		// el hash de bloque coincide
		if (!Arrays.equals(getHash(), calcularHash())) {
			System.out.println("Hash bloque inválido");
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Bloque bloque = (Bloque) o;

		return Arrays.equals(hash, bloque.hash);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(hash);
	}

    @Override
    public String toString() {
        return "{Hash:" + Base64.encodeBase64String(hash) + ", Previo:" + Base64.encodeBase64String(hashBloqueAnterior) + ", RaizMerkle:" + Base64.encodeBase64String(raizArbolMerkle) + ", Nonce:" + nonce + ", Timestamp:" + new Date(timestamp) + ", Transacciones:" + transacciones.toString() + "}";
    }
}
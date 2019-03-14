package com.aprendeblockchain.miblockchainenjava.commons.utilidades;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public abstract class UtilidadesFirma {

	/**
	 * Factoria de claves que se inicializa con el algoritmo para generar los pares
	 * de claves publica-privada
	 */
	private static KeyFactory keyFactory = null;

	static {
		try {
			keyFactory = KeyFactory.getInstance("DSA", "SUN");
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
		}
	}

	/**
	 * Generar un par de claves publica-privada
	 * 
	 * @return KeyPair par de claves
	 */
	public static KeyPair generarParClaves() throws NoSuchProviderException, NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
		keyGen.initialize(1024, random);
		return keyGen.generateKeyPair();
	}

	/**
	 * Validar una firma para unos datos y clave publica dados
	 * 
	 * @param info         datos firmados y a ser verificados
	 * @param firma        a ser verificada
	 * @param clavePublica clave publica asociada a la clave privada con la que
	 *                     fueron firmados los datos
	 * @return true si la firma es valida para los datos y clave publica dados
	 */
	public static boolean validarFirma(byte[] info, byte[] firma, byte[] clavePublica) {

		// crear un objeto PublicKey con la clave publica dada
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(clavePublica);
		PublicKey publicKeyObj;
		try {
			publicKeyObj = keyFactory.generatePublic(keySpec);

			// validar firma
			Signature sig = getInstanciaSignature();
			sig.initVerify(publicKeyObj);
			sig.update(info);
			return sig.verify(firma);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Firmar unos datos con una clave privada dada
	 * 
	 * @param info         datos a ser firmados
	 * @param clavePrivada para firmar los datos
	 * @return firma de los datos y que puede ser verificada con los datos y la
	 *         clave pública
	 */
	public static byte[] firmar(byte[] info, byte[] clavePrivada) throws Exception {
		// crear objeto PrivateKey con la clave dada
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clavePrivada);
		PrivateKey privateKeyObj = keyFactory.generatePrivate(keySpec);

		// firmar
		Signature sig = getInstanciaSignature();
		sig.initSign(privateKeyObj);
		sig.update(info);
		return sig.sign();
	}

	private static Signature getInstanciaSignature() throws NoSuchProviderException, NoSuchAlgorithmException {
		return Signature.getInstance("SHA1withDSA", "SUN");
	}

}
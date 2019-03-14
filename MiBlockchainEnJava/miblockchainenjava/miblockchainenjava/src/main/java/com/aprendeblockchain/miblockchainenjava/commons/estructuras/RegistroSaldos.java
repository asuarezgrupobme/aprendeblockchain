package com.aprendeblockchain.miblockchainenjava.commons.estructuras;

import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.commons.codec.binary.Base64;

/*
 * La cadena de bloques es esencialmente una lista de bloques enlazados ya que cada bloque tiene el identificador del bloque anterior.
 * */
public class RegistroSaldos {

	// Registro de saldos <encoded public key, saldo>
	private Hashtable<String, Double> saldos = new Hashtable<String, Double>();

	public RegistroSaldos() {
	}

	public Hashtable<String, Double> getSaldos() {
		return saldos;
	}

	public void setSaldos(Hashtable<String, Double> saldos) {
		this.saldos = saldos;
	}

	public Double getSaldoCuenta(byte[] clavePublica) {
		
		return (this.saldos.containsKey(convertirClaveAString(clavePublica)) ? this.saldos.get(convertirClaveAString(clavePublica)) : 0.0);
	}

	public void setSaldoCuenta(byte[] clavePublica, Double saldo) {
		this.saldos.put(convertirClaveAString(clavePublica), saldo);
	}

	public void añadeSaldoACuenta(byte[] clavePublica, Double saldo) {
		this.saldos.put(convertirClaveAString(clavePublica), getSaldoCuenta(clavePublica) + saldo);
	}

	public void liquidarTransaccion(Transaccion transaccion) throws Exception {
		if(transaccion.getEsCoinbase()) {
			this.añadeSaldoACuenta(transaccion.getDestinatario(), transaccion.getCantidad());
		}
		else {
			if (getSaldoCuenta(transaccion.getEmisor()) >= transaccion.getCantidad()) {		
				this.añadeSaldoACuenta(transaccion.getEmisor(), - transaccion.getCantidad());
				this.añadeSaldoACuenta(transaccion.getDestinatario(), transaccion.getCantidad());
			} else {
				throw new Exception("No hay suficiente saldo en cuenta emisor.");
			}
		}
	}
	
	public boolean existeCuenta(byte[] cuenta) {
		return this.saldos.containsKey(convertirClaveAString(cuenta));
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		Enumeration<String> cuentas = saldos.keys();
		buf.append("CLAVE PUBLICA | SALDO\n");
		buf.append("---------------------\n");
		while(cuentas.hasMoreElements()) {
			String cuenta = cuentas.nextElement();
			buf.append(cuenta.substring(0, 10) + "...");
			buf.append(" | ");
			buf.append(saldos.get(cuenta));
			if(cuentas.hasMoreElements()) buf.append("\n");
		}
		return buf.toString();
	}
	
	private String convertirClaveAString(byte[] clave) {
		return Base64.encodeBase64String(clave);
	}
}
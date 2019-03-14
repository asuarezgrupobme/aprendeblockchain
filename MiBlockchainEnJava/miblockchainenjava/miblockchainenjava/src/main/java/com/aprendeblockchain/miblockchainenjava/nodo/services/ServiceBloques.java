package com.aprendeblockchain.miblockchainenjava.nodo.services;

import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.aprendeblockchain.miblockchainenjava.commons.estructuras.Bloque;
import com.aprendeblockchain.miblockchainenjava.commons.estructuras.CadenaDeBloques;
import com.aprendeblockchain.miblockchainenjava.commons.estructuras.RegistroSaldos;
import com.aprendeblockchain.miblockchainenjava.commons.estructuras.Transaccion;
import com.aprendeblockchain.miblockchainenjava.Configuracion;

@Service
public class ServiceBloques {

	private final ServiceTransacciones servicioTransacciones;
	
	private CadenaDeBloques cadenaDeBloques = new CadenaDeBloques();

	@Autowired
	public ServiceBloques(ServiceTransacciones servicioTransacciones) {
		this.servicioTransacciones = servicioTransacciones;
	}

	public CadenaDeBloques getCadenaDeBloques() {
		return cadenaDeBloques;
	}

	/**
	 * Añadir un bloque a la cadena
	 * 
	 * @param bloque Bloque a ser añadido
	 * @return true si el bloque pasa la validacion y es a�adida a la cadena
	 * @throws Exception 
	 */
	public synchronized void añadirBloque(Bloque bloque) throws Exception {
		if (validarBloque(bloque)) {
			this.cadenaDeBloques.añadirBloque(bloque);
			
			// eliminar transacciones del pool excepto primer tx que es la coinbase
			bloque.getTransacciones().subList(1, bloque.getTransacciones().size()).forEach(servicioTransacciones::eliminarTransaccion);
			
			System.out.println("Bloque añadido a cadena de bloques.\n");
		}
		else {
			throw new Exception("Bloque inválido");
		}
	}

	/**
	 * Descargar la cadena de bloques de otro nodo
	 * 
	 * @param urlNodo      Url del nodo al que enviar la peticion
	 * @param restTemplate RestTemplate a usar
	 */
	public void obtenerCadenaDeBloques(URL urlNodo, RestTemplate restTemplate) {
		CadenaDeBloques cadena = restTemplate.getForObject(urlNodo + "/bloque", CadenaDeBloques.class);
		System.out.println("Obtenida cadena de bloques de nodo " + urlNodo + ".\n");
		try {
			this.cadenaDeBloques = new CadenaDeBloques(cadena);
		} catch (Exception e) {
			System.out.println("Cadena de bloques inválida");
		}
	}

	/**
	 * Validar un bloque a ser añadido a la cadena
	 * 
	 * @param bloque Bloque a ser validado
	 */
	private boolean validarBloque(Bloque bloque) {
		// comprobar que el bloque tiene un formato v�lido
		if (!bloque.esValido()) {
			return false;
		}

		// el hash de bloque anterior hace referencia al ultimo bloque en mi cadena
		if (!cadenaDeBloques.estaVacia()) {
			byte[] hashUltimoBloque = cadenaDeBloques.getUltimoBloque().getHash();
			if (!Arrays.equals(bloque.getHashBloqueAnterior(), hashUltimoBloque)) {
				System.out.println("Hash bloque anterior no coincide.");
				return false;
			}
		} else {
			if (bloque.getHashBloqueAnterior() != null) {
				System.out.println("Hash bloque anterior inválido. Debería ser null.");
				return false;
			}
		}

		// max. numero de transacciones en un bloque
		if (bloque.getTransacciones().size() > Configuracion.getInstancia().getMaxNumeroTransaccionesEnBloque() + 1) {
			System.out.println("El número de transacciones supera el limite.");
			return false;
		}

		// verificar que todas las transacciones estaban en mi pool
		if (!servicioTransacciones.contieneTransacciones(bloque.getTransacciones().subList(1, bloque.getTransacciones().size()))) {
			System.out.println("Algunas transacciones no están en pool");
			return false;
		}

		// la dificultad coincide
		if (bloque.getNumeroDeCerosHash() < Configuracion.getInstancia().getDificultad()) {
			System.out.println("Bloque con dificultad inválida");
			return false;
		}

		return true;
	}
}
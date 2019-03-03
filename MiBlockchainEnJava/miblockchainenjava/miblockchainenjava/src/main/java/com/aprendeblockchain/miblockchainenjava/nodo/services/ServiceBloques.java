package com.aprendeblockchain.miblockchainenjava.nodo.services;

import java.net.URL;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.aprendeblockchain.miblockchainenjava.commons.estructuras.Bloque;
import com.aprendeblockchain.miblockchainenjava.commons.estructuras.CadenaDeBloques;
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
	 * A�adir un bloque a la cadena
	 * 
	 * @param bloque Bloque a ser a�adido
	 * @return true si el bloque pasa la validacion y es a�adida a la cadena
	 */
	public synchronized boolean añadirBloque(Bloque bloque) {
		if (validarBloque(bloque)) {
			this.cadenaDeBloques.añadirBloque(bloque);

			// eliminar las transacciones incluidas en el bloque del pool de transacciones
			bloque.getTransacciones().forEach(servicioTransacciones::eliminarTransaccion);
			return true;
		}
		return false;
	}

	/**
	 * Descargar la cadena de bloques de otro nodo
	 * 
	 * @param urlNodo      Url del nodo al que enviar la peticion
	 * @param restTemplate RestTemplate a usar
	 */
	public void obtenerCadenaDeBloques(URL urlNodo, RestTemplate restTemplate) {
		CadenaDeBloques cadena = restTemplate.getForObject(urlNodo + "/bloque", CadenaDeBloques.class);
		this.cadenaDeBloques = cadena;
		System.out.println("Obtenida cadena de bloques de nodo " + urlNodo);
	}

	/**
	 * Validar un bloque a ser a�adido a la cadena
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
				System.out.println("Bloque anterior invalido");
				return false;
			}
		} else {
			if (bloque.getHashBloqueAnterior() != null) {
				System.out.println("Bloque anterior invalido");
				return false;
			}
		}

		// max. numero de transacciones en un bloque
		if (bloque.getTransacciones().size() > Configuracion.getInstancia().getMaxNumeroTransaccionesEnBloque()) {
			System.out.println("Numero de transacciones supera el limite.");
			return false;
		}

		// verificar que todas las transacciones estaban en mi pool
		if (!servicioTransacciones.contieneTransacciones(bloque.getTransacciones())) {
			System.out.println("Algunas transacciones no en pool");
			return false;
		}

		// la dificultad coincide
		if(bloque.getNumeroDeCerosHash() < Configuracion.getInstancia().getDificultad()) {
			System.out.println("Bloque con dificultad inválida");
			return false;
		}
		
		return true;
	}
}
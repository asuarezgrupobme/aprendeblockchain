package com.aprendeblockchain.miblockchainenjava.nodo.services;

import java.net.URL;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.aprendeblockchain.miblockchainenjava.commons.estructuras.PoolTransacciones;
import com.aprendeblockchain.miblockchainenjava.commons.estructuras.RegistroSaldos;
import com.aprendeblockchain.miblockchainenjava.commons.estructuras.Transaccion;

@Service
public class ServiceTransacciones {

	// Pool de transacciones con transacciones pendientes de ser incluidas en un bloque
	private PoolTransacciones poolTransacciones = new PoolTransacciones();
	
	@Autowired
	public ServiceTransacciones() {
	}

	public PoolTransacciones getPoolTransacciones() {
		return poolTransacciones;
	}

	/**
	 * A�adir transaccion al pool
	 * 
	 * @param transaccion Transaccion a ser añadida
	 * @throws Exception 
	 */
	public synchronized void añadirTransaccion(Transaccion transaccion) throws Exception {   
		poolTransacciones.añadirTransaccion(transaccion);
	}

	/**
	 * Eliminar una transacci�n del pool
	 * 
	 * @param transaccion Transaccion a ser eliminada
	 */
	public void eliminarTransaccion(Transaccion transaccion) {
		poolTransacciones.eliminarTransaccion(transaccion);
	}

	/**
	 * Comprobar si el pool contiene una lista de transacciones
	 * 
	 * @param transacciones Transacciones a ser verificadas
	 * @return true si todas las transacciones est�n en el pool
	 */
	public boolean contieneTransacciones(Collection<Transaccion> transacciones) {
		return poolTransacciones.contieneTransacciones(transacciones);
	}
	
	/**
	 * Descargar pool de transacciones desde otro nodo
	 * 
	 * @param urlNodo Nodo al que pedir las transacciones
	 * @param restTemplate RestTemplate a usar
	 */
	public void obtenerPoolTransacciones(URL urlNodo, RestTemplate restTemplate) {
		PoolTransacciones poolTransacciones = restTemplate.getForObject(urlNodo + "/transaccion", PoolTransacciones.class);
		this.poolTransacciones = poolTransacciones;
		System.out.println("Obtenido pool de transacciones de nodo " + urlNodo + ".\n");
	}
}
package com.aprendeblockchain.miblockchainenjava.commons.estructuras;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * El pool de transacciones mantiene una colección de transacciones que están pendientes de ser incluidas en un bloque y añadidas a la cadena
 */
public class PoolTransacciones {

	private Set<Transaccion> pool = new HashSet<>();
	
	public PoolTransacciones() {
		
	}
	
	public PoolTransacciones(List<Transaccion> transacciones) {
		this.pool.addAll(transacciones);
	}
	
	/**
     * Añadir una transaccion al pool
     * @param transaccion Transaccion a ser añadida
     * @return true si la transaccion es válida y es añadida al pool
     */
    public synchronized boolean añadirTransaccion (Transaccion transaccion) {
        if (transaccion.esValida()) {
        	pool.add(transaccion);
            return true;
        }
        return false;
    }    

    /**
     * Eliminar una transaccion del pool
     * @param transaccion Transaccion a eliminar
     */
    public void eliminarTransaccion(Transaccion transaccion) {
    	pool.remove(transaccion);
    }

    /**
     * Comprobar si el pool contiene todas las transacciones de una lista de transacciones
     * @param transacciones Lista de transacciones a comprobar
     * @return true si todas las transacciones de la coleccion están en el pool
     */
    public boolean contieneTransacciones(Collection<Transaccion> transacciones) {
        return pool.containsAll(transacciones);
    }
}

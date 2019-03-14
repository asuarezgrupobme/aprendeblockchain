package com.aprendeblockchain.miblockchainenjava.nodo.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aprendeblockchain.miblockchainenjava.Configuracion;
import com.aprendeblockchain.miblockchainenjava.commons.estructuras.Bloque;
import com.aprendeblockchain.miblockchainenjava.commons.estructuras.RegistroSaldos;
import com.aprendeblockchain.miblockchainenjava.commons.estructuras.Transaccion;

@Service
public class ServiceMinado implements Runnable {

	private final ServiceTransacciones servicioTransacciones;
	private final ServiceNodo servicioNodo;
	private final ServiceBloques servicioBloques;

	private AtomicBoolean runMinado = new AtomicBoolean(false);

	@Autowired
	public ServiceMinado(ServiceTransacciones servicioTransacciones, ServiceNodo servicioNodo,
			ServiceBloques servicioBloques) {
		this.servicioTransacciones = servicioTransacciones;
		this.servicioNodo = servicioNodo;
		this.servicioBloques = servicioBloques;
	}

	/**
	 * Comenzar el servicio de minado
	 */
	public void startMinado() {
		if (runMinado.compareAndSet(false, true)) {
			System.out.println("Comenzando minado...");
			Thread thread = new Thread(this);
			thread.start();
		}
	}

	/**
	 * Parar el servicio de minado
	 */
	public void pararMinado() {
		System.out.println("Parando minado...");
		runMinado.set(false);
	}

	/**
	 * Parar el servicio de minado
	 */
	public void restartMinado() {
		System.out.println("Restarting minado...");
		this.pararMinado();
		this.startMinado();		
	}

	/**
	 * Busqueda de bloque valido y propagacion
	 */
	@Override
	public void run() {
		while (runMinado.get()) {
			Bloque bloque = minarBloque();
			if (bloque != null) {
				System.out.println("NUEVO BLOQUE MINADO:");
				System.out.println(bloque);
				System.out.println("\n");
				
				// añado el bloque a mi cadena y lo propago
				try {
					servicioBloques.añadirBloque(bloque);
					servicioNodo.emitirPeticionPostNodosVecinos("bloque", bloque);
				} catch (Exception e) {
					// bloque invalido
				}
			}
		}
	}

	/**
	 * Iterar nonce hasta que cumpla con la dificultad configurada
	 */
	private Bloque minarBloque() {
		long nonce = 0;

		Bloque ultimoBloque = servicioBloques.getCadenaDeBloques().getUltimoBloque();
		byte[] hashUltimoBloque =  ultimoBloque!= null
				? ultimoBloque.getHash()
				: null;

		// Saldos temporales para ver si una transaccion hace doble gasto
		RegistroSaldos saldosTemporales = new RegistroSaldos();
		RegistroSaldos saldosActuales = servicioBloques.getCadenaDeBloques().getSaldos();		
		Iterator<Transaccion> it = servicioTransacciones.getPoolTransacciones().getPool().iterator();
		while (it.hasNext()) {
			Transaccion transaccion = it.next();
			if(saldosActuales.existeCuenta(transaccion.getEmisor())) {
				saldosTemporales.setSaldoCuenta(transaccion.getEmisor(), saldosActuales.getSaldoCuenta(transaccion.getEmisor()));
			}
		}
		
		List<Transaccion> transaccionesBloque = new ArrayList<Transaccion>();

		// iteramos las transacciones y las añadimos al bloque si el emisor tiene saldo
		it = servicioTransacciones.getPoolTransacciones().getPool().iterator();
		while (transaccionesBloque.size() < Configuracion.getInstancia().getMaxNumeroTransaccionesEnBloque()
				&& it.hasNext()) {
			Transaccion transaccion = it.next();
			try {
				if(saldosTemporales.existeCuenta(transaccion.getEmisor()))
						saldosTemporales.liquidarTransaccion(transaccion);
				else
					throw new Exception ("La cuenta del emisor " + Base64.encodeBase64String(transaccion.getEmisor()) + " no existe");
			} catch (Exception e) {
				//no incluir transaccion si hace doble gasto
				System.out.println("Transacción " + transaccion.getHash() + " no incluida por saldo insuficiente");
			}
		}

		// añadir transaccion coinbase como recompensa por resolver la prueba de trabajo
		Transaccion txCoinbase = new Transaccion(Base64.decodeBase64(Configuracion.getInstancia().getCoinbase()));
		txCoinbase.setTimestamp(System.currentTimeMillis());
		txCoinbase.setHash(txCoinbase.calcularHashTransaccion());
		
		transaccionesBloque.add(0, txCoinbase);

		// iterar nonce hasta encontrar solucion
		while (runMinado.get()) {
			if(ultimoBloque != servicioBloques.getCadenaDeBloques().getUltimoBloque())
				return null;
			Bloque bloque = new Bloque(hashUltimoBloque, transaccionesBloque, nonce);
			if (bloque.getNumeroDeCerosHash() >= Configuracion.getInstancia().getDificultad()) {
				return bloque;
			}
			nonce++;
		}
		return null;
	}

}
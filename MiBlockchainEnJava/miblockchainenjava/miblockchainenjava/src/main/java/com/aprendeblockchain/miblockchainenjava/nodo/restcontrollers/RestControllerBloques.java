package com.aprendeblockchain.miblockchainenjava.nodo.restcontrollers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aprendeblockchain.miblockchainenjava.Configuracion;
import com.aprendeblockchain.miblockchainenjava.commons.estructuras.Bloque;
import com.aprendeblockchain.miblockchainenjava.commons.estructuras.CadenaDeBloques;
import com.aprendeblockchain.miblockchainenjava.nodo.services.ServiceBloques;
import com.aprendeblockchain.miblockchainenjava.nodo.services.ServiceMinado;
import com.aprendeblockchain.miblockchainenjava.nodo.services.ServiceNodo;

@RestController
@RequestMapping("bloque")
public class RestControllerBloques {

	private final ServiceBloques servicioBloques;
	private final ServiceNodo servicioNodo;
	private final ServiceMinado servicioMinado;

	private List<Bloque> bufferBloques = new ArrayList<Bloque>();
	
	@Autowired
	public RestControllerBloques(ServiceBloques servicioCadenaDeBloques, ServiceNodo servicioNodo,
			ServiceMinado servicioMinado) {
		this.servicioBloques = servicioCadenaDeBloques;
		this.servicioNodo = servicioNodo;
		this.servicioMinado = servicioMinado;

		if (Configuracion.getInstancia().getMinar()) {
			servicioMinado.startMinado();
		}
	}

	/**
	 * Obtener la cadena de bloques
	 * 
	 * @return JSON Lista de bloques
	 */
	@RequestMapping(method = RequestMethod.GET)
	CadenaDeBloques getCadenaDeBloques() {
		System.out.println("PETICION CADENA DE BLOQUES\n");
		return servicioBloques.getCadenaDeBloques();
	}

	/**
	 * A�adir un bloque a la cadena
	 * 
	 * @param bloque   El bloque a ser añadido
	 * @param propagar Si el bloque debe ser propagado al resto de nodos en la red
	 * @param response codigo 202 si el bloque es aceptado y añadido, código 406 en
	 *                 caso contrario
	 */
	@RequestMapping(method = RequestMethod.POST)
	void añadirBloque(@RequestBody Bloque bloque, @RequestParam(required = false) Boolean propagar,
			HttpServletResponse response) {
		System.out.println("NUEVO BLOQUE RECIBIDO:");
		System.out.println(bloque);
		System.out.println("\n");
		
		if(!servicioNodo.inicializado) {
			this.bufferBloques.add(bloque);
			System.out.println("Bloque añadido a buffer.\n");
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
		}
		try {
			for (Bloque b : this.bufferBloques) {
				this.servicioBloques.añadirBloque(b);
			}
			this.bufferBloques = new ArrayList<Bloque>();
			
			servicioBloques.añadirBloque(bloque);
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
			
			//servicioMinado.restartMinado();
			if (propagar != null && propagar) {
				servicioNodo.emitirPeticionPostNodosVecinos("bloque", bloque);
				System.out.println("Bloque propagado.\n");
			}
		} catch (Exception e) {
			System.out.println("Bloque invalido y no añadido. Error: " + e + "\n");
			response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
		}
		
	}

}
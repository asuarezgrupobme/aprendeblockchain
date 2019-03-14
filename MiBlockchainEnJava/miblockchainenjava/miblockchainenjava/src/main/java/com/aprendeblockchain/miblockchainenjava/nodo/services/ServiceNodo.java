package com.aprendeblockchain.miblockchainenjava.nodo.services;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PreDestroy;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.aprendeblockchain.miblockchainenjava.Configuracion;
import com.aprendeblockchain.miblockchainenjava.commons.estructuras.Bloque;
import com.aprendeblockchain.miblockchainenjava.commons.estructuras.Transaccion;
import com.aprendeblockchain.miblockchainenjava.commons.utilidades.UtilidadesFirma;

@Service
public class ServiceNodo implements ApplicationListener<WebServerInitializedEvent> {

	private final ServiceBloques servicioBloques;
	private final ServiceTransacciones servicioTransacciones;

	// URL de mi nodo (host + port)
	private URL miUrlNodo;

	// nodos en la red
	private Set<URL> nodosVecinos = new HashSet<>();

	private RestTemplate restTemplate = new RestTemplate();
	public boolean inicializado = false;

	@Autowired
	public ServiceNodo(ServiceBloques servicioCadenaDeBloques, ServiceTransacciones servicioTransacciones) {
		this.servicioBloques = servicioCadenaDeBloques;
		this.servicioTransacciones = servicioTransacciones;
	}

	/**
	 * Al iniciar el nodo tenemos que: - Obtener la lista de nodos en la red -
	 * Obtener la cadena de bloques - Obtener transactiones en el pool - Dar de alta
	 * mi nodo en el resto de nodos
	 * 
	 * @param webServerInitializedEvent WebServer para obtener el puerto
	 */
	@Override
	public void onApplicationEvent(WebServerInitializedEvent webServerInitializedEvent) {
		// obtener la url del nodo master
		URL urlNodoMaster = getNodoMaster();

		// calcular mi url (host y puerto)
		String host = getIpPublica(urlNodoMaster, restTemplate);
		int port = webServerInitializedEvent.getWebServer().getPort();

		miUrlNodo = getMiUrlNodo(host, port);

		// descargar cadena de bloques y transacciones en pool si no soy nodo master
		if (miUrlNodo.equals(urlNodoMaster)) {
			System.out.println("Ejecutando nodo master");
			//crear bloque genesis			
			/*
			 * try { Bloque genesis = this.getBloqueGenesis();
			 * //servicioBloques.añadirBloque(genesis); } catch (Exception e) {
			 * System.out.println("No se pudo añadir el bloque génesis"); }
			 */
			
		} else {
			nodosVecinos.add(urlNodoMaster);

			// obtener lista de nodos, bloques y transacciones
			obtenerNodosVecinos(urlNodoMaster, restTemplate);
			servicioBloques.obtenerCadenaDeBloques(urlNodoMaster, restTemplate);
			servicioTransacciones.obtenerPoolTransacciones(urlNodoMaster, restTemplate);

			// dar de alta mi nodo en el resto de nodos en la red
			emitirPeticionPostNodosVecinos("nodo", miUrlNodo);
		}
		
		inicializado = true;
	}

	/**
	 * Dar de baja el nodo del resto de nodos antes de pararlo completamente
	 */
	@PreDestroy
	public void shutdown() {
		System.out.println("Parando nodo...");
		// enviar peticion para que el resto de nodos den de baja mi nodo
		emitirPetitionDeleteNodosVecinos("nodo", miUrlNodo);
	}

	/**
	 * Obtener nodos vecinos en la red
	 */
	public Set<URL> getNodosVecinos() {
		return nodosVecinos;
	}

	/**
	 * Dar de alta un nodo
	 */
	public synchronized void altaNodo(URL urlNodo) {
		nodosVecinos.add(urlNodo);
	}

	/**
	 * Dar de baja un nodo
	 */
	public synchronized void bajaNodo(URL urlNodo) {
		nodosVecinos.remove(urlNodo);
	}

	/**
	 * Enviar petición de tipo PUT al resto de nodos en la red (nodos vecinos)
	 * 
	 * @param endpoint el endpoint para esta petición
	 * @param datos    los datos que se quieren enviar con la peticion
	 */
	public void emitirPeticionPutNodosVecinos(String endpoint, Object datos) {
		nodosVecinos.parallelStream().forEach(urlNodo -> restTemplate.put(urlNodo + "/" + endpoint, datos));
	}

	/**
	 * Enviar petición de tipo POST al resto de nodos en la red (nodos vecinos)
	 * 
	 * @param endpoint el endpoint para esta petición
	 * @param datos    los datos que se quieren enviar con la peticion
	 */
	public void emitirPeticionPostNodosVecinos(String endpoint, Object data) {
		nodosVecinos.parallelStream().forEach(urlNodo -> restTemplate.postForLocation(urlNodo + "/" + endpoint, data));
	}

	/**
	 * Enviar petición de tipo DELETE al resto de nodos en la red (nodos vecinos)
	 * 
	 * @param endpoint el endpoint para esta petición
	 * @param datos    los datos que se quieren enviar con la peticion
	 */
	public void emitirPetitionDeleteNodosVecinos(String endpoint, Object data) {
		nodosVecinos.parallelStream().forEach(urlNodo -> restTemplate.delete(urlNodo + "/" + endpoint, data));
	}

	/**
	 * Obtener la lista de nodos en la red
	 * 
	 * @param urlNodoVecino Nodo vecino al que hacer la peticion
	 * @param restTemplate  RestTemplate a usar
	 */
	public void obtenerNodosVecinos(URL urlNodoVecino, RestTemplate restTemplate) {
		URL[] nodos = restTemplate.getForObject(urlNodoVecino + "/nodo", URL[].class);
		Collections.addAll(nodosVecinos, nodos);
	}

	/**
	 * Obtener la IP publica con la que me conecto a la red
	 * 
	 * @param urlNodoVecino Nodo vecino al que hacer la peticion
	 * @param restTemplate  RestTemplate a usar
	 */
	private String getIpPublica(URL urlNodoVecino, RestTemplate restTemplate) {
		return restTemplate.getForObject(urlNodoVecino + "/nodo/ip", String.class);
	}

	/**
	 * Construir mi url a partir de mi host y puerto
	 * 
	 * @param host Mi host publico
	 * @param port Puerto en el que se lanza el servicio
	 */
	private URL getMiUrlNodo(String host, int port) {
		try {
			return new URL("http", host, port, "");
		} catch (MalformedURLException e) {
			System.out.println("Invalida URL Nodo:" + e);
			return null;
		}
	}

	/**
	 * Obtener URL del nodo master del archivo de configuracion
	 */
	private URL getNodoMaster() {
		try {
			return new URL(Configuracion.getInstancia().getUrlNodoMaster());
		} catch (MalformedURLException e) {
			System.out.println("Invalida URL Nodo Master:" + e);
			return null;
		}
	}
	
	/**
	 * Bloque genesis
	 * @throws Exception 
	 */
	private Bloque getBloqueGenesis() throws Exception {

		//PUBLIC KEY: MIIBtzCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYQAAoGAY/Vx+BoONG2RSNIuM8ZrrVJ/Y0fbePVbyBAaLVIzIfilYk1WCGvz/ijOzk9Gp6DesHbXnf9MZptm36a3amu3JEBFyaBP4/j2gTs+N47UbvB/45hnjTdN0yI2H6iubxn8wgq2MartRYFRhF2D6Dcaw0DqNkRqyHDB2c+gVTTehSI=
		//PRIVATE KEY: MIIBSwIBADCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoEFgIUH7WhGYQ8hnRF4vTspE3CpjZRKqs=

				
		Transaccion txCoinbase = new Transaccion(Base64.decodeBase64(Configuracion.getInstancia().getCoinbase()));
		txCoinbase.setTimestamp(System.currentTimeMillis());
		txCoinbase.setHash(txCoinbase.calcularHashTransaccion());
		
		
		List<Transaccion> transacciones = new ArrayList<Transaccion>(Arrays.asList(txCoinbase));
		Bloque bloqueGenesis = new Bloque(null, transacciones, 0);
		
		return bloqueGenesis;
	}
	
	/**
	 * Devuelve la URL del nodo
	 * @return URL del nodo
	 */
	public URL getMyURL() {
		return this.miUrlNodo;
	}
}

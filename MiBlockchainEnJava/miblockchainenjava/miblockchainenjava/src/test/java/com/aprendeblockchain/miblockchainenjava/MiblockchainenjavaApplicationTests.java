package com.aprendeblockchain.miblockchainenjava;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URL;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.aprendeblockchain.miblockchainenjava.commons.estructuras.Bloque;
import com.aprendeblockchain.miblockchainenjava.commons.estructuras.Transaccion;
import com.aprendeblockchain.miblockchainenjava.commons.utilidades.UtilidadesFirma;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MiblockchainenjavaApplicationTests {

	@Autowired
	private MockMvc mvc;

	public static String asJsonString(final Object obj) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final String jsonContent = mapper.writeValueAsString(obj);
			return jsonContent;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void añadirTransaccion() throws Exception {
		KeyPair claveEmisor = UtilidadesFirma.generarParClaves();
		KeyPair claveDestinatario = UtilidadesFirma.generarParClaves();

		Transaccion tx = new Transaccion();
		tx.setEmisor(claveEmisor.getPublic().getEncoded());
		tx.setDestinatario(claveDestinatario.getPublic().getEncoded());
		tx.setCantidad(100);
		tx.setTimestamp(System.currentTimeMillis());
		tx.setFirma(UtilidadesFirma.firmar(tx.getContenidoTransaccion(), claveEmisor.getPrivate().getEncoded()));
		tx.setHash(tx.calcularHashTransaccion());

		mvc.perform(MockMvcRequestBuilders.post("/transaccion").content(asJsonString(tx))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isAccepted());
	}

	@Test
	public void getPoolTransacciones() throws Exception {
		KeyPair claveEmisor = UtilidadesFirma.generarParClaves();
		KeyPair claveDestinatario = UtilidadesFirma.generarParClaves();

		Transaccion tx = new Transaccion();
		tx.setEmisor(claveEmisor.getPublic().getEncoded());
		tx.setDestinatario(claveDestinatario.getPublic().getEncoded());
		tx.setCantidad(100);
		tx.setTimestamp(System.currentTimeMillis());
		tx.setFirma(UtilidadesFirma.firmar(tx.getContenidoTransaccion(), claveEmisor.getPrivate().getEncoded()));
		tx.setHash(tx.calcularHashTransaccion());

		mvc.perform(MockMvcRequestBuilders.post("/transaccion").content(asJsonString(tx))
				.contentType(MediaType.APPLICATION_JSON));

		mvc.perform(MockMvcRequestBuilders.get("/transaccion").contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	public void añadirBloque() throws Exception {

		KeyPair claveEmisor = UtilidadesFirma.generarParClaves();
		KeyPair claveDestinatario = UtilidadesFirma.generarParClaves();

		Transaccion tx1 = new Transaccion();
		tx1.setEmisor(claveEmisor.getPublic().getEncoded());
		tx1.setDestinatario(claveDestinatario.getPublic().getEncoded());
		tx1.setCantidad(100);
		tx1.setTimestamp(System.currentTimeMillis());
		tx1.setFirma(UtilidadesFirma.firmar(tx1.getContenidoTransaccion(), claveEmisor.getPrivate().getEncoded()));
		tx1.setHash(tx1.calcularHashTransaccion());

		System.out.println("Transaccion 1: " + asJsonString(tx1));
		mvc.perform(MockMvcRequestBuilders.post("/transaccion").content(asJsonString(tx1))
				.contentType(MediaType.APPLICATION_JSON));

		List<Transaccion> transacciones = new ArrayList<Transaccion>(Arrays.asList(tx1));
		long nonce = 5;
		Bloque bloque1 = new Bloque(null, transacciones, nonce);

		System.out.println("Bloque 1: " + asJsonString(bloque1));
		mvc.perform(MockMvcRequestBuilders.post("/bloque").content(asJsonString(bloque1))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isAccepted());
		
		Transaccion tx2 = new Transaccion();
		tx2.setEmisor(claveEmisor.getPublic().getEncoded());
		tx2.setDestinatario(claveDestinatario.getPublic().getEncoded());
		tx2.setCantidad(50);
		tx2.setTimestamp(System.currentTimeMillis());
		tx2.setFirma(UtilidadesFirma.firmar(tx2.getContenidoTransaccion(), claveEmisor.getPrivate().getEncoded()));
		tx2.setHash(tx2.calcularHashTransaccion());

		System.out.println("Transaccion 2: " + asJsonString(tx2));
		mvc.perform(MockMvcRequestBuilders.post("/transaccion").content(asJsonString(tx2))
				.contentType(MediaType.APPLICATION_JSON));

		List<Transaccion> transacciones2 = new ArrayList<Transaccion>(Arrays.asList(tx2));
		long nonce2 = 5;
		Bloque bloque2 = new Bloque(bloque1.getHash(), transacciones2, nonce2);

		System.out.println("Bloque 2: " + asJsonString(bloque2));
		mvc.perform(MockMvcRequestBuilders.post("/bloque").content(asJsonString(bloque2)).param("propagar", String.valueOf(true))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isAccepted());
	}

	@Test
	public void getIpPublica() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/ip").contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	public void altaNodo() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/nodo").content(asJsonString(new URL("http", "localhost", 8090, "")))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	public void bajaNodo() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/nodo").content(asJsonString(new URL("http", "localhost", 8090, "")))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

		mvc.perform(MockMvcRequestBuilders.delete("/nodo").content(asJsonString(new URL("http", "localhost", 8090, "")))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	public void getNodosVecinos() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/nodo")
				.contentType(MediaType.APPLICATION_JSON));
	}
}

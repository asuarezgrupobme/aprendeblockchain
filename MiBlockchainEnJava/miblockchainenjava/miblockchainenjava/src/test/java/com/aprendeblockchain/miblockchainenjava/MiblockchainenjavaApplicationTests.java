package com.aprendeblockchain.miblockchainenjava;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

		Transaccion tx = new Transaccion();
		tx.setEmisor(claveEmisor.getPublic().getEncoded());
		tx.setDestinatario(claveDestinatario.getPublic().getEncoded());
		tx.setCantidad(100);
		tx.setTimestamp(System.currentTimeMillis());
		tx.setFirma(UtilidadesFirma.firmar(tx.getContenidoTransaccion(), claveEmisor.getPrivate().getEncoded()));
		tx.setHash(tx.calcularHashTransaccion());

		System.out.println("Transaccion: " + asJsonString(tx));
		mvc.perform(MockMvcRequestBuilders.post("/transaccion").content(asJsonString(tx))
				.contentType(MediaType.APPLICATION_JSON));

		List<Transaccion> transacciones = new ArrayList<Transaccion>(Arrays.asList(tx));
		long nonce = 5;
		Bloque bloque = new Bloque(null, transacciones, nonce);

		System.out.println("Bloque: " + asJsonString(bloque));
		mvc.perform(MockMvcRequestBuilders.post("/bloque").content(asJsonString(bloque))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isAccepted());
	}

	@Test
	public void getIpPublica() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/ip").contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	public void altaNodo() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/nodo").content("http://localhost:8081")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	public void bajaNodo() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/nodo").content("http://localhost:8081")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

		mvc.perform(MockMvcRequestBuilders.delete("/nodo").content("http://localhost:8081")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	public void getNodosVecinos() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/nodo")
				.contentType(MediaType.APPLICATION_JSON));
	}
}

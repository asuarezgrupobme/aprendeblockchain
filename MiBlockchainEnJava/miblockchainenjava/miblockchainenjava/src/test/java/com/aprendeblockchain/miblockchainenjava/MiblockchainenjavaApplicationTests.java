package com.aprendeblockchain.miblockchainenjava;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URL;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tomcat.util.codec.binary.Base64;
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
		//KeyPair claveEmisor = UtilidadesFirma.generarParClaves();
		KeyPair claveDestinatario = UtilidadesFirma.generarParClaves();
		
		String publicKeyEmisor = "MIIBtzCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYQAAoGAY/Vx+BoONG2RSNIuM8ZrrVJ/Y0fbePVbyBAaLVIzIfilYk1WCGvz/ijOzk9Gp6DesHbXnf9MZptm36a3amu3JEBFyaBP4/j2gTs+N47UbvB/45hnjTdN0yI2H6iubxn8wgq2MartRYFRhF2D6Dcaw0DqNkRqyHDB2c+gVTTehSI=";
		String privateKeyEmisor = "MIIBSwIBADCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoEFgIUH7WhGYQ8hnRF4vTspE3CpjZRKqs=";

		
		Transaccion tx = new Transaccion();
		tx.setEmisor(Base64.decodeBase64(publicKeyEmisor));
		tx.setDestinatario(claveDestinatario.getPublic().getEncoded());
		tx.setCantidad(5);
		tx.setTimestamp(System.currentTimeMillis());
		tx.setFirma(UtilidadesFirma.firmar(tx.getContenidoTransaccion(), Base64.decodeBase64(privateKeyEmisor)));
		tx.setHash(tx.calcularHashTransaccion());

		mvc.perform(MockMvcRequestBuilders.post("/transaccion").content(asJsonString(tx))
				.contentType(MediaType.APPLICATION_JSON));

		mvc.perform(MockMvcRequestBuilders.get("/transaccion").contentType(MediaType.APPLICATION_JSON));
	}

	/*
	 * @Test public void añadirBloque() throws Exception {
	 * 
	 * KeyPair claveEmisor = UtilidadesFirma.generarParClaves(); KeyPair
	 * claveDestinatario = UtilidadesFirma.generarParClaves();
	 * 
	 * Transaccion tx1 = new Transaccion();
	 * tx1.setEmisor(claveEmisor.getPublic().getEncoded());
	 * tx1.setDestinatario(claveDestinatario.getPublic().getEncoded());
	 * tx1.setCantidad(100); tx1.setTimestamp(System.currentTimeMillis());
	 * tx1.setFirma(UtilidadesFirma.firmar(tx1.getContenidoTransaccion(),
	 * claveEmisor.getPrivate().getEncoded()));
	 * tx1.setHash(tx1.calcularHashTransaccion());
	 * 
	 * System.out.println("Transaccion 1: " + asJsonString(tx1));
	 * mvc.perform(MockMvcRequestBuilders.post("/transaccion").content(asJsonString(
	 * tx1)) .contentType(MediaType.APPLICATION_JSON));
	 * 
	 * List<Transaccion> transacciones = new
	 * ArrayList<Transaccion>(Arrays.asList(tx1)); long nonce = 5; Bloque bloque1 =
	 * new Bloque(null, transacciones, nonce);
	 * 
	 * System.out.println("Bloque 1: " + asJsonString(bloque1));
	 * mvc.perform(MockMvcRequestBuilders.post("/bloque").content(asJsonString(
	 * bloque1))
	 * .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isAccepted());
	 * 
	 * Transaccion tx2 = new Transaccion();
	 * tx2.setEmisor(claveEmisor.getPublic().getEncoded());
	 * tx2.setDestinatario(claveDestinatario.getPublic().getEncoded());
	 * tx2.setCantidad(50); tx2.setTimestamp(System.currentTimeMillis());
	 * tx2.setFirma(UtilidadesFirma.firmar(tx2.getContenidoTransaccion(),
	 * claveEmisor.getPrivate().getEncoded()));
	 * tx2.setHash(tx2.calcularHashTransaccion());
	 * 
	 * System.out.println("Transaccion 2: " + asJsonString(tx2));
	 * mvc.perform(MockMvcRequestBuilders.post("/transaccion").content(asJsonString(
	 * tx2)) .contentType(MediaType.APPLICATION_JSON));
	 * 
	 * List<Transaccion> transacciones2 = new
	 * ArrayList<Transaccion>(Arrays.asList(tx2)); long nonce2 = 5; Bloque bloque2 =
	 * new Bloque(bloque1.getHash(), transacciones2, nonce2);
	 * 
	 * System.out.println("Bloque 2: " + asJsonString(bloque2));
	 * mvc.perform(MockMvcRequestBuilders.post("/bloque").content(asJsonString(
	 * bloque2)).param("propagar", String.valueOf(true))
	 * .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isAccepted()); }
	 */

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

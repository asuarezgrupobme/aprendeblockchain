package com.aprendeblockchain.miblockchainenjava.commons.estructuras;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {

	public static void main(String[] args) {
		Transaccion tx1 = new Transaccion("A".getBytes(), "B".getBytes(), 100, "AB".getBytes());
		Transaccion tx2 = new Transaccion("A".getBytes(), "C".getBytes(), 50, "AC".getBytes());
		Bloque b = new Bloque("0x0".getBytes(), new ArrayList<Transaccion>(Arrays.asList(tx1, tx2)), 43);
		CadenaDeBloques c = new CadenaDeBloques(new ArrayList<Bloque>(Arrays.asList(b)));
		System.out.println(c);
	}

}

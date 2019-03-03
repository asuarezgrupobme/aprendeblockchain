package com.aprendeblockchain.miblockchainenjava;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

public final class Configuracion {

	private XMLConfiguration xmlConfiguracion = null;
	private static Configuracion configuracion = null;

	public static final Configuracion getInstancia() {
		if (configuracion == null) {
			configuracion = new Configuracion();
			configuracion.xmlConfiguracion = new XMLConfiguration();
			configuracion.xmlConfiguracion.setFileName("configuracion.xml");
			try {
				configuracion.xmlConfiguracion.load();
			} catch (ConfigurationException e) {
				System.out.println("Error al leer el archivo de configuracion: " + e);
			}
		}

		return configuracion;
	}

	public String getUrlNodoMaster() {
		return configuracion.xmlConfiguracion.getString("urlNodoMaster");
	}

	public int getMaxNumeroTransaccionesEnBloque() {
		return configuracion.xmlConfiguracion.getInt("maxTransaccionesPorBloque");
	}

	public int getDificultad() {
		return configuracion.xmlConfiguracion.getInt("dificultad");
	}
}

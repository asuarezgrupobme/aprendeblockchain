package com.aprendeblockchain.miblockchainenjava.commons.estructuras;

import java.util.ArrayList;
import java.util.List;

/*
 * La cadena de bloques es esencialmente una lista de bloques enlazados ya que cada bloque tiene el identificador del bloque anterior.
 * */
public class CadenaDeBloques {

	// Lista de bloques en la cadena ordenados por altura
	private List<Bloque> bloques = new ArrayList<Bloque>();

	public CadenaDeBloques() {
	}

	public CadenaDeBloques(List<Bloque> bloques) {
		this.bloques = bloques;
	}

	public List<Bloque> getBloques() {
		return bloques;
	}

	public void setBloques(List<Bloque> bloques) {
		this.bloques = bloques;
	}

	public boolean estaVacia() {
		return this.bloques == null || this.bloques.isEmpty();
	}

	public int getNumeroBloques() {
		return (estaVacia() ? 0 : this.bloques.size());
	}

	/**
     * Obtener el ultimo bloque en la cadena
     * @return Ultimo bloque de la cadena
     */
    public Bloque getUltimoBloque() {
        if (estaVacia()) {
            return null;
        }
        return this.bloques.get(this.bloques.size() - 1);
    }
    
    public void añadirBloque(Bloque bloque) {
    	this.bloques.add(bloque);
    }
    
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		CadenaDeBloques cadena = (CadenaDeBloques) o;

		if (bloques.size() != cadena.getBloques().size())
			return false;

		for (int i = 0; i < bloques.size(); i++) {
			if (bloques.get(i) != cadena.getBloques().get(i))
				return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return bloques.toString();
	}
}
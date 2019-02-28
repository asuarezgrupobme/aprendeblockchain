package com.aprendeblockchain.miblockchainenjava.nodo.restcontrollers;


import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aprendeblockchain.miblockchainenjava.commons.estructuras.Bloque;
import com.aprendeblockchain.miblockchainenjava.commons.estructuras.CadenaDeBloques;
import com.aprendeblockchain.miblockchainenjava.nodo.services.ServiceBloques;
import com.aprendeblockchain.miblockchainenjava.nodo.services.ServiceNodo;


@RestController
@RequestMapping("bloque")
public class RestControllerBloques {

    private final ServiceBloques servicioBloques;
    private final ServiceNodo servicioNodo;

    @Autowired
    public RestControllerBloques(ServiceBloques servicioCadenaDeBloques, ServiceNodo servicioNodo) {
        this.servicioBloques = servicioCadenaDeBloques;
        this.servicioNodo = servicioNodo;
    }

    /**
     * Obtener la cadena de bloques
     * @return JSON Lista de bloques
     */
    @RequestMapping
    CadenaDeBloques getCadenaDeBloques() {
        return servicioBloques.getCadenaDeBloques();
    }

    /**
     * A�adir un bloque a la cadena
     * @param bloque El bloque a ser a�adido
     * @param propagar Si el bloque debe ser propagado al resto de nodos en la red
     * @param response codigo 202 si el bloque es aceptado y a�adido, c�digo 406 en caso contrario
     */
    @RequestMapping(method = RequestMethod.POST)
    void añadirBloque(@RequestBody Bloque bloque, @RequestParam(required = false) Boolean propagar, HttpServletResponse response) {
        System.out.println("Añadir bloque " + Base64.encodeBase64String(bloque.getHash()));
        boolean exito = servicioBloques.añadirBloque(bloque);
        
        if (exito) {
            System.out.println("Bloque validado y añadido.");
            response.setStatus(HttpServletResponse.SC_ACCEPTED);

            if (propagar != null && propagar) {
                servicioNodo.emitirPeticionPostNodosVecinos("bloque", bloque);
            }
        } else {
            System.out.println("Bloque invalido y no añadido.");
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }
    }

}
package com.aprendeblockchain.miblockchainenjava.nodo.restcontrollers;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aprendeblockchain.miblockchainenjava.commons.estructuras.PoolTransacciones;
import com.aprendeblockchain.miblockchainenjava.commons.estructuras.Transaccion;
import com.aprendeblockchain.miblockchainenjava.nodo.services.ServiceNodo;
import com.aprendeblockchain.miblockchainenjava.nodo.services.ServiceTransacciones;


@RestController()
@RequestMapping("transaccion")
public class RestControllerTransacciones {

    private final ServiceTransacciones servicioTransacciones;
    private final ServiceNodo servicioNodo;

    @Autowired
    public RestControllerTransacciones(ServiceTransacciones servicioTransacciones, ServiceNodo servicioNodo) {
        this.servicioTransacciones = servicioTransacciones;
        this.servicioNodo = servicioNodo;
    }

    /**
     * Obtener el pool de transacciones pendientes de ser incluidas en un bloque
     * @return JSON pool de transacciones
     */
    @RequestMapping
    PoolTransacciones getPoolTransacciones() {
        return servicioTransacciones.getPoolTransacciones();
    }


    /**
     * Añadir una transaccion al pool
     * @param transaccion Transaccion a ser añaadida
     * @param propagar si la transacción debe ser propaga a otros nodos en la red
     * @param response código 202 si la transacci�n es a�adida al pool, 406 en otro caso
     */
    @RequestMapping(method = RequestMethod.POST)
    void añadirTransaccion(@RequestBody Transaccion transaccion, @RequestParam(required = false) Boolean propagar, HttpServletResponse response) {
        System.out.println("Añadir transaccion " + Base64.encodeBase64String(transaccion.getHash()));
        boolean exito = servicioTransacciones.añadirTransaccion(transaccion);

        if (exito) {
            System.out.println("Transaccion validada y añadida.");
            response.setStatus(HttpServletResponse.SC_ACCEPTED);

            if (propagar != null && propagar) {
                servicioNodo.emitirPeticionPostNodosVecinos("transaccion", transaccion);
            }
        } else {
            System.out.println("Transaccion invalida y no añadida.");
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }
    }

}
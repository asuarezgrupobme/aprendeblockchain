package com.aprendeblockchain.miblockchainenjava.nodo.restcontrollers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aprendeblockchain.miblockchainenjava.nodo.services.ServiceNodo;


@RestController
@RequestMapping("nodo")
public class RestControllerNodo {

    private final ServiceNodo servicioNodo;

    @Autowired
    public RestControllerNodo(ServiceNodo servicioNodo) {
        this.servicioNodo = servicioNodo;
    }

    /**
     * Obtener la lista de nodos vecinos en la red
     * @return JSON lista de URLs
     */
    @RequestMapping(method = RequestMethod.GET)
    Set<URL> getNodosVecinos() {
        System.out.println("Request: getNodosVecinos");
        return servicioNodo.getNodosVecinos();
    }

    /**
     * Dar de alta un nodo en la red
     * @param nodo a ser dado de alta
     */
    @RequestMapping(method = RequestMethod.POST)
    void altaNodo(@RequestBody URL urlNodo, HttpServletResponse response) {
        System.out.println("Request: altaNodo " + urlNodo);
        servicioNodo.altaNodo(urlNodo);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Dar de baja a un nodo en la red
     * @param nodo a ser dado de baja
     */
    @RequestMapping(method = RequestMethod.DELETE)
    void bajaNodo(@RequestBody URL urlNodo, HttpServletResponse response) {
    	System.out.println("Request: bajaNodo " + urlNodo);
		servicioNodo.bajaNodo(urlNodo);
	    response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Endpoint auxiliar para que un nodo pueda conocer su IP publica y con la que otros nodos se comunicarán con él
     * @param request HttpServletRequest
     * @return la IP publica
     */
    @RequestMapping(path = "ip", method = RequestMethod.GET)
    String getIpPublica(HttpServletRequest request) {
    	System.out.println("Request: getIpPublica");
        return request.getRemoteAddr();
    }

}
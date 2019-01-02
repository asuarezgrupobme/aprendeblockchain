'use strict';


/**
 * Comienza la busqueda del nonce para solucionar la prueba de trabajo
 *
 * returns MinadoResponse
 **/
exports.comenzarMinado = function() {
  return new Promise(function(resolve, reject) {
    var examples = {};
    examples['application/json'] = {
  "message" : "Bloque minado",
  "bloque" : {
    "transacciones" : [ {
      "receptor" : "b",
      "cantidad" : 10,
      "emisor" : "a"
    }, {
      "receptor" : "a",
      "cantidad" : 5,
      "emisor" : "b"
    } ],
    "hashBloquePrevio" : "alskfñalsjfaowf98fals",
    "nonce" : "20",
    "dificultad" : 4,
    "timestamp" : new Date(),
    "altura" : 1,
  }
};
    if (Object.keys(examples).length > 0) {
      resolve(examples[Object.keys(examples)[0]]);
    } else {
      resolve();
    }
  });
}


/**
 * Envia una nueva transacción al nodo
 *
 * transaccion Transaccion La transaccion
 * returns TransaccionResponse
 **/
exports.enviarNuevaTransaccion = function(transaccion) {
  return new Promise(function(resolve, reject) {
    var examples = {};
    examples['application/json'] = {
  "message" : "Transacción enviada."
};
    if (Object.keys(examples).length > 0) {
      resolve(examples[Object.keys(examples)[0]]);
    } else {
      resolve();
    }
  });
}


/**
 * Devuelve la cadena de bloques del nodo
 *
 * returns CadenaResponse
 **/
exports.obtenerCadena = function() {
  return new Promise(function(resolve, reject) {
    var examples = {};
    examples['application/json'] = {
  "cadena" : [ {
    "transacciones" : [ {
      "receptor" : "b",
      "cantidad" : 10,
      "emisor" : "a"
    }, {
      "receptor" : "a",
      "cantidad" : 5,
      "emisor" : "b"
    } ],
    "hashBloquePrevio" : "alskfñalsjfaowf98fals",
    "nonce" : "20",
    "dificultad" : 4,
    "timestamp" : new Date(),
    "altura" : 1
  } ],
  "message" : "Cadena de bloques"
};
    if (Object.keys(examples).length > 0) {
      resolve(examples[Object.keys(examples)[0]]);
    } else {
      resolve();
    }
  });
}


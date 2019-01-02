'use strict';

var utils = require('../utils/writer.js');
var Default = require('../service/DefaultService');

module.exports.comenzarMinado = function comenzarMinado (req, res, next) {
  Default.comenzarMinado()
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

module.exports.enviarNuevaTransaccion = function enviarNuevaTransaccion (req, res, next) {
  var transaccion = req.swagger.params['transaccion'].value;
  Default.enviarNuevaTransaccion(transaccion)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

module.exports.obtenerCadena = function obtenerCadena (req, res, next) {
  Default.obtenerCadena()
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

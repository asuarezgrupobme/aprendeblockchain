var instancia;

Votacion.deployed().then(function(contrato) {
  instancia = contrato;
  return contrato.votar("Satoshi", {from: "0x3fA0dAB0c92334569f3837a4a98a2e4a8A1a42c3"});
}).then(function(result) {
  console.log("La transaccion se ejecutó con éxito: " + result.tx);
  return instancia.votosTotales.call("Satoshi");
}).then(function(votos) {
  console.log("Satoshi tiene " + votos.toNumber() + " votos.");
});

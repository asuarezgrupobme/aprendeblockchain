var Votacion = artifacts.require("./Votacion.sol");

contract('Votacion', function(accounts) {

  //test unitario para dar un voto al candidato Satoshi. Al finalizar el test se debe comprobar que el numero de votos se
  //ha incrementado como se espera
  it("votamos a Satoshi con la cuenta 0", function() {
    var contrato;

    var votosAntes, votosDespues;

    return Votacion.deployed().then(function(instance) {
      contrato = instance;
      return contrato.votosTotales.call("Satoshi");
    }).then(function(votos) {
      votosAntes = votos.toNumber();
      return contrato.votar("Satoshi", {from: accounts[0]});
    }).then(function() {
      return contrato.votosTotales.call("Satoshi");
    }).then(function(votos) {
      votosDespues = votos.toNumber();

      assert.equal(votosAntes + 1, votosDespues, "El número de votos deberia haberse incrementado en 1");
    });
  });

  // test unitario que intenta dar un voto a un candidato no existente. Debe lanzar excepcion y entrar por el catch
  it("votamos a candidato no valido", function() {
    var contrato;

    var votosAntes, votosDespues;

    return Votacion.deployed().then(function(instance) {
      contrato = instance;
      return contrato.votar("Pepe", {from: accounts[0]});
    }).then(function() {
      console.debug("No deberia llegar aqui porque deberia lanzar excepción")
    });
  });
});

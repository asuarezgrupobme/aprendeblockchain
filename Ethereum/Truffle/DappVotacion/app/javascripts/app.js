// Import the page's CSS. Webpack will know what to do with it.
import "../stylesheets/app.css";

// Import libraries we need.
import { default as Web3} from 'web3';
import { default as contract } from 'truffle-contract'

import voting_artifacts from '../../build/contracts/Votacion.json'

var Voting = contract(voting_artifacts);

let candidatos = {"Satoshi": "candidato-1", "Vitalik": "candidato-2"}

// funcion para votar a un candidato que se pasa como parametro. Una vez minada la transaccion se actualiza el contador
window.votar = function(candidato) {
  let nombreCandidato = $("#candidato").val();
  try {
    $("#msg").html("Tu voto ha sido emitido. El número de votos se actualizará cuando la transacción sea minada. Espera.")
    $("#candidato").val("");

    Voting.deployed().then(function(contractInstance) {
      contractInstance.votar(nombreCandidato, {gas: 140000, from: web3.eth.accounts[0]}).then(function() {
        let div_id = candidatos[nombreCandidato];
        return contractInstance.votosTotales.call(nombreCandidato).then(function(v) {
          $("#" + div_id).html(v.toString());
          $("#msg").html("");
        });
      });
    });
  } catch (err) {
    console.log(err);
  }
}

// cuando se carga la página se inicializa la conexión con la blockchain y se actualizan los votos de cada candidato
$( document ).ready(function() {
  if (typeof web3 !== 'undefined') {
    console.warn("Usando web3 de fuente externa como Metamask")
    window.web3 = new Web3(web3.currentProvider);
  } else {
    console.warn("No web3 detectado. Redirifiendo a http://localhost:7545.");
    window.web3 = new Web3(new Web3.providers.HttpProvider("http://localhost:7545"));
  }

  Voting.setProvider(web3.currentProvider);
  let nombreCandidatos = Object.keys(candidatos);

  for (var i = 0; i < nombreCandidatos.length; i++) {
    let nombre = nombreCandidatos[i];
    Voting.deployed().then(function(contractInstance) {
      contractInstance.votosTotales.call(nombre).then(function(v) {

        $("#" + candidatos[nombre]).html(v.toString());
      });
    })
  }
});

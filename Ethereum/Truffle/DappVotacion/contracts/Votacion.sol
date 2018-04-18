pragma solidity ^0.4.0;

contract Votacion {

  mapping (bytes32 => uint8) public votosRecibidos; //mapping que asocia numero de votos a candidato
  mapping (bytes32 => bool) public candidatos; //mapping para saber si un candidato es válido

  function Votacion(bytes32[] nombreCandidatos) public {
    for(uint i = 0; i < nombreCandidatos.length; i++) {
      candidatos[nombreCandidatos[i]] = true;
      votosRecibidos[nombreCandidatos[i]] = 0;
    }
  }

  // Numero total de votos recibidos por un candidato
  function votosTotales(bytes32 candidato) constant public returns (uint8) {
    require(candidatoEsValido(candidato));
    return votosRecibidos[candidato];
  }

  // Incrementa en 1 el numero de votos de un candidato
  function votar(bytes32 candidato) public {
    require(candidatoEsValido(candidato) == true);
    votosRecibidos[candidato] += 1;
  }

  // Comprueba que un candidato existe
  function candidatoEsValido(bytes32 candidato) constant public returns (bool) {
    return (candidatos[candidato] == true);
  }
}

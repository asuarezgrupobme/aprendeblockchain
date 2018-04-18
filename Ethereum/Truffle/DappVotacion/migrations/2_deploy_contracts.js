var Votacion = artifacts.require("./Votacion.sol");

module.exports = function(deployer) {
  deployer.deploy(Votacion, ['Satoshi', 'Vitalik'], {gas: 6700000});
};

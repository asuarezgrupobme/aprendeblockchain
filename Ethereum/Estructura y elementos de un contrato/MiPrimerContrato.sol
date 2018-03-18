pragma solidity ^0.4.0;

//Ejemplo variable de estado y funciones
contract MiPrimerContrato_A {
    uint x;

    function set(uint _x) public {
        x = _x;
    }

    function get() public constant returns (uint) {
        return x;
    }
}

//Ejemplo modificador de funcion
contract MiPrimerContrato_B {
    uint x;
    address creador;

    function MiPrimerContrato() public{
        creador = msg.sender;
    }

    modifier soloCreador(){
        require(msg.sender == creador);
        _;
    }

    function set(uint _x) public soloCreador {
        x = _x;
    }

    function get() public constant returns (uint) {
        return x;
    }
}

//Ejemplo de evento
contract MiPrimerContrato_C {
    uint x;

    event VariableModificada(address direccion, uint nuevoValor); // Evento

    function set(uint _x) public {
        x = _x;
        VariableModificada(msg.sender, _x);
    }

    function get() public constant returns (uint) {
        return x;
    }
}

//Ejemplo de estructura de datos
contract MiPrimerContrato_D {

    struct Registro { // Struct
        uint x;
        uint cuenta;
    }

    Registro miRegistro;

    function set(uint _x) public {
       miRegistro.x = _x;
       miRegistro.cuenta = miRegistro.cuenta + 1;
    }

    function get() public constant returns (uint, uint) {
        return (miRegistro.x, miRegistro.cuenta);
    }
}

//Ejemplo de enumerado
contract MiPrimerContrato_E {
    enum EstadoVariable { Original, Modificada} // Enumerado

    uint x;
    EstadoVariable estado;

    function MiPrimerContrato() {
	estado = EstadoVariable.Original;
    }

    function set(uint _x) public {
        x = _x;
	estado = EstadoVariable.Modificada;
    }

    function get() public constant returns (uint) {
        return x;
    }
}

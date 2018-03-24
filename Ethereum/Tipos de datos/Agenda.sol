pragma solidity ^0.4.0;

contract Agenda {

   struct Contacto {
       string nombreYApellidos;
       bytes16 telefono;
       string email;
       uint edad;
       bool valido; //flag
   }

   Contacto[] arrayContactos;
   mapping(bytes16 => Contacto) mappingContactos;

   //función para añadir un contacto
   function anadirContacto(string _nombreYApellidos, bytes16 _telefono, string _email, uint _edad) public {
       require(mappingContactos[_telefono].valido == false); //comprobar que no hay otro contacto con el mismo telefono
       mappingContactos[_telefono] = Contacto(_nombreYApellidos, _telefono, _email, _edad, true);
       arrayContactos.push(mappingContactos[_telefono]);
   }

   //función para leer un contacto a partir del teléfono
   function obtenerContacto(bytes16 _telefono) public constant returns (string, bytes16, string, uint) {
       Contacto storage contacto = mappingContactos[_telefono];
       require(contacto.valido); //comprobar que el contacto existe
       return (contacto.nombreYApellidos, contacto.telefono, contacto.email, contacto.edad);
   }
}

// POSICIONES TABLA
var columnas = ["0","1","2","3","4","5","6","7","8","9","10"];
var filas = ["0","A","B","C","D","E","F","G","H","I","J"];
//newType NAVE Y newLargo
var patrolBoat = {"type" : "patrolBoat", "largo" : 2, "cant" : 1};
var submarine = {"type" : "submarine", "largo" : 3, "cant" : 1};
var destroyer = {"type" : "destroyer", "largo" : 3, "cant" : 1};
var battleship = {"type" : "battleship", "largo" : 4, "cant" : 1};
var carrier = {"type" : "carrier", "largo" : 5, "cant" : 1};
//POSICION NAVE
var posicionH = "X";
var posicionV = "Y";
// TABLERO
var tableroNaves = document.querySelector(".tableroNaves");
var tableroSalvo = document.querySelector(".tableroSalvo");
// GAMEPLAYER
var gp = new URLSearchParams(window.location.search).get("gp");
// STOMPCLIENT DEL WEBSOCKET
var stompClient = null;


function determinarNave(newLargoNave) {
  let newLargo = 0;

  // Validacion - Segun el newType del barco y su tamaño (datos traidos por fetch)
  if(newLargoNave == patrolBoat.type) {
    newLargo = patrolBoat.largo;
  } else if(newLargoNave == submarine.type) {
    newLargo = submarine.largo;
  } else if(newLargoNave == destroyer.type) {
    newLargo = destroyer.largo;
  } else if(newLargoNave == battleship.type) {
    newLargo = battleship.largo;
  } else if(newLargoNave == carrier.type) {
    newLargo = carrier.largo;
  }

  return newLargo
}

// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// FUNCIONES / EVENTOS PARA SHIPS
// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

function onDragStart(e) {
  e.dataTransfer.setData("text/plain", e.target.className);

  console.log(e);

  // FEATURE A TRABAJAR
  console.log(e.target);
  console.log(e.currentTarget);
  //e.currentTarget.style.position = "absolute";
  e.currentTarget.addEventListener("mousedown", function(event){
    event.currentTarget.style.position = "absolute";
    event.currentTarget.style.left = event.clientX + "px";
    event.currentTarget.style.top = event.clientY + "px";}, true)
  

  e.currentTarget.style.backgroundColor = "#DDD";
}

function onDragOver(e) {
  e.preventDefault();
}

function onDragEnter(e) {
  if (e.target.classList.contains("celdaDiv") ) {
    e.target.style.backgroundColor = "lightblue";
  }
}

function onDragLeave(e) {
  if ( e.target.classList.contains("celdaDiv") ) {
    e.target.style.backgroundColor = "";
  }
}

function onDrop(e){
  e.preventDefault();

  e.target.classList.remove("active");

  e.target.style.backgroundColor = "";

  let id = e.dataTransfer.getData("text");

  let draggableElement = document.getElementsByClassName(id);

  let dropzone = e.target;

  let posicionClases = e.target.className.split(" ");

  let posicion;

  if(posicionClases[0].length < 4) {
    posicion = posicionClases[0];
  } else {
    posicion = posicionClases[1];
  }

  validarDrop(posicion, id, draggableElement[0]);

  revisarEspacios();

  e.dataTransfer.clearData();
}

function validarDrop(posicion, newLargoNave, draggableElement) {
  // Falta si es horizontal o vertical
  let nave = newLargoNave.split(" ");
  let newLargo;
  let postura;
  let newTypeNave;
  
  if (nave[0].length > 1) {
    newTypeNave = nave[0];
    newLargo = determinarNave(nave[0]);
    postura = nave[1];
  } else {
    newTypeNave = nave[1];
    newLargo = determinarNave(nave[1]);
    postura = nave[0];
  }
  
  // Slice de la posicion
  let letra = posicion.slice(0,1);
  let num = posicion.slice(1);

  // Letra -> ASCII     num -> INT
  let letraASCII = letra.charCodeAt(0);
  let numParse = +num;


  // Validacion - Que no se pase de los límites del tablero
  // Porque todavia no tengo si es vertical if ( (letraASCII + newLargo) > 75 || (numParse + newLargo) > 11) {
  if ( (postura == "X" && (numParse + newLargo) > 11) || (postura == "Y" && (letraASCII + newLargo) > 75) )  {
    console.log("Algo fallo") ;
    return false;
  } 

  // Validacion - Que no se superpongan
  if (postura == "X") {
    for (let i = numParse; i <= numParse+newLargo; i++) {
      let newPos = letra + (i);
      let pos = tableroNaves.querySelector("." + newPos);
      //if (pos.getAttribute("ondrop") == null) {return false;}
      if (pos.classList.contains("posicionado")) {
        
        return false;
      }
    }
  } else if (postura == "Y") {
    for (let i = letraASCII; i <= letraASCII+newLargo; i++) {
      let newPos = String.fromCharCode(i) + numParse;
      let pos = tableroNaves.querySelector("." + newPos);
      
      //if (pos.getAttribute("ondrop") == null) {return false;}
      if (pos.classList.contains("posicionado")) {

        return false;
      }
    }
  }

  for (let i = 1; i < filas.length; i++) {

    for(let j = 1; j < columnas.length; j++) {
      let celda = tableroNaves.querySelector("."+filas[i]+columnas[j]);
      if (celda.hasChildNodes()) {
        if ( celda.childNodes[0].classList.contains(newTypeNave) ) {
          celda.removeChild(celda.childNodes[0]);
        }
      }
    }
  }

  if (postura == "X") {
    for (let i = numParse; i < numParse+newLargo; i++) {
      let newPos = letra + (i);

      let posicionar = tableroNaves.querySelector("." + newPos);


      posicionar.classList.add("posicionado");

      posicionar.appendChild(draggableElement.cloneNode(true));

      posicionar.firstElementChild.style.opacity = 0;
      posicionar.firstElementChild.style.width = "100%";
      posicionar.firstElementChild.style.height = "100%";
      posicionar.firstElementChild.style.margin = "auto";
    }
  } else if (postura == "Y") {
    for (let i = letraASCII; i < letraASCII+newLargo; i++) {
      let newPos = String.fromCharCode(i) + numParse;

      let posicionar = tableroNaves.querySelector("." + newPos);
      posicionar.classList.add("posicionado");

      posicionar.appendChild(draggableElement.cloneNode(true));

      posicionar.firstElementChild.style.opacity = 0;
      posicionar.firstElementChild.style.width = "100%";
      posicionar.firstElementChild.style.height = "100%";
      posicionar.firstElementChild.style.margin = "auto";
    }
  }
}


function revisarEspacios() {
  for (let i = 1; i < filas.length; i++) {

    for(let j = 1; j < columnas.length; j++) {
      let celda = tableroNaves.querySelector("."+filas[i]+columnas[j]);

      if (!celda.hasChildNodes()) {
        celda.classList.remove("posicionado");
      }
    }
  }
}

// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// FUNCIONES / EVENTOS PARA SALVOES
// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

function onClick(e) {
  let cantSalvo = tableroSalvo.querySelectorAll(".active").length;
  let ajeno = tableroNaves.querySelectorAll(".hit").length + tableroNaves.querySelectorAll(".fail").length;
  let propio = tableroSalvo.querySelectorAll(".hit").length + tableroSalvo.querySelectorAll(".fail").length;

  if(e.target.classList.contains("active")) {
    e.target.classList.remove("active");
    cantSalvo--;
  } else if(cantSalvo < 5) {
    e.target.classList.add("active");
    cantSalvo++;
  }

  if (cantSalvo < 5 || app.turnos(propio, ajeno)) {
    document.querySelector(".btnShot").setAttribute("disabled", "true");
  } else {
    document.querySelector(".btnShot").removeAttribute("disabled");
  }
}

function onShot(e) {
  let shots = tableroSalvo.querySelectorAll(".active");
  let locations = [];

  for (let i = 0; i < shots.length; i++) {
    let shot = shots[i].className.split(" ");
    for (let j = 0; j < shot.length; j++) {
      if(shot[j].length < 4) {
        locations[i] = shot[j];
      }
    }
  }

  let salvo = {"locations": locations};

  console.log(locations);

  let url = "/api/games/players/" + gp + "/salvos";
  fetch(url, {
    method: "POST",
    body: JSON.stringify(salvo),
    headers: new Headers({
      'Content-Type': 'application/json'
    })
  }).then(res => res.json()
  ).then(response => {
    let rta = response;

    if(rta.status == "400" || rta.status == "401" || rta.status == "403" || rta.status == "406" || rta.status == "405") {
      alert(rta.msg);
    } else if(rta.status == "200") {
      stompClient.send("/api/turno/" + gp, {}, JSON.stringify(locations));
      app.hits = rta.hits;
      app.removerTablas();
      app.hacerTablas();
      app.mostrarNaves();
      app.mostrarSalvoesPropios();
      app.mostrarSalvoesAjenos();
      document.querySelector(".btnShot").setAttribute("disabled", "true");
      app.sunkedRival(rta.sunked);
    } else {
      alert("Algo raro paso!");
    }
  })

}
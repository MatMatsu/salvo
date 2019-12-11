var app = new Vue({
  el: "#app",
  data: {
    columnas: ["0","1","2","3","4","5","6","7","8","9","10"],
    filas: ["0","A","B","C","D","E","F","G","H","I","J"],
    game: [],
    naves: [],
    tipoNaves: [],
    marcasPropias: [],
    marcasAjenas: [],
    hits: [],
    playerOne: {},
    playerTwo: {},
    gp: new URLSearchParams(window.location.search).get("gp")
  },
  methods: {
    // FETCH DATA
    fetchData() {
      let url = "/api/game_view/" + this.gp;
      fetch(url).then(response=> response.json()
      ).then(values=>{
        this.game = values;
        this.naves = this.objNaves(this.game.ships);
        
        this.tipoNaves = values.shipTypes;

        if (this.game.gamePlayers.length == 2) {
          if(this.gp == this.game.gamePlayers[0].id) {
            this.playerOne = this.game.gamePlayers[0].player;
            this.playerTwo = this.game.gamePlayers[1].player;
          } else {
            this.playerOne = this.game.gamePlayers[1].player;
            this.playerTwo = this.game.gamePlayers[0].player;
          }
          if(this.game.salvoes[0][this.playerOne.id] == undefined) {
            this.marcasPropias = this.game.salvoes[1][this.playerOne.id];
            this.marcasAjenas = this.game.salvoes[0][this.playerTwo.id];
          } else {
            this.marcasPropias = this.game.salvoes[0][this.playerOne.id];
            this.marcasAjenas = this.game.salvoes[1][this.playerTwo.id];
          }
        } else {
          this.playerOne = this.game.gamePlayers[0].player;
          this.playerTwo = {"email": "Waiting for player 2..."};
          this.marcasPropias = this.game.salvoes[0][this.playerOne.id];
          this.marcasAjenas = this.game.salvoes[0][this.playerTwo.id];
        }
        this.removerTablas();
        this.hacerTablas();
        this.mostrarNaves();
        this.mostrarSalvoesPropios();
        this.mostrarSalvoesAjenos();
        this.partidaIniciada(); 
        this.conectar();
        this.fetchInicio();
        if (this.marcasPropias.length = 0) {
          document.querySelector(".turno").textContent = "TURN: 1";
        }
        this.tablaNaves();
        

      }).catch (error => {
        //window.location.replace("/web/games.html?error=authForb");
      })
    },
    fetchNaves(ships) {
      let url = "/api/games/players/" + gp + "/ships";
      fetch(url, {
        method: "POST",
        body: JSON.stringify(ships),
        headers: new Headers({
          'Content-Type': 'application/json'
        })
      }).then(res => res.json()
      ).then(response => {
        console.log(response);
        let rta = response;

        if(rta.status == "400" || rta.status == "401" || rta.status == "403" || rta.status == "406" || rta.status == "405") {
          alert(rta.msg);
        } else if(rta.status == "200") {
          window.location.replace("/web/game.html?gp=" + gp);
        } else {
          alert("Algo raro paso!");
        }
      })
    },
    fetchInicio() {
      let url = "/api/games/players/" + this.gp + "/salvos";
      let tablero = document.querySelector(".tableroSalvo");
      fetch(url).then(res => res.json()
      ).then(response => {
        let rta = response;

        if(rta.status == "400" || rta.status == "401" || rta.status == "403" || rta.status == "406" || rta.status == "405") {
          alert(rta.msg);
        } else if(rta.status == "200") {
          this.hits = rta.hits;
          this.sunkedRival(rta.sunked);
          for (let i = 0; i < this.hits.length; i++) {
            tablero.querySelector("." + this.hits[i]).classList.remove("fail");
            tablero.querySelector("." + this.hits[i]).classList.add("hit");
          }
        } else {
          alert("Algo raro paso!");
        }
      })
    },
    //METHODS
    hacerTablas() {
      for (let k = 0; k < 2; k++) {
        for (let i = 0; i < filas.length; i++) {
          let divFila = document.createElement("div");
          divFila.classList.add("filaDiv");

          for(let j = 0; j < columnas.length; j++) {
            if(i == 0) {
              let divCol = document.createElement("div");
              divCol.classList.add(filas[i]+columnas[j]);
              divCol.classList.add("headDiv");
              divCol.textContent = columnas[j];
              divFila.appendChild(divCol);
            } else if(j == 0) {
              let divCol = document.createElement("div");
              divCol.classList.add(filas[i]+columnas[j]);
              divCol.classList.add("headDiv");
              divCol.textContent = filas[i];
              divFila.appendChild(divCol);
            } else {
              let divCol = document.createElement("div");
              divCol.classList.add(filas[i]+columnas[j]);
              divCol.classList.add("celdaDiv");
              if (k == 0) {
                divCol.setAttribute("ondragover","onDragOver(event)");
                divCol.setAttribute("ondragenter","onDragEnter(event)");
                divCol.setAttribute("ondragleave","onDragLeave(event)");
                divCol.setAttribute("ondrop", "onDrop(event)");
              } else {
                divCol.setAttribute("onclick", "onClick(event)");
              }
              divFila.appendChild(divCol);
            }

          }
          if (k == 0) {
            document.querySelector(".tableroNaves").appendChild(divFila);
          } else {
            document.querySelector(".tableroSalvo").appendChild(divFila);
          }
        }
      }

    },
    objNaves(naves) {
      let barcos = [];
      for (let i = 0; i < naves.length; i++) {
        let barco = {};
        barco.type = naves[i].type;
        let posiciones = naves[i].locations;
        barco.locations = [];
        for (let j = 0; j < posiciones.length; j++) {
          barco.locations[j] = {};
          barco.locations[j].location = posiciones[j];
          barco.locations[j].mark = false;
        }
        barco.sunk = false;
        barcos.push(barco);
      }
      return barcos;
    },
    mostrarNaves() {
      let tablero = document.querySelector(".tableroNaves");
      for(let i = 0; i < this.naves.length; i++) {
        let posiciones = this.naves[i].locations;
        for(let j = 0; j < posiciones.length; j++) {
          tablero.querySelector("."+posiciones[j].location).classList.add("nave");
        }
      }
    },
    mostrarSalvoesPropios() {
      let tablero = document.querySelector(".tableroSalvo");
      for(let i = 0; i < this.marcasPropias.length; i++) {
        let posiciones = this.marcasPropias[i].locations;
        let turno = this.marcasPropias[i].turn;
        for (let j = 0; j < posiciones.length; j++) {
          if (!this.hits.indexOf(posiciones[j]) > -1) {
            tablero.querySelector("."+posiciones[j]).classList.remove("hit");
            tablero.querySelector("."+posiciones[j]).classList.add("fail");
            tablero.querySelector("."+posiciones[j]).textContent = turno;
            tablero.querySelector("."+posiciones[j]).removeAttribute("onclick");
          }
        }
        document.querySelector(".turno").textContent = "TURN: "+ (turno+1);
      }

      for (let i = 0; i < this.hits.length; i++) {
        tablero.querySelector("." + this.hits[i]).classList.remove("fail");
        tablero.querySelector("." + this.hits[i]).classList.add("hit");
      }
    },
    buscarSalvoesAjenos(salvo){
      let misNaves = document.querySelector(".misNaves");
      for (let i = 0; i < this.naves.length; i++) {
        let posiciones = this.naves[i].locations;
        for (let j = 0; j < posiciones.length; j++) {
          let posicion = posiciones[j].location;
          if(posicion == salvo){
            posiciones[j].mark = true;
            console.log("The " + this.naves[i].type + " was HIT!");

            let cantMarcas = posiciones.reduce((total, num)=>{
              total += (num.mark==true)?1:0;
              return total;
            },0);

            if(cantMarcas == posiciones.length) {
              this.naves[i].sunk = true;
              misNaves.querySelector("."+this.naves[i].type).textContent = "SUNKED";
              console.log("The " + this.naves[i].type + " was SUNKED!");
            }

            return 1;
          }
        }
      }
      return 0;
    },
    mostrarSalvoesAjenos() {
      let tablero = document.querySelector(".tableroNaves");

      for(let i = 0; i < this.marcasAjenas.length; i++) {
        let posiciones = this.marcasAjenas[i].locations;
        let turno = this.marcasAjenas[i].turn;
        for (let j = 0; j < posiciones.length; j++) {
          if (this.buscarSalvoesAjenos(posiciones[j])==1) {
            tablero.querySelector("."+posiciones[j]).classList.remove("fail");
            tablero.querySelector("."+posiciones[j]).classList.add("hit");
            tablero.querySelector("."+posiciones[j]).textContent = turno;
          } else {
            tablero.querySelector("."+posiciones[j]).classList.remove("hit");
            tablero.querySelector("."+posiciones[j]).classList.add("fail");
            tablero.querySelector("."+posiciones[j]).textContent = turno;
          }
        }
      }
    },
    tablaNaves() {
      let tabla = document.createElement("table");
      tabla.setAttribute("class", "tablaNaves");
      let tablaHead = document.createElement("thead");
      let trHead = document.createElement("tr");
      let th = document.createElement("th");
      th.textContent = "Your Ships";
      trHead.appendChild(th);
      tablaHead.appendChild(trHead);
      let tablaBody = document.createElement("tbody");

      for(let i = 0; i < this.naves.length; i++) {
        let tr = document.createElement("tr");
        let td = document.createElement("td");
        td.textContent = this.naves[i].type;
        tr.appendChild(td);
        tablaBody.appendChild(tr);
      }
      tabla.appendChild(tablaHead);
      tabla.appendChild(tablaBody);
      document.querySelector("#misNaves").appendChild(tabla);
    },
    partidaIniciada() {
      if (this.naves.length == 0) {
        document.querySelector(".tableroSalvo").style.display = "none";
      }

      if (this.naves.length > 0) {
        document.querySelector(".navesZone").style.display = "none";
        let tablero = document.querySelector(".tableroNaves");
        let celdas = tablero.querySelectorAll(".celdaDiv");
        for (let i = 0; i < celdas.length; i++) {
          celdas[i].removeAttribute("ondragleave");
          celdas[i].removeAttribute("ondragLeave");
          celdas[i].removeAttribute("ondragover");
          celdas[i].removeAttribute("ondrop");
          celdas[i].removeAttribute("ondragenter");
        }
      }
    },
    turnos(propio, ajeno) {
      let cantPropias = propio;
      let cantRival = ajeno;

      let respuesta = false;

      if (cantPropias > cantRival) {
        respuesta = true;
      }

      return respuesta;

    },
    removerTablas() {
      let naves = document.querySelector(".tableroNaves");
      let salvo = document.querySelector(".tableroSalvo");

      while(naves.childNodes.length > 0) {
        naves.removeChild(naves.childNodes[0]);
        salvo.removeChild(salvo.childNodes[0]);
      }

    },
    sunkedRival(sunked) {
      let rivalNaves = document.querySelector(".rivalNaves");

      if (sunked.length != 0) {
        for (let i = 0; i < sunked.length; i++) {
          rivalNaves.querySelector("." + sunked[i]).textContent = "SUNKED";
        }
      }
    },
    //EVENTOS
    enviarNaves(event) {
      let tableroNaves = document.querySelector(".tableroNaves");
      let nave1 = tableroNaves.querySelectorAll(".patrolBoat");
      let nave2 = tableroNaves.querySelectorAll(".submarine");
      let nave3 = tableroNaves.querySelectorAll(".destroyer");
      let nave4 = tableroNaves.querySelectorAll(".battleship");
      let nave5 = tableroNaves.querySelectorAll(".carrier");
      let naves = [nave1, nave2, nave3, nave4, nave5];
      let ships = [];

      for (let i = 0; i < naves.length; i++) {
        if (naves[i].length == 0) {
          alert("ERROR. Falta ubicar naves");
          return;
        }
      }
      for (let i = 0; i < naves.length; i++) {
        let posiciones = [];

        for (let j = 0; j < naves[i].length; j++) {
          naves[i][j].parentNode.classList.remove("posicionado");
          naves[i][j].parentNode.classList.remove("celdaDiv");
          posiciones[j] = naves[i][j].parentNode.className;
        }

        naves[i][0].classList.remove("H");
        naves[i][0].classList.remove("V");

        ships[i] = {"nave": "",
                    "locations": posiciones}
      }

      ships[0].nave = patrolBoat;
      ships[1].nave = submarine;
      ships[2].nave = destroyer;
      ships[3].nave = battleship;
      ships[4].nave = carrier;

      this.fetchNaves(ships);
    },
    // WEBSOCKET
    conectar() {
      var socket = new SockJS('/gs-guide-websocket');
      stompClient = Stomp.over(socket);
      stompClient.connect({}, function (frame) {
        //console.log('Connected: ' + frame);
        
        stompClient.subscribe('/topic/finTurnos', function (resp) {
          app.fetchData();
          let t = JSON.parse(resp.body).body.msg.split(" ");
          document.querySelector(".turno").textContent = "TURN: " + t[0];
        })
      })
    }
  },
  // CREATED
  created() {
    this.fetchData();
    //this.fetchInicio();
  }
})
// covylorientacion@gmail.com - asunto - c7 nombre y apellido
var app = new Vue({
  el: "#app",
  data: {
    allGames: [],
    user: {},
    players: [],
    finishGames: [],
    name: "",
    pass: "",
    rta: {}
  },
  created() {
    this.fetchData();
  },
  methods: {
    // FETCH'S
    fetchData() {
      let url = "/api/games";
      //try {
        fetch(url).then(response => response.json()          
        ).then(values=>{
          this.allGames = values.games;
          this.user = values.player;
          this.players = this.getPlayers(this.allGames);
          this.finishGames = this.getFinishGames(this.allGames);
          this.getDataFinishGames(this.finishGames, this.players);
          this.createGamesTable(this.allGames);
          this.createLeaderboard(this.players);
          this.setLink();
        })
      //} catch (e) {
        //window.location("/web/games.html");
      //}
    },
    regFetchData() {
      let url = "/api/players"
      fetch(url, {
        method: "POST",
        body: JSON.stringify({userName: this.name, password: this.pass}),
        headers: new Headers({
          "Content-Type": "application/json"
        })
      }).then(res => res.json()
      ).then(response => {
        this.rta = response;
        if(this.rta.status == "400") {
          alert(this.rta.msg);
        } else if(this.rta.status == "200") {
          alert("Usuario registrado exitosamente. Ahora logueate!");
          document.querySelector("#username").value = document.querySelector("#newUsername").value;
          let jug = {
            email: this.name,
            gp: [],
            lost: 0,
            tied: 0,
            total: 0,
            won: 0
          }
          this.players.push(jug);
        } else {
          alert("Algo raro paso!");
        }
        document.querySelector("#newUsername").value = "";
        document.querySelector("#newPassword").value = "";
        this.fetchData();
      })
    },
    newGame() {
      let url = "/api/games"
      fetch(url, {
        method: "POST",
        /*body: JSON.stringify({userName: this.user.name, password: "pass"}),
        headers: new Headers({
          'Content-Type': 'application/json'
        })*/
        body: this.user.name,
        headers: new Headers({
          'Content-Type': 'text/plain'
        })
      }).then(res => res.json()
      ).then(response => {
        this.rta = response;
        
        if(this.rta.status == "405" || this.rta.status == "401") {
          alert(this.rta.msg);
        } else if (this.rta.status == "200") {
          window.location.replace("/web/game.html?gp=" + this.rta.msg);
        } else {
          alert("Algo raro paso!");
        }
      })
    },
    joinGameFetch(gID) {
      let url = "/api/game/" + gID + "/players";
      fetch(url, {
        method: "POST",
        /*body: JSON.stringify({userName: this.user.name, password: "pass"}),
        headers: new Headers({
          'Content-Type': 'application/json'
        })*/
        body: this.user.name,
        headers: new Headers({
          'Content-Type': 'text/plain'
        })
      }).then(res => {
        //if(!res.ok) {
        //  throw Error(res.statusText);
        //}
        console.log(res)
        return res.json()
      }).then(response => {
        console.log(response);
        this.rta = response;

        /*if(Object.keys(this.rta)[0] == "ERROR") {
          alert(this.rta.ERROR);
        } else {
          console.log(this.rta);
          window.location.replace("/web/game.html?gp=" + this.rta.msg);
        }*/
        if(this.rta.status == "400" || this.rta.status == "401" || this.rta.status == "403" || this.rta.status == "404" || this.rta.status == "405") {
          alert(this.rta.msg);
        } else if(this.rta.status == "200") {
          window.location.replace("/web/game.html?gp=" + this.rta.msg);
        } else {
          $(".modal").modal("show");
          
          //alert("Algo raro paso!");
        }
      })//.catch(error=>alert("Sign in to play!"));
    },
    // FUNCTIONS    
    getFinishGames(juegos) {
      let terminados = [];
      for(let i = 0; i < juegos.length; i++) {
        if(juegos[i].finishDate !== "") {
          terminados.push(juegos[i]);
        }
      }
      return terminados;
    },
    getPlayers(juegos) {
      let jugadores = [];
      let auxJugadores = [];
      for(let i = 0; i < juegos.length; i++) {
        for(let j = 0; j < juegos[i].gamePlayers.length; j++) {
          let refPlayer = juegos[i].gamePlayers[j].player.email;
          let refGPId = juegos[i].gamePlayers[j].id;
          let jugador = {
            "email": "",
            "total": 0,
            "won": 0,
            "lost": 0,
            "tied": 0,
            "gp": []
          };

          let aux = auxJugadores.indexOf(refPlayer);

          if(aux == -1) {
            jugador.email = refPlayer;
            auxJugadores.push(refPlayer);
            jugador.gp.push(refGPId);
            jugadores.push(jugador);
          } else {
            jugadores[aux].gp.push(refGPId);
          }
        }
      }
      return jugadores;
    },
    getDataFinishGames(juegosTerminados, jugadores) {
      let jugador = [];
      for(let i = 0; i < jugadores.length; i++) {
        jugador.push(jugadores[i].email);
      }
      for(let i = 0; i < juegosTerminados.length; i++) {
        for(let j = 0; j < juegosTerminados[i].gamePlayers.length; j++) {
          let gamePlayer = juegosTerminados[i].gamePlayers[j].player;
          let pos = jugador.indexOf(gamePlayer.email);
          if(gamePlayer.scores[0] > 0.5) {
            jugadores[pos].total += gamePlayer.scores[0];
            jugadores[pos].won++;
          } else if(gamePlayer.scores[0] < 0.5) {
            jugadores[pos].lost++;
          } else {
            jugadores[pos].total += gamePlayer.scores[0];
            jugadores[pos].tied++;
          }
        }
      }
      jugadores.sort((a,b)=>{return b.total - a.total || a.lost - b.lost});
    },
    createLeaderboard(jugadores) {
      document.querySelector(".bodyLeaderboard").innerHTML = "";
      for(let i = 0; i < jugadores.length; i++) {
        let tr = document.createElement("tr");

        for(x in jugadores[i]) {
          if(x != "gp") {
            let td = document.createElement("td");
            td.innerHTML = jugadores[i][x];
            tr.appendChild(td);
          }
        }
        document.querySelector(".bodyLeaderboard").appendChild(tr);
      }
    },
    createGamesTable(juegos) {
      document.querySelector(".bodyGamesTable").innerHTML = "";
      for(let i = 0; i < juegos.length; i++) {
        let tr = document.createElement("tr");
        let a = document.createElement("a");
        let td = document.createElement("td");
        let p = document.createElement("p");
        let btn = document.createElement("button");

        /*REORDENAR FECHA Y HORA*/
        let fecha = juegos[i].created;
        fecha = fecha.split("T");
        fecha[0] = fecha[0].split("-");
        fecha[1] = fecha[1].slice(0, fecha[1].indexOf("."));
        a.innerHTML = fecha[0][2] + "/" + fecha[0][1] + "/" + fecha[0][0] + " " + fecha[1] + "hs";
        a.classList.add("game");
        td.appendChild(a);
        tr.appendChild(td);

        let gamePlayerRef = juegos[i].gamePlayers; /*REF A LOS GAMEPLAYERS EN ALLGAMES*/

        for(let j = 0; j < 2; j++) {
          p = document.createElement("p");
          td = document.createElement("td");
          if(gamePlayerRef[j] == undefined) {
            p.innerHTML = "Waiting for a oponent...";
            td.appendChild(p);
            btn.innerHTML = "Join";
            btn.setAttribute("data-game", i+1);
            btn.addEventListener("click", this.joinGame);
            td.appendChild(btn);
            tr.appendChild(td);
          } else {
            p.innerHTML = gamePlayerRef[j].player.email;
            td.appendChild(p);
            (this.user.name != "Anonymous")?tr.classList.add("gp" + gamePlayerRef[j].id):0;
            tr.appendChild(td);
          }
        }
        //tr.classList.add("game");
        document.querySelector(".bodyGamesTable").appendChild(tr);
      }
    },
    setLink() {
      // SI ESTA LOGUEADO PUEDE USAR EL EVENTO
      let ref = this.players.findIndex(p => p.email == this.user.name);
      if(ref > -1) {
        // TODOS LOS GPID DEL USER LOGUEADO
        this.user.gpId = this.players[ref].gp;

        // TODAS LAS PARTIDAS DE LA TABLA
        let partidas = document.querySelectorAll("[class^=gp]");

        for(let i = 0; i < partidas.length; i++) {
          // ARMA ARRAY DE GPID
          let part = partidas[i].className.split(" ");
          for(let j = 0; j < this.user.gpId.length; j++) {
            let aux = "gp" + this.user.gpId[j];
            // BUSCA SI ALGUNO DE LOS GPID DEL USER ES IGUAL AL CLASS DEL ROW DE LA TABLA
            if(part.indexOf(aux) > -1) {
              partidas[i].querySelector(".game").setAttribute("href", "/web/game.html?gp=" + this.user.gpId[j]);
              j = this.user.gpId.length;
            }
          }
        }
      }
    },
    // EVENTS
    getValues(event) {
      this.name = document.querySelector("#newUsername").value;
      this.pass = document.querySelector("#newPassword").value;
      if (this.name.indexOf("@") == -1 || this.pass.length < 5) {
        this.name = "";
        this.pass = "";
        alert("Escriba un correo valido o la contraseña es muy corta");
      } else {
        this.regFetchData();
      }
    },
    joinGame(event) {
      /*let ref = this.players.findIndex(p => p.email == this.user.name);
      if(ref > -1) {
        let gID = event.path[0].dataset.game;
        this.joinGameFetch(gID);
      } else {
        alert("Sign in to play!");
      }*/
      let gID = event.target.dataset.game;
      console.log(gID);
      this.joinGameFetch(gID);
    }
  }
})
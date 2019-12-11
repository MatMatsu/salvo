package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.Tablero;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class SalvoController {
    //-----------------------------------------------------
    //  AUTOWIRED
    //-----------------------------------------------------
    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private ScoreRepository scoreRepo;

    @Autowired
    private PlayerRepository playRepo;

    @Autowired
    private GamePlayerRepository gamePlayRepo;

    @Autowired
    private ShipTypeRepository shipTypeRepository;

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private SalvoRepository salvoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Tablero tablero = new Tablero();

    //-----------------------------------------------------
    // METHODS
    //-----------------------------------------------------
    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    //-----------------------------------------------------
    // REQUEST MAPPING
    // GET
    //-----------------------------------------------------
    @RequestMapping("/games")
    private Map<String, Object> getMap (@Nullable Authentication auth) {
        //System.out.print("Hola mundo");
        Map<String, Object> player = new HashMap<>();

        Map<String, Object> result = new HashMap<String, Object>();

        result.put("games", gameRepo.findAll().stream().map(Game::makeGameScoreDTO));

        if(isGuest(auth)) {
            player.put("id", null);
            player.put("name", "Anonymous");
        } else {
            player.put("id", playRepo.findByUserName(auth.getName()).getId());
            player.put("name", auth.getName());
            player.put("role", auth.getAuthorities());
        }

        result.put("player", player);
        return result;
    }

    @RequestMapping("/game_view/{gamePlayer_id}")
    public ResponseEntity<Map<String, Object>> findGame(@PathVariable Long gamePlayer_id, Authentication auth) {
        GamePlayer gamePlayer = gamePlayRepo.findById(gamePlayer_id).get();
        Long userId = playRepo.findByUserName(auth.getName()).getId();

        if(gamePlayer.getPlayer().getId() == userId || userId == 1) {
            Map<String, Object> result = gamePlayer.getGame().makeGameDTO();
            result.put("ships", gamePlayer.getPlayerShip());
            result.put("salvoes", gamePlayer.getGame().makeSalvoesDTO());
            result.put("shipTypes", shipTypeRepository.findAll().stream().map(ShipType::makeShipTypeDTO).collect(toList()));
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            Map<String, Object> result = new HashMap<>();
            result.put("ERROR","No autorizado");
            return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
        }
    }

    //-----------------------------------------------------
    // REQUEST MAPPING
    // POST
    //-----------------------------------------------------
    @RequestMapping(value="/players", method=RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createPlayer(@RequestBody Player player) {
        Player playerExist = playRepo.findByUserName(player.getUserName());
        Map<String, Object> result = new HashMap<>();

        if (playerExist != null) {
            result.put("status", "400");
            result.put("msg", "The user alredy exist!");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            Player newPlayer = new Player(player.getUserName(),passwordEncoder.encode(player.getPassword()));
            playRepo.save(newPlayer);
        }
        result.put("status", "200");
        result.put("msg", "Player created!");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value="/games", method=RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(@RequestBody String name, Authentication auth) {

        Player user = playRepo.findByUserName(name);
        Map<String, Object> result = new HashMap<>();

        if (!name.equals(auth.getName())) {
            result.put("status", "405");
            result.put("msg", "Error: Piilin, eso está mal!" + auth.getName() + " " + name);
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }

        if(user == null) {
            result.put("status", "401");
            result.put("msg", "Error: Unauthorized. Log in!");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        } else {
            Game nuevoJuego = new Game(LocalDateTime.now());
            gameRepo.save(nuevoJuego);

            GamePlayer nuevoGP = new GamePlayer(nuevoJuego, user);
            gamePlayRepo.save(nuevoGP);

            result.put("status", "200");
            result.put("msg", nuevoGP.getId());
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        }
    }

    @RequestMapping(value="/game/{game_id}/players", method=RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(@RequestBody String name, @PathVariable Long game_id, Authentication auth) {
        // Usuario que ingresa
        Player user = playRepo.findByUserName(name);
        // Juego en el que se esta intentando entrar
        Game game = gameRepo.findById(game_id).get();
        // Lista los jugadores que haya en la partida
        List<GamePlayer> gamePlayers = gamePlayRepo.findByGame(game);
        // Player ya en el juego
        Player pInGame = gamePlayers.get(0).getPlayer();

        Map<String, Object> result = new HashMap<>();

        if (!name.equals(auth.getName())) {
            result.put("status", "405");
            result.put("msg", "Error: Piilin, eso está mal!");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }

        if(user == null) {
            result.put("status", "401");
            result.put("msg", "Error: Unauthorized. Log in!");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        } else if( game == null) {
            result.put("status", "404");
            result.put("msg", "Error: No such game!");
            return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
        } else if(gamePlayers.size() == 2) {
            result.put("status", "403");
            result.put("msg", "Error: Game is full!");
            return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
        } else if(user == pInGame) {
            result.put("status", "403");
            result.put("msg", "Error: You can't join your own game!");
            return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
        } else {
            GamePlayer nuevoGP = new GamePlayer(game, user);
            gamePlayRepo.save(nuevoGP);

            result.put("status", "200");
            result.put("msg", nuevoGP.getId());
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        }
    }

    @RequestMapping(value="/games/players/{gamePlayer_Id}/ships", method=RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> placeShips(@RequestBody List<Ship> ships, @PathVariable Long gamePlayer_Id, Authentication auth) {
        // GP QUE INGRESA
        GamePlayer userGP = gamePlayRepo.findById(gamePlayer_Id).get();

        Map<String, Object> result = new HashMap<>();

        // CONTROL DE ASIGNACION DE SHIPS
        if (!userGP.getPlayer().getUserName().equals(auth.getName())) {
            result.put("status", "403");
            result.put("msg", "Error: El player ya tiene ships asignados!");
            return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
        }

        // CONTROL DE ASIGNACION DE SHIPS
        if (userGP.getPlayerShip().size() != 0) {
            result.put("status", "405");
            result.put("msg", "Error: Piilin, eso está mal!");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        }

        // CONTROL CANTIDAD DE SHIPS
        if (ships.size() != 5) {
            result.put("status", "406");
            result.put("msg", "Error: Faltan o hay Ships de más!");
            return new ResponseEntity<>(result, HttpStatus.NOT_ACCEPTABLE);
        }

        // CONTROL DE SHIPTYPE
        Set<String> buscarDuplicados = new HashSet<>(); // PARA CONTROL DE SUPERPOSICIONES
        for(Ship ship : ships) {
            // SHIPTYPE EXISTE
            Boolean exist = shipTypeRepository.findAll()
                                                .stream()
                                                .anyMatch(st->st.getType().equals(ship.getNave().getType()));
            if (!exist) {
                result.put("status", "406");
                result.put("msg", "Error: " + ship.getNave().getType() + " not exist!");
                return new ResponseEntity<>(result, HttpStatus.NOT_ACCEPTABLE);
            }

            // SHIP LOCATION CORRECTO x BARCO
            if (!tablero.controlPosicion(ship.getLocations())) {
                result.put("status", "406");
                result.put("msg", "Error: " + ship.getNave().getType() + " bad position!");
                return new ResponseEntity<>(result, HttpStatus.NOT_ACCEPTABLE);
            }


            // SIN SUPERPOSICIONES
            for(int i = 0; i < ship.getLocations().size(); i++) {
                if(!buscarDuplicados.add(ship.getLocations().get(i))) {
                    result.put("status", "406");
                    result.put("msg", "Error: " + ship.getNave().getType() + " bad position!");
                    return new ResponseEntity<>(result, HttpStatus.NOT_ACCEPTABLE);
                }
            }
        }

        // SHIPTYPE UNICOS
        Boolean repeated = ships.stream()
                .filter(f -> Collections.frequency(ships.stream()
                        .collect(toList()), f)>1)
                .toArray().length > 1;
        if (repeated) {
            result.put("status", "406");
            result.put("msg", "Error: Only one ship of each one!");
            return new ResponseEntity<>(result, HttpStatus.NOT_ACCEPTABLE);
        }

        for(Ship ship : ships) {
            Ship newShip = new Ship(shipTypeRepository.findByType(ship.getNave().getType()), ship.getLocations(), userGP);
            shipRepository.save(newShip);
        }

        result.put("status", "200");
        result.put("msg", "Ships posicionados correctamente");
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @RequestMapping(value="/games/players/{gamePlayer_Id}/salvos", method=RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> shotSalvo(@RequestBody Salvo salvo, @PathVariable Long gamePlayer_Id, Authentication auth) {
        // GP QUE INGRESA
        GamePlayer userGP = gamePlayRepo.findById(gamePlayer_Id).get();
        GamePlayer rivalGP;

        List<Salvo> salvoes = salvoRepository.findByGamePlayers(userGP);

        int turno = salvoes.size();

        Map<String, Object> result = new HashMap<>();

        // VERIFICAR SI HAY 2 GAMEPLAYERS
        Game juegoActual = gameRepo.findByGamePlayers(userGP);
        List<GamePlayer> jugadoresActuales = juegoActual.getGamePlayers().stream().collect(toList());

        if (jugadoresActuales.size() < 2) {
            result.put("status", "406");
            result.put("msg", "Error: Aún no tenes rival!");
            return new ResponseEntity<>(result, HttpStatus.NOT_ACCEPTABLE);
        }

        if (!jugadoresActuales.get(0).equals(userGP)) {
            rivalGP = jugadoresActuales.get(0);
        } else {
            rivalGP = jugadoresActuales.get(1);
        }


        // VERIFICAR TURNO DE CADA JUGADOR
        int turnoRival = 0;
        turnoRival = salvoRepository.findByGamePlayers(rivalGP).size();
        if (turno > turnoRival) {
            result.put("status", "406");
            result.put("msg", "Error: Tu rival no completo aún su turno!");
            return new ResponseEntity<>(result, HttpStatus.NOT_ACCEPTABLE);
        }

        // EMPIEZA EL NUEVO TURNO
        turno = turno + 1;

        // CONTROL CANTIDAD DE SHIPS
        if (salvo.getLocations().size() != 5) {
            result.put("status", "406");
            result.put("msg", "Error: Faltan o hay Salvoes de más!");
            return new ResponseEntity<>(result, HttpStatus.NOT_ACCEPTABLE);
        }

        // CONTROL DE SALVO
        Set<String> buscarDuplicados = new HashSet<>(); // PARA CONTROL DE SUPERPOSICIONES
        // SIN SUPERPOSICIONES
        for(int i = 0; i < salvo.getLocations().size(); i++) {
            if(!buscarDuplicados.add(salvo.getLocations().get(i))) {
                result.put("status", "406");
                result.put("msg", "Error: " + salvo.getLocations().get(i) + " bad position!");
                return new ResponseEntity<>(result, HttpStatus.NOT_ACCEPTABLE);
            }
        }

        // SALVO REPETIDO
        if (salvoes.size() > 0) {
            // RECORRO LOS SALVOES ALMACENADOS
            for (Salvo tiro : salvoes) {
                // RECORRO LOS SALVOES ENVIADOS
                for (int i = 0; i < salvo.getLocations().size(); i++) {
                    // COMPARO SI ALGUN SALVO ENVIADO SE ENCUENTRA ENTRE LOS SALVOES ALMACENADOS
                    if (tiro.getLocations().contains(salvo.getLocations().get(i))){
                        result.put("status", "406");
                        result.put("msg", "Error: " + salvo.getLocations().get(i) + " was fired before!");
                        return new ResponseEntity<>(result, HttpStatus.NOT_ACCEPTABLE);
                    }
                }
            }
        }


        Salvo newSalvo = new Salvo(turno, userGP, salvo.getLocations());
        salvoRepository.save(newSalvo);

        // LISTA DE SHIPS RIVAL
        List<Ship> rivalShips = shipRepository.findByGamePlayers(rivalGP);

        // LISTA DE LISTA DE TIROS + TIROS RECIENTE
        List<List<String>> shotList = salvoes.stream().map(s -> s.getLocations()).collect(toList());
        shotList.add(salvo.getLocations());

        // LISTA SOLO DE TIROS
        List<String> shots = new LinkedList<>();
        shotList.stream().forEach(sl -> sl.forEach(s -> {
            shots.add(s);
        }));

        // LISTA Y CONTADOR PARA HITS Y HUNDIDOS
        List<String> hits = new LinkedList<>();
        List<String> sunked = new LinkedList<>();
        final Integer[] contador = {0};

        rivalShips.stream().forEach(rShips -> {
            contador[0] = 0;
            rShips.getLocations().forEach(rShipsLocation -> {
                // VERIFICO HITS
                if(shots.contains(rShipsLocation)){
                    hits.add(rShipsLocation);
                    contador[0] = contador[0] + 1;
                }
                // VERIFICO HUNDIDOS
                if(rShips.getLocations().size() == contador[0]) {
                    sunked.add(rShips.getNave().getType());
                }
            });
        });

        result.put("status", "200");
        result.put("msg", "Salvoes posicionados correctamente");
        result.put("hits", hits);
        result.put("sunked", sunked);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @RequestMapping(value="/games/players/{gamePlayer_Id}/salvos")
    public ResponseEntity<Map<String, Object>> shotSalvo(@PathVariable Long gamePlayer_Id, Authentication auth) {
        // GP QUE INGRESA
        GamePlayer userGP = gamePlayRepo.findById(gamePlayer_Id).get();
        GamePlayer rivalGP;

        List<Salvo> salvoes = salvoRepository.findByGamePlayers(userGP);

        Map<String, Object> result = new HashMap<>();

        // VERIFICAR SI HAY 2 GAMEPLAYERS
        Game juegoActual = gameRepo.findByGamePlayers(userGP);
        List<GamePlayer> jugadoresActuales = juegoActual.getGamePlayers().stream().collect(toList());

        if (!jugadoresActuales.get(0).equals(userGP)) {
            rivalGP = jugadoresActuales.get(0);
        } else {
            rivalGP = jugadoresActuales.get(1);
        }

        // LISTA DE SHIPS RIVAL
        List<Ship> rivalShips = shipRepository.findByGamePlayers(rivalGP);

        // LISTA DE LISTA DE TIROS + TIROS RECIENTE
        List<List<String>> shotList = salvoes.stream().map(s -> s.getLocations()).collect(toList());

        // LISTA SOLO DE TIROS
        List<String> shots = new LinkedList<>();
        shotList.stream().forEach(sl -> sl.forEach(s -> {
            shots.add(s);
        }));

        // LISTA Y CONTADOR PARA HITS Y HUNDIDOS
        List<String> hits = new LinkedList<>();
        List<String> sunked = new LinkedList<>();
        final Integer[] contador = {0};

        rivalShips.stream().forEach(rShips -> {
            contador[0] = 0;
            rShips.getLocations().forEach(rShipsLocation -> {
                // VERIFICO HITS
                if(shots.contains(rShipsLocation)){
                    hits.add(rShipsLocation);
                    contador[0] = contador[0] + 1;
                }
                // VERIFICO HUNDIDOS
                if(rShips.getLocations().size() == contador[0]) {
                    sunked.add(rShips.getNave().getType());
                }
            });
        });

        result.put("status", "200");
        result.put("msg", "Salvoes posicionados correctamente");
        result.put("hits", hits);
        result.put("sunked", sunked);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
}


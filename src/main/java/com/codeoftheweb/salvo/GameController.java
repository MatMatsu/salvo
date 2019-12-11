package com.codeoftheweb.salvo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Controller
public class GameController {
    @Autowired
    private GamePlayerRepository gamePlayRepo;

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private SalvoRepository salvoRepo;

    @MessageMapping("/turno/{gamePlayer_id}")
    @SendTo("/topic/finTurnos")
    public ResponseEntity<Map<String, Object>> greeting(List<String> tiros, @DestinationVariable Long gamePlayer_id) throws Exception {
        Thread.sleep(1000); // simulated delay

        GamePlayer userGP = gamePlayRepo.findById(gamePlayer_id).get();
        List<Salvo> salvoes = salvoRepo.findByGamePlayers(userGP);

        int turno = salvoes.size();
        Map<String, Object> result = new HashMap<>();
        result.put("status", "200");

        Game juegoActual = gameRepo.findByGamePlayers(userGP);
        Set<GamePlayer> jugadoresActuales = juegoActual.getGamePlayers();

        // VERIFICAR TURNO DE CADA JUGADOR
        int turnoRival = 0;
        for (GamePlayer gp: jugadoresActuales) {
            if(!gp.equals(userGP)){
                turnoRival = salvoRepo.findByGamePlayers(gp).size();
                if (turno > turnoRival + 5) {
                    result.put("status", "406");
                    return new ResponseEntity<>(result, HttpStatus.NOT_ACCEPTABLE);
                }
            }
        }

        result.put("msg", turno + " " + turnoRival);
        //result.put("tiros", tiros);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}

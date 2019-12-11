package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    private String userName;
    private String password;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    private Set<Score> scores;

    public Player() { }

    public Player(String user, String pass) {
        userName = user;
        password = pass;
    }

    public Map<String, Object> makePlayerDTO(Player jugador) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", jugador.getId());
        dto.put("email", jugador.getUserName());
        return dto;
    }

    @Nullable
    public Map<String, Object> makePlayerScoreDTO(Player jugador, Game juego) {
        //Map<String, Object> dto = new LinkedHashMap<>();
        //dto.put("id", jugador.getId());
        //dto.put("email", jugador.getUserName());
        Map<String, Object> dto = makePlayerDTO(jugador);
        /*List<Object> puntos = scores.stream()
                                    .filter(a->a.getGame().equals(juego) && a.getPlayer().equals(jugador))
                                    .map(a->a.getPuntos())
                                    .collect(toList());
        if(puntos.get(0) == null) {
            dto.put("score", puntos.get(0));
        } else {
            dto.put("score", 0);
        }*/
        List<Object> sc = scores.stream()
                                .filter(a->a.getGame().equals(juego) && a.getPlayer().equals(jugador))
                                .map(Score::getPuntos)
                                .collect(toList());


        dto.put("scores", sc);
        return dto;
    }

    public Long getId() { return id; }

    public String getUserName() {
        return userName;
    }

    public String getPassword() { return password; }
    public void setPassword(String pass) { this.password = pass; }

    public String toString() {
        return userName;
    }

    public Set<Score> getScores() { return scores; }

}
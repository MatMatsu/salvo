package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    private LocalDateTime joinDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gameID")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="playerID")
    private Player player;

    @OneToMany(mappedBy="gamePlayers", fetch=FetchType.EAGER)
    private Set<Ship> ships;

    @OneToMany(mappedBy="gamePlayers", fetch=FetchType.EAGER)
    private Set<Salvo> salvoes;

    public GamePlayer() {}

    public GamePlayer(Game newGame, Player newPlayer) {
        joinDate = newGame.getCreationDate();
        game = newGame;
        player = newPlayer;
    }

    public Map<String, Object> makeGamePlayerDTO () {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", getId());
        dto.put("player", player.makePlayerDTO(getPlayer()));
        return dto;
    }

    @Nullable
    public Map<String, Object> makeGamePlayerScoreDTO () {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", getId());
        dto.put("player", player.makePlayerScoreDTO(getPlayer(), getGame()));
        return dto;
    }

    public List<Object> getPlayerShip () {
        return ships.stream().map(Ship::makeShipDTO).collect(toList());
    }

    public Map<Object, Object> getGameSalvoes () {
        Map<Object, Object> dto = new LinkedHashMap<>();
        dto.put(this.getPlayer().getId() , salvoes.stream().sorted(Comparator.comparingInt(Salvo::getTurno)).map(Salvo::makeSalvoDTO));
        return dto;
    }

    public Long getId() { return id; }

    public LocalDateTime getJoinDate() { return joinDate; }

    public Game getGame() { return game; }

    public Player getPlayer() { return player; }
}
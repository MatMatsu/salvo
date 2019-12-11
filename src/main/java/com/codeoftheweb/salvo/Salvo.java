package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Entity
public class Salvo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayerID")
    private GamePlayer gamePlayers;

    @ElementCollection
    @Column(name="locationID")
    private List<String> locations = new ArrayList<>();

    private int turno;

    public Salvo() {}

    public Salvo(int newTurno, GamePlayer newGamePlayer, List<String> newSalvoLocations) {
        turno = newTurno;
        gamePlayers = newGamePlayer;
        locations = newSalvoLocations;
    }

    public Map<String, Object> makeSalvoDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", getTurno());
        dto.put("player", gamePlayers.getPlayer().getId());
        dto.put("locations", getLocations());
        return dto;
    }

    public Long getId() { return id; }

    public List<String> getLocations() { return locations; }

    public GamePlayer getGamePlayers() { return gamePlayers; }

    public int getTurno() { return turno; }
}

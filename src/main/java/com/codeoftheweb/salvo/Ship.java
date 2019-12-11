package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Entity
public class Ship {
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

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="shipTypeID")
    private ShipType nave;

    public Ship() {}

    public Ship (ShipType newNave, List<String> locacion, GamePlayer newGamePlayer) {
        nave = newNave;
        locations = locacion;
        gamePlayers = newGamePlayer;
    }

    public Map<String, Object> makeShipDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("type", nave.getType());
        dto.put("locations", getLocations());
        return dto;
    }

    public Long getId() { return id; }

    public ShipType getNave() { return nave; }

    public GamePlayer getGamePlayers() { return gamePlayers; }

    public List<String> getLocations() { return locations; }
}

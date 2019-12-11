package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    private LocalDateTime creationDate;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    private Set<Score> scores;

    public Game() {}

    public Game(int hora) {
        creationDate = LocalDateTime.now().plusHours(hora);
    }

    public Game(LocalDateTime hora) {
        creationDate = hora;
    }

    public Map<String, Object> makeGameDTO () {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", getId());
        dto.put("created", getCreationDate());
        dto.put("gamePlayers", gamePlayers.stream().map(GamePlayer::makeGamePlayerDTO));
        return dto;
    }

    @Nullable
    public Map<String, Object> makeGameScoreDTO () {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", getId());
        dto.put("created", getCreationDate());
        List<Object> game = scores.stream()
                                    .filter(a->a.getGame().getId() == getId())
                                    .map(a->a.getFinishDate())
                                    .collect(toList());
        if(game.isEmpty()) {
            dto.put("finishDate", "");
        } else {
            dto.put("finishDate", game.get(0));
        }
        List<Object> gp = gamePlayers.stream()
                                    .map(GamePlayer::makeGamePlayerScoreDTO)
                                    .collect(toList());
        dto.put("gamePlayers", gp);
        return dto;
    }

    public List<Object> makeSalvoesDTO () {
        return gamePlayers.stream().sorted(Comparator.comparingLong(a->a.getPlayer().getId())).map(GamePlayer::getGameSalvoes).collect(toList());
    }

    public Long getId() { return id; }

    public LocalDateTime getCreationDate() { return creationDate; }

    public Set<GamePlayer> getGamePlayers() { return gamePlayers; }
}
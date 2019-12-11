package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    @Nullable
    private LocalDateTime finishDate;
    @Nullable
    private float puntos;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gameID")
    @Nullable
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="playerID")
    @Nullable
    private Player player;

    public Score() {}

    public Score(Game newGame, Player newPlayer, float newScore) {
        game = newGame;
        player = newPlayer;
        if(newScore > -1f) {
            puntos = newScore;
            finishDate = newGame.getCreationDate().plusMinutes(30);
        }
    }

    public Long getId() { return id; }

    public LocalDateTime getFinishDate() { return finishDate; }

    public float getPuntos() { return puntos; }

    public Game getGame() { return game; }

    public Player getPlayer() { return player; }
}
package org.underdogs.teams.domain;

import org.underdogs.players.domain.Player;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
@Access(AccessType.FIELD)
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long technicalId;

    @Embedded
    private TeamId id;

    @Column(nullable = false, unique = true, length = 80)
    private String name;

    @Column(nullable = false, unique = true, length = 10)
    private String tag;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private Game game;

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    private List<Player> players = new ArrayList<>();

    protected Team() {
    }

    private Team(TeamId id, String name, String tag, Game game) {
        this.id = id;
        this.name = name;
        this.tag = tag;
        this.game = game;
    }

    public static Team create(TeamId id, String name, String tag, Game game) {
        return new Team(id, name, tag, game);
    }

    public void update(String name, String tag, Game game) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (tag != null && !tag.isBlank()) {
            this.tag = tag;
        }
        if (game != null) {
            this.game = game;
        }
    }

    public Long getTechnicalId() {
        return technicalId;
    }

    public TeamId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public Game getGame() {
        return game;
    }

    public List<Player> getPlayers() {
        return players;
    }
}
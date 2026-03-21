package org.underdogs.players.domain;

import org.underdogs.teams.domain.Team;
import jakarta.persistence.*;

@Entity
@Table(name = "players")
@Access(AccessType.FIELD)
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long technicalId;

    @Embedded
    private PlayerId id;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(length = 50)
    private String role;

    @Column(nullable = false, length = 2)
    private String countryCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_technical_id", nullable = false)
    private Team team;

    protected Player() {
    }

    private Player(
            PlayerId id,
            String nickname,
            String fullName,
            String role,
            String countryCode,
            Team team
    ) {
        this.id = id;
        this.nickname = nickname;
        this.fullName = fullName;
        this.role = role;
        this.countryCode = countryCode;
        this.team = team;
    }

    public static Player create(
            PlayerId id,
            String nickname,
            String fullName,
            String role,
            String countryCode,
            Team team
    ) {
        return new Player(id, nickname, fullName, role, countryCode, team);
    }

    public void update(String nickname, String fullName, String role, String countryCode, Team team) {
        if (nickname != null && !nickname.isBlank()) {
            this.nickname = nickname;
        }
        if (fullName != null && !fullName.isBlank()) {
            this.fullName = fullName;
        }
        if (countryCode != null && !countryCode.isBlank()) {
            this.countryCode = countryCode;
        }

        this.role = role;

        if (team != null) {
            this.team = team;
        }
    }

    public Long getTechnicalId() {
        return technicalId;
    }

    public PlayerId getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public Team getTeam() {
        return team;
    }
}
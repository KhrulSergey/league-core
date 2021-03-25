package com.freetonleague.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "session")
@NoArgsConstructor
@AllArgsConstructor
@Data
@SequenceGenerator(name = "base_entity_seq", sequenceName = "session_id_seq", allocationSize = 1)
public class Session extends BaseEntity {

    @Column(name = "token")
    private String token;

    @Column(name = "auth_provider")
    String authProvider;

    @Column(name = "expiration")
    private LocalDateTime expiration;

    @ManyToOne
    @JoinColumn(name = "freetonleague_id", referencedColumnName = "league_id", nullable = false)
    private User user;

    public boolean isExpired() {
        return expiration.isBefore(LocalDateTime.now());
    }
}

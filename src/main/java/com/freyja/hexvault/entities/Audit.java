package com.freyja.hexvault.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "audit")
public class Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_id_gen")
    @SequenceGenerator(name = "audit_id_gen", sequenceName = "audit_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @JoinColumn(name = "username", nullable = false)
    private String username;

    @Column(name = "action_taken", nullable = false)
    private String actionTaken;

    @Column(name = "\"timestamp\"")
    private OffsetDateTime timestamp;

}
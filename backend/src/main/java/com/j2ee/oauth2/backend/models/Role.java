package com.j2ee.oauth2.backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, unique = true, nullable = false)
    private ERole name; // Utilisation de l'énumération ERole

    private String description; // Description du rôle

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "role_functionality",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "functionality_id")
    )
    private Set<Functionality> functionalities = new HashSet<>();
}

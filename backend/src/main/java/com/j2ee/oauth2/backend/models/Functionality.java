package com.j2ee.oauth2.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "functionalities")
@Getter
@Setter
@NoArgsConstructor
public class Functionality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String name; // Ex: "Tableaux de bord", "Point de vente"

    private String description; // Description de la fonctionnalité

    @ManyToMany(mappedBy = "functionalities", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Role> roles = new HashSet<>();

    public Functionality(String name, String description) {
        this.name = name;
        this.description = description;
    }
}

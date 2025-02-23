package com.j2ee.oauth2.backend.repository;

import com.j2ee.oauth2.backend.models.ERole;
import com.j2ee.oauth2.backend.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    // Recherche par mot-clé dans le nom du rôle
    @Query("SELECT r FROM Role r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Role> searchRoles(@Param("keyword") String keyword);
    Optional<Role> findByName(ERole name);

}

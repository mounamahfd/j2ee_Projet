package com.j2ee.oauth2.backend.controllers;
import com.j2ee.oauth2.backend.services.RoleService;
import com.j2ee.oauth2.backend.models.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    // 1️⃣ Obtenir tous les rôles
    @GetMapping
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    // 2️⃣ Obtenir un rôle par ID
    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    // 3️⃣ Ajouter un rôle
    @PostMapping
    public ResponseEntity<Role> addRole(@RequestBody Role role) {
        return ResponseEntity.ok(roleService.addRole(role));
    }

    // 4️⃣ Mettre à jour un rôle
    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable Long id, @RequestBody Role role) {
        return ResponseEntity.ok(roleService.updateRole(id, role));
    }

    // 5️⃣ Supprimer un rôle
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    // 6️⃣ Rechercher un rôle
    @GetMapping("/search")
    public List<Role> searchRoles(@RequestParam String keyword) {
        return roleService.searchRoles(keyword);
    }

    // 7️⃣ Assigner des fonctionnalités à un rôle
    @PostMapping("/{roleId}/assign-functionalities")
    public ResponseEntity<Role> assignFunctionalities(@PathVariable Long roleId, @RequestBody Set<Long> functionalityIds) {
        return ResponseEntity.ok(roleService.assignFunctionalitiesToRole(roleId, functionalityIds));
    }
}

package com.j2ee.oauth2.backend.services;

import com.j2ee.oauth2.backend.models.Functionality;
import com.j2ee.oauth2.backend.repository.FunctionalityRepository;
import com.j2ee.oauth2.backend.repository.RoleRepository;
import com.j2ee.oauth2.backend.models.Role;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final FunctionalityRepository functionalityRepository;

    // Récupérer tous les rôles
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // Récupérer un rôle par ID
    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rôle non trouvé avec ID: " + id));
    }

    // Ajouter un rôle
    public Role addRole(Role role) {
        return roleRepository.save(role);
    }

    // Mettre à jour un rôle
    public Role updateRole(Long id, Role roleDetails) {
        Role role = getRoleById(id);
        role.setName(roleDetails.getName());
        role.setDescription(roleDetails.getDescription());
        return roleRepository.save(role);
    }

    // Supprimer un rôle
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }

    // Rechercher un rôle par nom
    public List<Role> searchRoles(String keyword) {
        return roleRepository.searchRoles(keyword);
    }

    // Affecter une ou plusieurs fonctionnalités à un rôle
    public Role assignFunctionalitiesToRole(Long roleId, Set<Long> functionalityIds) {
        Role role = getRoleById(roleId);
        Set<Functionality> functionalities = functionalityRepository.findByIdIn(functionalityIds);
        role.setFunctionalities(functionalities);
        return roleRepository.save(role);
    }
}

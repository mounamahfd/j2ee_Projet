import { Component, OnInit } from '@angular/core';
import { RoleService, Role } from '../role.service';
import { HttpErrorResponse } from '@angular/common/http';
import Swal from 'sweetalert2';

export interface Functionality {
  id: number;
  name: string;
}

@Component({
  selector: 'app-role-management',
  templateUrl: './role-management.component.html',
  styleUrls: ['./role-management.component.css']
})
export class RoleManagementComponent implements OnInit {
  roles: Role[] = [];
  functionalities: Functionality[] = [];
  newRole: Role = { id: 0, name: '', description: '' };
  selectedRole: Role | null = null;
  showFunctionalityModal: boolean = false;
  showAddRoleModal: boolean = false;

  constructor(private roleService: RoleService) {}

  ngOnInit(): void {
    this.fetchRoles();
    this.fetchFunctionalities();
  }

  fetchRoles(): void {
    this.roleService.getAllRoles().subscribe(
      (data: Role[]) => {
        this.roles = data;
      },
      (error: HttpErrorResponse) => {
        console.error('Erreur lors de la récupération des rôles', error.message);
      }
    );
  }

  fetchFunctionalities(): void {
    this.roleService.getAllFunctionalities().subscribe(
      (data: Functionality[]) => {
        this.functionalities = data;
      },
      (error: HttpErrorResponse) => {
        console.error("Erreur lors de la récupération des fonctionnalités", error.message);
      }
    );
  }

  openAddRoleModal(): void {
    this.newRole = { id: 0, name: '', description: '' }; // Réinitialiser le formulaire
    this.showAddRoleModal = true;
  }

  addRole(): void {
    if (!this.newRole.name.trim()) return;

    this.roleService.addRole(this.newRole).subscribe(
      () => {
        this.fetchRoles();
        this.showAddRoleModal = false;
        Swal.fire('Succès', 'Nouveau rôle ajouté', 'success');
      },
      (error: HttpErrorResponse) => {
        console.error("Erreur lors de l'ajout du rôle", error.message);
      }
    );
  }

  deleteRole(id: number): void {
    this.roleService.deleteRole(id).subscribe(
      () => {
        this.fetchRoles();
        Swal.fire('Supprimé', 'Le rôle a été supprimé', 'success');
      },
      (error: HttpErrorResponse) => {
        console.error("Erreur lors de la suppression du rôle", error.message);
      }
    );
  }

  openFunctionalityModal(role: Role): void {
    this.selectedRole = { ...role };
    this.showFunctionalityModal = true;
  }

  onFunctionalityChange(event: Event, functionality: Functionality): void {
    const isChecked = (event.target as HTMLInputElement).checked;
    this.toggleFunctionalitySelection(functionality, isChecked);
  }

  toggleFunctionalitySelection(functionality: Functionality, isChecked: boolean): void {
    if (!this.selectedRole) return;

    if (!this.selectedRole.functionalities) {
      this.selectedRole.functionalities = [];
    }

    if (isChecked) {
      if (!this.selectedRole.functionalities.some(f => f.id === functionality.id)) {
        this.selectedRole.functionalities.push(functionality);
      }
    } else {
      this.selectedRole.functionalities = this.selectedRole.functionalities.filter(f => f.id !== functionality.id);
    }
  }

  hasFunctionality(functionality: Functionality): boolean {
    return this.selectedRole?.functionalities?.some(f => f.id === functionality.id) ?? false;
  }

  assignFunctionalities(): void {
    if (!this.selectedRole) return;

    const functionalityIds = (this.selectedRole.functionalities ?? []).map(f => f.id);

    this.roleService.assignFunctionalities(this.selectedRole.id, functionalityIds).subscribe(
      () => {
        Swal.fire('Succès', 'Les fonctionnalités ont été assignées', 'success');
        this.showFunctionalityModal = false;
      },
      (error: HttpErrorResponse) => {
        console.error("Erreur lors de l'assignation des fonctionnalités", error.message);
      }
    );
  }
}

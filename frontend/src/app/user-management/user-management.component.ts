import { Component, OnInit } from "@angular/core";
import { MyHttpService } from "../my-http.service";
import { Router } from "@angular/router";
import { HttpErrorResponse } from "@angular/common/http";
import Swal from "sweetalert2";

export interface Role {
  id: number;
  name: string;
}

export interface User {
  id: number;
  username: string;
  email: string;
  roles: Role[];
  active: boolean;
}

@Component({
  selector: "app-user-management",
  templateUrl: "./user-management.component.html",
  styleUrls: ["./user-management.component.css"],
})
export class UserManagementComponent implements OnInit {
  users: User[] = [];
  roles: Role[] = [];
  isLoading: boolean = true;
  isAdmin: boolean = false;
  selectedUser: User | null = null;
  showRoleModal: boolean = false;

  constructor(
    private myHttpService: MyHttpService, 
    private router: Router // ✅ Injection correcte
  ) {}

  

  ngOnInit(): void {
    const roles = JSON.parse(localStorage.getItem("user_roles") || "[]");
    this.isAdmin = roles.includes("ROLE_ADMIN");

    if (!this.isAdmin) {
      this.router.navigate(["/unauthorized"]);
    } else {
      this.fetchUsers();
      this.fetchRoles();
    }
  }

  fetchUsers(): void {
    this.isLoading = true;
    this.myHttpService.getAllUsers().subscribe(
      (data: User[]) => {
        this.users = data.map(user => ({
          ...user,
          roles: user.roles ?? [] // S'assurer que roles n'est jamais undefined
        }));
        this.isLoading = false;
      },
      (error: HttpErrorResponse) => {
        console.error("Erreur lors de la récupération des utilisateurs", error.message);
        this.isLoading = false;
      }
    );
  }

  fetchRoles(): void {
    this.myHttpService.getAllRoles().subscribe(
      (data: Role[]) => {
        this.roles = data;
      },
      (error: HttpErrorResponse) => {
        console.error("Erreur lors de la récupération des rôles", error.message);
      }
    );
  }

  toggleUserStatus(user: User): void {
    const action = user.active ? "désactivé" : "activé";
    const request = user.active 
      ? this.myHttpService.deactivateUser(user.id) 
      : this.myHttpService.activateUser(user.id);
  
    console.log(`Envoi de la requête pour ${action} l'utilisateur ${user.id}`);
  
    request.subscribe(
      (response) => {
        console.log("Réponse reçue du backend :", response);
        user.active = !user.active;
        Swal.fire("Succès", `Utilisateur ${action} avec succès`, "success");
      },
      (error: HttpErrorResponse) => {
        console.error(`Erreur lors de la modification du statut`, error);
      }
    );
  }
  

  openRoleModal(user: User): void {
    this.selectedUser = {
      ...user,
      roles: user.roles ? [...user.roles] : [] // S'assurer que roles n'est pas undefined
    };
    this.showRoleModal = true;
  }

  updateUserRole(): void {
    if (!this.selectedUser) return;

    const newRoles = this.selectedUser.roles.map((r) => r.name);
    this.myHttpService.updateUserRoles(this.selectedUser.id, newRoles).subscribe(
      () => {
        this.fetchUsers(); // Recharger la liste des utilisateurs après modification
        Swal.fire("Succès", "Le rôle de l'utilisateur a été mis à jour", "success");
        this.showRoleModal = false;
      },
      (error: HttpErrorResponse) => {
        console.error("Erreur lors de la mise à jour du rôle", error.message);
      }
    );
  }

  toggleRoleSelection(role: Role, isChecked: boolean): void {
    if (this.selectedUser) {
      if (!this.selectedUser.roles) {
        this.selectedUser.roles = [];
      }
      
      if (isChecked) {
        if (!this.selectedUser.roles.some(r => r.id === role.id)) {
          this.selectedUser.roles.push(role);
        }
      } else {
        this.selectedUser.roles = this.selectedUser.roles.filter(r => r.id !== role.id);
      }
    }
  }

  // ✅ Nouvelle fonction pour vérifier si un utilisateur a un rôle donné
  hasRole(user: User | null, roleId: number): boolean {
    return user?.roles?.some(r => r.id === roleId) ?? false;
  }

  onRoleChange(event: Event, role: any): void {
    const isChecked = (event.target as HTMLInputElement).checked;
    this.toggleRoleSelection(role, isChecked);
  }
  
}

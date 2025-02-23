import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

// Modèle du Token
import { Token } from './token';

@Injectable({
  providedIn: 'root',
})
export class MyHttpService {
  private baseUrl: string = 'http://localhost:8080/api'; // URL de base de l'API

  constructor(private http: HttpClient) {}

  post(url: string, data: any): Observable<any> {
    return this.http.post(`${this.baseUrl}${url}`, data, {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
    });
  }

  // ✅ Ajouter la méthode GET
  get(url: string): Observable<any> {
    return this.http.get(`${this.baseUrl}${url}`);
  }

  // ✅ Récupérer tous les utilisateurs
  getAllUsers(): Observable<any> {
    console.log(`🔹 GET request: ${this.baseUrl}/auth/users`);
    return this.http.get(`${this.baseUrl}/auth/users`);
  }

  // ✅ Activer un utilisateur
  activateUser(id: number): Observable<any> {
    console.log(`🔹 PUT request: ${this.baseUrl}/auth/users/${id}/activate`);
    return this.http.put(`${this.baseUrl}/auth/users/${id}/activate`, {});
  }

deactivateUser(id: number): Observable<any> {
  return this.http.put(
    `${this.baseUrl}/auth/users/${id}/deactivate`, 
    {}, 
    { headers: new HttpHeaders({ "Content-Type": "application/json" }) }
  );
}

  
updateUserRoles(id: number, roles: string[]): Observable<any> {
  console.log(`🔹 PUT request vers: ${this.baseUrl}/auth/users/${id}/roles`, { role: roles });

  return this.http.put(`${this.baseUrl}/auth/users/${id}/roles`, 
    { role: roles }, 
    { headers: new HttpHeaders({ "Content-Type": "application/json" }) }
  );
}



  // ✅ Supprimer un utilisateur
  deleteUser(id: number): Observable<any> {
    console.log(`🔹 DELETE request: ${this.baseUrl}/auth/users/${id}`);
    return this.http.delete(`${this.baseUrl}/auth/users/${id}`);
  }

  // ✅ Récupérer tous les rôles
  getAllRoles(): Observable<any> {
    console.log(`🔹 GET request: ${this.baseUrl}/roles`);
    return this.http.get(`${this.baseUrl}/roles`);
  }

  // ✅ Ajouter un rôle
  addRole(role: any): Observable<any> {
    console.log(`🔹 POST request: ${this.baseUrl}/roles`, role);
    return this.http.post(`${this.baseUrl}/roles`, role);
  }

  // ✅ Mettre à jour un rôle
  updateRole(id: number, role: any): Observable<any> {
    console.log(`🔹 PUT request: ${this.baseUrl}/roles/${id}`, role);
    return this.http.put(`${this.baseUrl}/roles/${id}`, role);
  }

  // ✅ Supprimer un rôle
  deleteRole(id: number): Observable<any> {
    console.log(`🔹 DELETE request: ${this.baseUrl}/roles/${id}`);
    return this.http.delete(`${this.baseUrl}/roles/${id}`);
  }

  // ✅ Rechercher un rôle par mot-clé
  searchRoles(keyword: string): Observable<any> {
    console.log(
      `🔹 GET request: ${this.baseUrl}/roles/search?keyword=${keyword}`
    );
    return this.http.get(`${this.baseUrl}/roles/search?keyword=${keyword}`);
  }

  // Récupérer le token après authentification (si besoin pour d'autres services)
  getToken(code: string): Observable<boolean> {
    return this.http
      .get<Token>(`http://localhost:8080/api/auth/callback?code=${code}`, {
        observe: 'response',
      })
      .pipe(
        map((response: HttpResponse<Token>) => {
          console.log('🔹 Token response:', response);
          if (response.status === 200 && response.body !== null) {
            localStorage.setItem('auth_token', response.body.token);
            return true;
          } else {
            console.log('🔸 Erreur: Token non reçu.');
            return false;
          }
        })
      );
  }


  // ✅ Vérifier le statut d'un utilisateur (connecté / non connecté)
  getUserInfo(): Observable<any> {
    console.log(`🔹 GET request: ${this.baseUrl}/auth/userinfo`);
    return this.http.get(`${this.baseUrl}/auth/userinfo`);
  }


 

  
  // ✅ Récupérer le token stocké (au cas où)
  getStoredToken(): string | null {
    return localStorage.getItem('auth_token');
  }

  // ✅ Déconnexion (supprime le token du localStorage)
  logout(): void {
    console.log("🔹 Déconnexion de l'utilisateur");
    localStorage.removeItem('auth_token');
  }
}

// export class MyHttpService {
//   constructor(private http: HttpClient) {}

//   // Méthode GET sans authentification
//   get(url: string): Observable<any> {
//     console.log(`🔹 GET request: http://localhost:8080${url}`);
//     return this.http.get(`http://localhost:8080${url}`);
//   }

//   // Méthode GET (utilisée pour les routes nécessitant des permissions, mais SANS token)
//   getPrivate(url: string): Observable<any> {
//     console.log(
//       `🔹 GET request (private, sans token): http://localhost:8080${url}`
//     );
//     return this.http.get(`http://localhost:8080${url}`, {
//       headers: new HttpHeaders({
//         'Content-Type': 'application/json',
//       }),
//     });
//   }

//   // Méthode POST sans authentification
//   post(url: string, data: any): Observable<any> {
//     console.log(`🔹 POST request: http://localhost:8080${url}`, data);
//     return this.http.post(`http://localhost:8080${url}`, data);
//   }

//   // Méthode POST avec authentification (si besoin)
//   postPrivate(url: string, data: any): Observable<any> {
//     console.log(
//       `🔹 POST request (private, sans token): http://localhost:8080${url}`,
//       data
//     );
//     return this.http.post(`http://localhost:8080${url}`, data, {
//       headers: new HttpHeaders({
//         'Content-Type': 'application/json',
//       }),
//     });
//   }

//   // Récupérer le token après authentification (si besoin pour d'autres services)
//   getToken(code: string): Observable<boolean> {
//     return this.http
//       .get<Token>(`http://localhost:8080/api/auth/callback?code=${code}`, {
//         observe: 'response',
//       })
//       .pipe(
//         map((response: HttpResponse<Token>) => {
//           console.log('🔹 Token response:', response);
//           if (response.status === 200 && response.body !== null) {
//             localStorage.setItem('auth_token', response.body.token);
//             return true;
//           } else {
//             console.log('🔸 Erreur: Token non reçu.');
//             return false;
//           }
//         })
//       );
//   }

//   // Récupérer le token stocké (au cas où)
//   getStoredToken(): string | null {
//     return localStorage.getItem('auth_token');
//   }
// }

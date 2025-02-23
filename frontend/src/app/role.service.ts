import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Role {
  id: number;
  name: string;
  description?: string;
  functionalities?: Functionality[];
}



export interface Functionality {
  id: number;
  name: string;
}

@Injectable({
  providedIn: 'root'
})
export class RoleService {
  private baseUrl = 'http://localhost:8080/api/roles';
  private functionalitiesUrl = 'http://localhost:8080/api/functionalities';

  constructor(private http: HttpClient) {}

  getAllRoles(): Observable<Role[]> {
    return this.http.get<Role[]>(`${this.baseUrl}`);
  }

  getAllFunctionalities(): Observable<Functionality[]> {
    return this.http.get<Functionality[]>(`${this.functionalitiesUrl}`);
  }

  addRole(role: Role): Observable<Role> {
    return this.http.post<Role>(`${this.baseUrl}`, role);
  }

  updateRole(id: number, role: Role): Observable<Role> {
    return this.http.put<Role>(`${this.baseUrl}/${id}`, role);
  }

  deleteRole(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  assignFunctionalities(roleId: number, functionalityIds: number[]): Observable<Role> {
    return this.http.post<Role>(`${this.baseUrl}/${roleId}/assign-functionalities`, functionalityIds);
  }
}

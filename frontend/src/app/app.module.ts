import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms'; // ✅ Ajouter FormsModule ici
import { HttpClientModule } from '@angular/common/http';
import { RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';
import { LoginFormComponent } from './login-form/login-form.component';
import { UserManagementComponent } from './user-management/user-management.component';
import { RoleManagementComponent } from './role-management/role-management.component'; // ✅ Assure-toi que ce composant est bien importé

const routes: Routes = [
  { path: '', component: LoginFormComponent },
  { path: 'admin', component: UserManagementComponent },
  { path: 'roles', component: RoleManagementComponent }, // ✅ Ajouter la route pour le rôle
  { path: '**', redirectTo: '' }
];

@NgModule({
  declarations: [
    AppComponent,
    LoginFormComponent,
    UserManagementComponent,
    RoleManagementComponent
  ],
  imports: [
    BrowserModule,
    FormsModule, // ✅ Import nécessaire pour [(ngModel)]
    ReactiveFormsModule,
    HttpClientModule,
    RouterModule.forRoot(routes, { useHash: true })
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }

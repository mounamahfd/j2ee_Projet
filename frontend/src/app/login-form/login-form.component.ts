import { Component } from '@angular/core';
import { MyHttpService } from '../my-http.service';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router'; // <-- Importer Router

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.css']
})
export class LoginFormComponent {
  url: string | null = null;
  errorMessage: string | null = null;
  jwtErrorMessage: string | null = null;
  
  loginRequest = {
    username: '',
    password: ''
  };

  constructor(private http: MyHttpService, private router: Router) {} // <-- Injecter Router

  ngOnInit(): void {
    this.http.get("/auth/url").subscribe(
      (data: any) => {
        if (data?.authURL) {
          this.url = data.authURL;
        } else {
          this.errorMessage = "Unable to retrieve login URL.";
        }
      },
      (error: HttpErrorResponse) => {
        this.errorMessage = "An error occurred while fetching the login URL.";
        console.error("Error fetching login URL:", error);
      }
    );
  }

  onSubmit(): void {
    this.jwtErrorMessage = null; // Réinitialiser les erreurs
  
    this.http.post("/auth/signin", this.loginRequest).subscribe(
      (data: any) => {
        console.log("🟢 Login successful:", data);
  
        // Stocker les informations utilisateur
        localStorage.setItem("auth_token", data.accessToken);
        localStorage.setItem("user_roles", JSON.stringify(data.roles));
  
        console.log("🔹 Rôles stockés :", localStorage.getItem("user_roles"));
  
        // Vérifier si l'utilisateur est admin avant de rediriger
        if (data.roles.includes('ROLE_ADMIN')) {
          console.log("🟢 Redirection vers /admin...");
          this.router.navigate(['/admin']);
        } else {
          console.log("🟡 Utilisateur non admin, pas de redirection.");
        }
      },
      (error: HttpErrorResponse) => {
        this.jwtErrorMessage = "Invalid username or password.";
        console.error("❌ Login error:", error);
      }
    );
  }
  
}

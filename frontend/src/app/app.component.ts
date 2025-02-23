import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MyHttpService } from './my-http.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  isAuthenticated: boolean = false;
  isAdmin: boolean = false;

  constructor(private http: MyHttpService, private route: ActivatedRoute) {}

  ngOnInit(): void {
    const token = localStorage.getItem('auth_token');
    this.isAuthenticated = !!token;
  
    if (this.isAuthenticated) {
      const roles = JSON.parse(localStorage.getItem('user_roles') || '[]');
      console.log("🔹 Rôles récupérés :", roles);
  
      this.isAdmin = roles.includes('ROLE_ADMIN');
    }
  }
  
  
  
}

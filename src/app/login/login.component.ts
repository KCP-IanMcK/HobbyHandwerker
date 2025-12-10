import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  standalone: true,
  imports: [
    FormsModule,
    CommonModule,
    HttpClientModule
  ],
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  // Events to communicate with the Home Component
  @Output() loginSuccess = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();

  username: string = '';
  password: string = '';
  errorMessage: string = '';

  constructor(private http: HttpClient) {}

  onSubmit() {
    const loginData = {
      username: this.username,
      password: this.password
    };

    // Make sure your Backend is running and has the /login endpoint!
    this.http.post<any>('http://localhost:8080/user/login', loginData).subscribe({
      next: (user) => {
        console.log('Login successful', user);
        localStorage.setItem('loggedInUserId', user.id_user);

        // Notify the parent (Home) that we are finished
        this.loginSuccess.emit();
      },
      error: (error) => {
        console.error('Login failed', error);
        this.errorMessage = 'Benutzername oder Passwort falsch.';
      }
    });
  }

  onCancel() {
    this.cancel.emit();
  }
}

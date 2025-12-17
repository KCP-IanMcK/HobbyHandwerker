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

 async onSubmit() {
    try {
        const hashedPassword = await this.hashPassword(this.password);

        const loginData = {
          username: this.username,
          password: hashedPassword
        };

    this.http.put<any>('http://localhost:8080/user/login', loginData).subscribe({
      next: (response) => {
        const token = response.token;
        const user = response.user;

        // Token speichern (für spätere Requests)
        localStorage.setItem('jwtToken', token);

        // Benutzer ID speichern
        localStorage.setItem('loggedInUserId', user.id_user.toString());

        // Role speichern (optional)
        localStorage.setItem('role', user.role.toString());

        // Parent informieren
        this.loginSuccess.emit();
      },
      error: (error) => {
        console.error('Login failed', error);
        this.errorMessage = 'Benutzername oder Passwort falsch.';
      }
    });
  } catch (err) {
      console.error('Fehler beim Hashen des Passworts', err);
    }
  }

async hashPassword(password: string): Promise<string> {
  const encoder = new TextEncoder();
  const data = encoder.encode(password);
  const hashBuffer = await crypto.subtle.digest('SHA-256', data);
  const hashArray = Array.from(new Uint8Array(hashBuffer));
  const hashHex = hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
  return hashHex;
}

  onCancel() {
    this.cancel.emit();
  }
}

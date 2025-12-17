import { Component, EventEmitter, Output } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-create-profile',
  imports: [FormsModule, CommonModule],
  templateUrl: './create-profile.component.html',
  styleUrls: ['./create-profile.component.css']
})
export class CreateProfileComponent {
  @Output() close = new EventEmitter<boolean>();

  profile = {
    username: '',
    email: '',
    password: '',
    bio: '',
    avatarUrl: ''
  };

  message: string | null = null;

  apiUrl = environment.apiUrl + 'user';

  constructor(private http: HttpClient) {}

  async submitForm(): Promise<void> {
    if (!this.profile.username || !this.profile.email || !this.profile.password) {
      return;
    }

    this.profile.password = await this.hashPassword(this.profile.password);

    this.http.post(this.apiUrl + 'user', this.profile).subscribe({
      next: (res) => {
        this.message = 'Profil erfolgreich erstellt!';
        this.resetForm();
        this.closePopup();
      },
      error: (err) => {
        console.error('Fehler beim Erstellen des Profils:', err);
        this.message = 'Fehler beim Erstellen des Profils. Bitte versuche es später erneut.';
      }
    });
  }

  resetForm(): void {
    this.profile = {
      username: '',
      email: '',
      password: '',
      bio: '',
      avatarUrl: ''
    };
  }

  closePopup(): void {
    this.close.emit(false);
  }

async hashPassword(password: string): Promise<string> {
  const encoder = new TextEncoder();
  const data = encoder.encode(password);
  const hashBuffer = await crypto.subtle.digest('SHA-256', data);
  const hashArray = Array.from(new Uint8Array(hashBuffer));
  const hashHex = hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
  return hashHex;
}
}

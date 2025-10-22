import { Component, EventEmitter, Output } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-create-profile',
  imports: [FormsModule, CommonModule],
  templateUrl: './create-profile.component.html',
  styleUrls: ['./create-profile.component.css']
})
export class CreateProfileComponent {
  @Output() close = new EventEmitter<boolean>();  // <-- EventEmitter definieren

  profile = {
    name: '',
    email: '',
    bio: '',
    avatarUrl: ''
  };

  message: string | null = null;

  apiUrl = 'http://localhost:8080/user';

  constructor(private http: HttpClient) {}

  submitForm(): void {
    if (!this.profile.name || !this.profile.email) {
      return;
    }

    this.http.post(this.apiUrl, this.profile).subscribe({
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
      name: '',
      email: '',
      bio: '',
      avatarUrl: ''
    };
  }

  closePopup(): void {
    this.close.emit(false);
  }
}

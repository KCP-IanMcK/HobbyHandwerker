import { Component, EventEmitter, Output } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-create-profile',
  standalone: true,
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

  // Variables for Password Strength
  strengthScore: number = 0;
  strengthText: string = '';
  strengthColor: string = '#e74c3c'; // Default Red

  // Flags for specific requirements
  hasMinLength: boolean = false;
  hasUpperCase: boolean = false;
  hasLowerCase: boolean = false;
  hasNumber: boolean = false;
  hasSpecialChar: boolean = false;

  apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  checkPasswordStrength(): void {
    const pw = this.profile.password || '';

    // Check individual requirements
    this.hasMinLength = pw.length >= 12;
    this.hasUpperCase = /[A-Z]/.test(pw);
    this.hasLowerCase = /[a-z]/.test(pw);
    this.hasNumber = /\d/.test(pw);
    this.hasSpecialChar = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(pw);

    // Calculate Score (0 to 4)
    let score = 0;
    if (this.hasMinLength) score++;
    if (this.hasUpperCase && this.hasLowerCase) score++;
    if (this.hasNumber) score++;
    if (this.hasSpecialChar) score++;

    this.strengthScore = score;

    // Update UI Text and Color
    switch (score) {
      case 0:
      case 1:
        this.strengthText = 'Schwach';
        this.strengthColor = '#e74c3c'; // Red
        break;
      case 2:
        this.strengthText = 'Mittel';
        this.strengthColor = '#f39c12'; // Orange
        break;
      case 3:
        this.strengthText = 'Gut';
        this.strengthColor = '#3498db'; // Blue
        break;
      case 4:
        this.strengthText = 'Sehr stark';
        this.strengthColor = '#27ae60'; // Green
        break;
    }
  }

  submitForm(): void {
    if (!this.profile.username || !this.profile.email || !this.profile.password) {
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
      username: '',
      email: '',
      password: '',
      bio: '',
      avatarUrl: ''
    };
    // Reset Strength UI
    this.strengthScore = 0;
    this.strengthText = '';
    this.strengthColor = '#e74c3c';
  }

  closePopup(): void {
    this.close.emit(false);
  }
}

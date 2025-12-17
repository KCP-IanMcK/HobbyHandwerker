import { Component, EventEmitter, Output, ViewChild } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule, NgForm } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-create-profile',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './create-profile.component.html',
  styleUrls: ['./create-profile.component.css']
})
export class CreateProfileComponent {
  @Output() close = new EventEmitter<boolean>();

  @ViewChild('profileForm') profileForm!: NgForm;

  profile = {
    username: '',
    email: '',
    password: '',
    bio: '',
    avatarUrl: ''
  };

  // Visibility flag for strength meter
  showPasswordMeter: boolean = false;

  strengthScore: number = 0;
  strengthText: string = '';
  strengthColor: string = '#e74c3c';
  hasMinLength: boolean = false;
  hasUpperCase: boolean = false;
  hasLowerCase: boolean = false;
  hasNumber: boolean = false;
  hasSpecialChar: boolean = false;

  message: string | null = null;
  apiUrl = 'http://localhost:8080/user';

  constructor(private http: HttpClient) {}

  checkPasswordStrength(): void {
    const pw = this.profile.password || '';
    this.hasMinLength = pw.length >= 8;
    this.hasUpperCase = /[A-Z]/.test(pw);
    this.hasLowerCase = /[a-z]/.test(pw);
    this.hasNumber = /\d/.test(pw);
    this.hasSpecialChar = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(pw);

    let score = 0;
    if (this.hasMinLength) score++;
    if (this.hasUpperCase && this.hasLowerCase) score++;
    if (this.hasNumber) score++;
    if (this.hasSpecialChar) score++;

    this.strengthScore = score;
    switch (score) {
      case 0: case 1: this.strengthText = 'Schwach'; this.strengthColor = '#e74c3c'; break;
      case 2: this.strengthText = 'Mittel'; this.strengthColor = '#f39c12'; break;
      case 3: this.strengthText = 'Gut'; this.strengthColor = '#3498db'; break;
      case 4: this.strengthText = 'Sehr stark'; this.strengthColor = '#27ae60'; break;
    }
  }

  // Changed to async to handle hashing
  async submitForm(): Promise<void> {
    if (!this.profile.username || !this.profile.email || !this.profile.password) {
      this.message = "Bitte alle Pflichtfelder ausfüllen.";
      return;
    }

    if (this.strengthScore < 3) {
      this.message = "Passwort ist nicht stark genug.";
      return;
    }

    const createdName = this.profile.username;

    // RESTORED: Client-side hashing
    // We hash the password before sending it to the backend
    try {
      this.profile.password = await this.hashPassword(this.profile.password);
    } catch (e) {
      console.error("Hashing failed", e);
      this.message = "Interner Fehler bei der Passwortverarbeitung.";
      return;
    }

    this.http.post(this.apiUrl, this.profile).subscribe({
      next: (res) => {
        this.resetForm();
        this.message = `User "${createdName}" was successfully created!`;
      },
      error: (err) => {
        console.error('Fehler:', err);
        this.message = 'Fehler beim Erstellen des Profils.';
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
    this.hasMinLength = false;
    this.hasUpperCase = false;
    this.hasLowerCase = false;
    this.hasNumber = false;
    this.hasSpecialChar = false;

    // Hide meter
    this.showPasswordMeter = false;

    // Reset Form Validation State
    if (this.profileForm) {
      this.profileForm.resetForm();
    }
  }

  closePopup(): void {
    this.close.emit(false);
  }

  // RESTORED: Helper function to hash password with SHA-256
  async hashPassword(password: string): Promise<string> {
    const encoder = new TextEncoder();
    const data = encoder.encode(password);
    const hashBuffer = await crypto.subtle.digest('SHA-256', data);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    const hashHex = hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
    return hashHex;
  }
}

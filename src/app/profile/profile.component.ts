import { Component, OnInit } from '@angular/core';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CreateProfileComponent } from '../create-profile/create-profile.component';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, CreateProfileComponent, HttpClientModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  user = {
    id: 1,
    name: '',
    email: '',
    bio: '',
    avatarUrl: 'https://via.placeholder.com/150'
  };

  showCreateProfile: boolean = false;

  editing: boolean = false;
  apiUrl = 'http://localhost:8080/user';

  // Für Fehlermeldungen
  errorMessage: string | null = null;
  saving: boolean = false;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadUser();
  }

  loadUser(): void {
    this.errorMessage = null; // Fehler vorher löschen
    this.http.get<any>(`${this.apiUrl}/${this.user.id}`).subscribe({
      next: (data) => {
        this.user.name = data.name;
        this.user.email = data.email;
        this.user.bio = data.bio;
        this.user.avatarUrl = data.avatarUrl || this.user.avatarUrl;
      },
      error: (err) => {
        console.error('Fehler beim Laden des Users:', err);
        this.errorMessage = 'Das Profil konnte nicht geladen werden. Bitte versuche es später erneut.';
        // UI bleibt nutzbar, App läuft weiter
      }
    });
  }

  toggleEdit(): void {
    this.errorMessage = null; // Fehler beim Starten der Bearbeitung löschen
    this.editing = !this.editing;
  }

  saveProfile(): void {
    this.errorMessage = null;
    this.saving = true;
    this.http.put(`${this.apiUrl}/${this.user.id}`, this.user).subscribe({
      next: () => {
        console.log('Profil gespeichert:', this.user);
        this.editing = false;
        this.saving = false;
      },
      error: (err) => {
        console.error('Fehler beim Speichern:', err);
        this.errorMessage = 'Fehler beim Speichern des Profils. Bitte versuche es später erneut.';
        this.saving = false;
      }
    });
  }

 openCreateProfile(): void {
    this.showCreateProfile = true;
  }

  closeCreateProfile(): void {
    this.showCreateProfile = false;
  }
}


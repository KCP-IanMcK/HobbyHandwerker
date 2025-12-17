import { Component, OnInit } from '@angular/core';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  user = {
    id_user: 1,
    username: '',
    email: '',
    avatarUrl: 'https://via.placeholder.com/150'
  };

  showCreateProfile: boolean = false;
  userBeforeEdit: any = null;

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
    const userId = localStorage.getItem("loggedInUserId") ?? "1";
    const token = localStorage.getItem("jwtToken");

    const options = token
      ? { headers: { Authorization: "Bearer " + token } }
      : {};

    this.errorMessage = null;

    this.http.get<any>(`${this.apiUrl}/${userId}`, options).subscribe({
      next: (data) => {
        this.user.id_user = data.id_user;
        this.user.username = data.username;
        this.user.email = data.email;
        this.user.avatarUrl = data.avatarUrl || this.user.avatarUrl;
      },
      error: (err) => {
        console.error("Fehler beim Laden des Users:", err);
        this.errorMessage = "Das Profil konnte nicht geladen werden. Bitte versuche es später erneut.";
      }
    });
  }

  openEdit(): void {
    this.errorMessage = null; // Fehler beim Starten der Bearbeitung löschen
    this.userBeforeEdit = { ...this.user };
    this.editing = true;
  }

  closeEdit(): void {
    this.user = this.userBeforeEdit;
    this.editing = false;
   }

  saveProfile(): void {
    console.log(this.user);
    const userId = localStorage.getItem("loggedInUserId") ?? "1";
        const token = localStorage.getItem("jwtToken");
        const options = { headers: { Authorization: "Bearer " + token } }

    this.errorMessage = null;
    this.saving = true;
    this.http.put(`${this.apiUrl}/${this.user.id_user}`, this.user, options).subscribe({
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
}


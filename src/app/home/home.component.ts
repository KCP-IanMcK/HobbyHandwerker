import { Component, OnInit } from '@angular/core';
import { ProfileComponent } from '../profile/profile.component';
import { CommonModule } from '@angular/common';
import { ExplorerComponent } from '../explorer/explorer.component';
import { CreateProfileComponent } from '../create-profile/create-profile.component';
import { LoginComponent } from '../login/login.component'; // Import Login

@Component({
  selector: 'app-home',
  standalone: true,
  // Make sure LoginComponent is in the imports list
  imports: [ProfileComponent, CommonModule, ExplorerComponent, LoginComponent, CreateProfileComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  title = 'Startseite';
  showProfile = false;
  userId: String | null = null;
  showCreateProfile: boolean = false;
  isLoggedIn: boolean = false;

  // New State: Are we looking at the login screen?
  isLoginView = false;

  constructor() {}

  ngOnInit(): void {
    this.userId = localStorage.getItem("loggedInUserId")
  }

  toggleProfile(): void {
    this.showProfile = !this.showProfile;
  }

  openLogin(): void {
    this.isLoginView = true;
  }

  handleLoginSuccess(): void {
    this.isLoginView = false;
    this.isLoggedIn = true;
    this.showProfile = true; // Automatically open profile after login
  }

  handleLoginCancel(): void {
    this.isLoginView = false;
  }

  openCreateProfile(): void {
    this.showCreateProfile = true;
  }

  closeCreateProfile(): void {
    this.showCreateProfile = false;
  }
}

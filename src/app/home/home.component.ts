import { Component, OnInit } from '@angular/core';
import { ProfileComponent } from '../profile/profile.component';
import { CommonModule } from '@angular/common';
import { ExplorerComponent } from '../explorer/explorer.component';
import { LoginComponent } from '../login/login.component'; // Import Login

@Component({
  selector: 'app-home',
  standalone: true,
  // Make sure LoginComponent is in the imports list
  imports: [ProfileComponent, CommonModule, ExplorerComponent, LoginComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  title = 'Startseite';
  showProfile = false;

  // New State: Are we looking at the login screen?
  isLoginView = false;

  constructor() {}

  ngOnInit(): void {
    console.log('HomeComponent geladen');
  }

  toggleProfile(): void {
    this.showProfile = !this.showProfile;
  }

  openLogin(): void {
    this.isLoginView = true;
  }

  handleLoginSuccess(): void {
    this.isLoginView = false;
    this.showProfile = true; // Automatically open profile after login
  }

  handleLoginCancel(): void {
    this.isLoginView = false;
  }
}

import { Component, OnInit } from '@angular/core';
import { ProfileComponent } from '../profile/profile.component';
import { CommonModule } from '@angular/common';
import { ExplorerComponent } from '../explorer/explorer.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [ProfileComponent, CommonModule, ExplorerComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  title = 'Startseite';
  showProfile = false; // Profil zunächst versteckt

  constructor() {}

  ngOnInit(): void {
    console.log('HomeComponent geladen');
  }

  toggleProfile(): void {
    this.showProfile = !this.showProfile;
  }
}

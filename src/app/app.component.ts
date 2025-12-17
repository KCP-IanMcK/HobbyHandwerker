import { Component, OnInit } from '@angular/core';
import { HomeComponent } from './home/home.component';
import { environment } from '../environments/environment';

@Component({
  selector: 'app-root',
  imports: [HomeComponent],
  templateUrl: './app.component.html',
  standalone: true,
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'HobbyHandwerker';
  apiUrl = environment.apiUrl + 'user/checkJWT';

  async ngOnInit(): Promise<void> {
    const token = localStorage.getItem('jwtToken');

   if (!token) {
     return;
   }

    const stillValid = await this.checkJWT(token);

    if (!stillValid) {
      localStorage.clear();
      window.location.reload();
    }
  }

  async checkJWT(token: string): Promise<boolean> {
    try {
      const response = await fetch(this.apiUrl, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        console.error('Token ungültig oder Fehler beim Server');
        return false;
      }

      const isValid: boolean = await response.json();
      return isValid;

    } catch (error) {
      console.error('Fehler beim Aufruf von /checkJWT:', error);
      return false;
    }
  }
}

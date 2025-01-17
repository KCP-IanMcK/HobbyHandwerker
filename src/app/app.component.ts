import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ProfileComponent} from './profile/profile.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ProfileComponent],
  templateUrl: './app.component.html',
  standalone: true,
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'HobbyHandwerker';
}

import { Component, Input } from '@angular/core';
import {ToolDto} from '../dtos/ToolDto';

@Component({
  selector: 'app-werkzeug-detail',
  templateUrl: './werkzeug-detail.component.html',
  styleUrls: ['./werkzeug-detail.component.css']
})
export class WerkzeugDetailComponent {
  @Input() werkzeug: ToolDto | null = null;

  ngOnInit() {
    // Beispielwert – nur zu Testzwecken
    if (!this.werkzeug) {
      this.werkzeug = {
        name: 'Akkuschrauber Bosch GSR 12V',
        description: 'Kompakter Akkuschrauber mit 2-Gang-Getriebe und LED-Licht.',
        status: 'in_benutzung'
      };
    }
  }


// Kleiner Hilfsbutton: markiert als geprüft
  markiereGeprueft() {
    if (!this.werkzeug) return;
    this.werkzeug.status = 'verfügbar';
  }
}

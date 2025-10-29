import { Component, Input } from '@angular/core';


export interface Werkzeug {
  id: number;
  name: string;
  beschreibung?: string;
  status?: 'verfügbar' | 'in_benutzung' | 'wartung' | string;
}


@Component({
  selector: 'app-werkzeug-detail',
  templateUrl: './werkzeug-detail.component.html',
  styleUrls: ['./werkzeug-detail.component.css']
})
export class WerkzeugDetailComponent {
  @Input() werkzeug: Werkzeug | null = null;


// Kleiner Hilfsbutton: markiert als geprüft
  markiereGeprueft() {
    if (!this.werkzeug) return;
    this.werkzeug.status = 'verfügbar';
  }
}

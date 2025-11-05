import { Component, Input, Output, EventEmitter } from '@angular/core';
import {ToolDto} from '../dtos/ToolDto';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-tool-detail',
  imports: [CommonModule],
  templateUrl: './tool-detail.component.html',
  styleUrls: ['./tool-detail.component.css']
})
export class ToolDetailComponent {
  @Input() tool: ToolDto | undefined;
  @Output() closePopUpEmitter: EventEmitter<void> = new EventEmitter<void>();

  ngOnInit() {
    // Beispielwert – nur zu Testzwecken
    if (this.tool == null) {
      this.tool = {
        name: 'Akkuschrauber Bosch GSR 12V',
        description: 'Kompakter Akkuschrauber mit 2-Gang-Getriebe und LED-Licht.',
        status: 'in_benutzung'
      };
    }
  }


  highlightValidated(): void {
    if (!this.tool) return;
    this.tool.status = 'verfügbar';
  }

  closePopUp(): void {
    this.closePopUpEmitter.emit();
  }
}

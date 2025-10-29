import { ComponentFixture, TestBed } from '@angular/core/testing';
import { WerkzeugDetailComponent } from './werkzeug-detail.component';
import { ToolDto } from '../dtos/ToolDto';

describe('WerkzeugDetailComponent', () => {
  let component: WerkzeugDetailComponent;
  let fixture: ComponentFixture<WerkzeugDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WerkzeugDetailComponent], // <— wichtig: standalone-Komponente importieren!
    }).compileComponents();

    fixture = TestBed.createComponent(WerkzeugDetailComponent);
    component = fixture.componentInstance;
  });

  it('sollte erstellt werden', () => {
    expect(component).toBeTruthy();
  });

  it('sollte Beispielwert setzen, wenn kein werkzeug übergeben wurde', () => {
    component.werkzeug = undefined;
    component.ngOnInit();
    expect(component.werkzeug).toBeTruthy();
    expect(component.werkzeug!.name).toBe('Akkuschrauber Bosch GSR 12V');
    expect(component.werkzeug!.status).toBe('in_benutzung');
  });

  it('sollte vorhandenes werkzeug beibehalten, wenn gesetzt', () => {
    const customWerkzeug: ToolDto = {
      name: 'Bohrhammer Makita',
      description: 'Leistungsstarker Bohrhammer für Betonarbeiten',
      status: 'defekt',
    };

    component.werkzeug = customWerkzeug;
    component.ngOnInit();

    expect(component.werkzeug).toBe(customWerkzeug);
    expect(component.werkzeug!.name).toBe('Bohrhammer Makita');
  });

  it('highlightValidated() sollte Status auf "verfügbar" setzen', () => {
    const tool: ToolDto = {
      name: 'Stichsäge Bosch',
      description: 'Für präzise Holzschnitte.',
      status: 'in_benutzung',
    };

    component.werkzeug = tool;
    component.highlightValidated();

    expect(component.werkzeug!.status).toBe('verfügbar');
  });

  it('highlightValidated() sollte nichts tun, wenn werkzeug null ist', () => {
    component.werkzeug = undefined;
    component.highlightValidated();
    expect(component.werkzeug).toBeUndefined();
  });
});

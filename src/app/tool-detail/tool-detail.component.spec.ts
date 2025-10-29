import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ToolDetailComponent } from './tool-detail.component';
import { ToolDto } from '../dtos/ToolDto';

describe('ToolDetailComponent', () => {
  let component: ToolDetailComponent;
  let fixture: ComponentFixture<ToolDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ToolDetailComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ToolDetailComponent);
    component = fixture.componentInstance;
  });

  it('sollte erstellt werden', () => {
    expect(component).toBeTruthy();
  });

  it('sollte Beispielwert setzen, wenn kein tool übergeben wurde', () => {
    component.tool = undefined;
    component.ngOnInit();
    expect(component.tool).toBeTruthy();
    expect(component.tool!.name).toBe('Akkuschrauber Bosch GSR 12V');
    expect(component.tool!.status).toBe('in_benutzung');
  });

  it('sollte vorhandenes tool beibehalten, wenn gesetzt', () => {
    const customTool: ToolDto = {
      name: 'Bohrhammer Makita',
      description: 'Leistungsstarker Bohrhammer für Betonarbeiten',
      status: 'defekt',
    };

    component.tool = customTool;
    component.ngOnInit();

    expect(component.tool).toBe(customTool);
    expect(component.tool!.name).toBe('Bohrhammer Makita');
  });

  it('highlightValidated() sollte Status auf "verfügbar" setzen', () => {
    const tool: ToolDto = {
      name: 'Stichsäge Bosch',
      description: 'Für präzise Holzschnitte.',
      status: 'in_benutzung',
    };

    component.tool = tool;
    component.highlightValidated();

    expect(component.tool!.status).toBe('verfügbar');
  });

  it('highlightValidated() sollte nichts tun, wenn tool null ist', () => {
    component.tool = undefined;
    component.highlightValidated();
    expect(component.tool).toBeUndefined();
  });
});

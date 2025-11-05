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

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('should show example data when no tool is handed over', () => {
    component.tool = undefined;
    component.ngOnInit();
    expect(component.tool).toBeTruthy();
    expect(component.tool!.name).toBe('Akkuschrauber Bosch GSR 12V');
    expect(component.tool!.status).toBe('in_benutzung');
  });

  it('should contain tool when it is set', () => {
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

  it('should set status to "verfügbar"', () => {
    const tool: ToolDto = {
      name: 'Stichsäge Bosch',
      description: 'Für präzise Holzschnitte.',
      status: 'in_benutzung',
    };

    component.tool = tool;
    component.highlightValidated();

    expect(component.tool!.status).toBe('verfügbar');
  });

  it('should do nothing when tool is null', () => {
    component.tool = undefined;
    component.highlightValidated();
    expect(component.tool).toBeUndefined();
  });

it("should call closeEventEmitter", () => {
  spyOn(component.closePopUpEmitter, "emit");
  component.closePopUp();
  expect(component.closePopUpEmitter.emit).toHaveBeenCalled();
  });
  });

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WerkzeugDetailComponent } from './werkzeug-detail.component';

describe('WerkzeugDetailComponent', () => {
  let component: WerkzeugDetailComponent;
  let fixture: ComponentFixture<WerkzeugDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WerkzeugDetailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WerkzeugDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

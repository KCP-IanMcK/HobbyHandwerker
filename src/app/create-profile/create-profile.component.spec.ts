import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CreateProfileComponent } from './create-profile.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';

describe('CreateProfileComponent', () => {
  let component: CreateProfileComponent;
  let fixture: ComponentFixture<CreateProfileComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        CreateProfileComponent
      ]
    });

    fixture = TestBed.createComponent(CreateProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should reset form and show success message on successful submit', () => {
    spyOn(component['http'], 'post').and.returnValue(of({}));  // <- korrekt: Observable zurückgeben

    component.profile = { name: 'Test', email: 'test@test.com', bio: 'bio', avatarUrl: 'url' };
    component.submitForm();

    expect(component.profile.name).toBe('');
    expect(component.message).toBe('Profil erfolgreich erstellt!');
  });

  it('should show error message on failed submit', () => {
    spyOn(component['http'], 'post').and.returnValue(throwError(() => new Error('Fehler')));

    component.profile = { name: 'Test', email: 'test@test.com', bio: 'bio', avatarUrl: 'url' };
    component.submitForm();

    expect(component.message).toBe('Fehler beim Erstellen des Profils. Bitte versuche es später erneut.');
  });
});

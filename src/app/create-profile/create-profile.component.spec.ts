import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CreateProfileComponent } from './create-profile.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { HttpClient } from '@angular/common/http';
import { of, throwError } from 'rxjs';

describe('CreateProfileComponent', () => {
  let component: CreateProfileComponent;
  let fixture: ComponentFixture<CreateProfileComponent>;
  let http: HttpClient;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, CreateProfileComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CreateProfileComponent);
    component = fixture.componentInstance;
    http = TestBed.inject(HttpClient);
    fixture.detectChanges();
  });

  it('sollte die Komponente erstellen', () => {
    expect(component).toBeTruthy();
  });

  it('sollte Formular zurücksetzen und Erfolgsmeldung anzeigen bei erfolgreichem Submit', async () => {
    spyOn(http, 'post').and.returnValue(of({}));

    component.profile = { username: 'Test', email: 'test@test.com', password: '123', bio: 'bio', avatarUrl: 'url' };
    await component.submitForm();

    expect(component.profile!.username).toBe('');
    expect(component.profile!.email).toBe('');
    expect(component.profile!.password).toBe('');
    expect(component.profile!.bio).toBe('');
    expect(component.profile!.avatarUrl).toBe('');
    expect(component.message).toBe('Profil erfolgreich erstellt!');
  });

  it('sollte Fehlermeldung anzeigen bei fehlgeschlagenem Submit', async () => {
    spyOn(http, 'post').and.returnValue(
      throwError(() => new Error('Fehler'))
    );

    component.profile = { username: 'Test', email: 'test@test.com', password: '123', bio: 'bio', avatarUrl: 'url' };
    await component.submitForm();

    expect(component.message).toBe(
      'Fehler beim Erstellen des Profils. Bitte versuche es später erneut.'
    );
  });
});

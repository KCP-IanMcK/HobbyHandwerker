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
    spyOn(component, 'hashPassword').and.resolveTo('hashed-password');

    component.profileForm = {
      resetForm: () => {
        component.profile = {
          username: null as any,
          email: null as any,
          password: null as any,
          bio: null as any,
          avatarUrl: null as any
        };
      }
    } as any;

    component.profile = {
      username: 'Test',
      email: 'test@test.com',
      password: 'Abcdef1234!@',
      bio: 'bio',
      avatarUrl: 'url'
    };

    component.checkPasswordStrength();
    await component.submitForm();

    expect(component.profile.username).toBeNull();
    expect(component.profile.email).toBeNull();
    expect(component.profile.password).toBeNull();
    expect(component.profile.bio).toBeNull();
    expect(component.profile.avatarUrl).toBeNull();

    expect(component.message).toBe('User "Test" wurde erfolgreich erstellt!');
  });


  it('sollte Fehlermeldung anzeigen bei fehlgeschlagenem Submit', async () => {
    spyOn(http, 'post').and.returnValue(
      throwError(() => new Error('Fehler'))
    );
    spyOn(component, 'hashPassword').and.resolveTo('hashed-password');

    component.profile = {
      username: 'Test',
      email: 'test@test.com',
      password: 'Abcdef1234!@',
      bio: 'bio',
      avatarUrl: 'url'
    };

    component.checkPasswordStrength();
    await component.submitForm();

    expect(component.message).toBe('Fehler beim Erstellen des Profils.');
  });

  it('sollte Fehlermeldung anzeigen, wenn Pflichtfelder fehlen', async () => {
    component.profile = {
      username: '',
      email: '',
      password: '',
      bio: '',
      avatarUrl: ''
    };

    await component.submitForm();

    expect(component.message).toBe('Bitte alle Pflichtfelder ausfüllen.');
  });

  it('sollte Fehlermeldung anzeigen, wenn Passwort nicht stark genug ist', async () => {
    component.profile = {
      username: 'Test',
      email: 'test@test.com',
      password: 'abc',
      bio: '',
      avatarUrl: ''
    };

    component.checkPasswordStrength();
    await component.submitForm();

    expect(component.message).toBe('Passwort ist nicht stark genug.');
  });
});

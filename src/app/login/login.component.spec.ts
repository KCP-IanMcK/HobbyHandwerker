import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [LoginComponent, HttpClientTestingModule]
    });

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);

    // LocalStorage mocken
    spyOn(localStorage, 'setItem').and.callFake(() => {});
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should emit cancel event on onCancel', () => {
    let emitted = false;
    component.cancel.subscribe(() => emitted = true);

    component.onCancel();

    expect(emitted).toBeTrue();
  });
});

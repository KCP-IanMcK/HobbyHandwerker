import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(() => {
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [{ provide: Router, useValue: routerSpy }]
    });

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
  });

  it('should navigate to /home when username=admin and password=1234', () => {
    component.username = 'admin';
    component.password = '1234';

    component.onSubmit();

    expect(routerSpy.navigate).toHaveBeenCalledWith(['/home']);
  });

  it('should show alert when credentials are incorrect', () => {
    spyOn(window, 'alert');

    component.username = 'user';
    component.password = 'wrong';

    component.onSubmit();

    expect(window.alert).toHaveBeenCalledWith('Incorrect credentials. Please try again.');
    expect(routerSpy.navigate).not.toHaveBeenCalled();
  });

  it('should not navigate if only username is correct but password wrong', () => {
    spyOn(window, 'alert');

    component.username = 'admin';
    component.password = 'wrong';

    component.onSubmit();

    expect(window.alert).toHaveBeenCalled();
    expect(routerSpy.navigate).not.toHaveBeenCalled();
  });

  it('should not navigate if only password is correct but username wrong', () => {
    spyOn(window, 'alert');

    component.username = 'user';
    component.password = '1234';

    component.onSubmit();

    expect(window.alert).toHaveBeenCalled();
    expect(routerSpy.navigate).not.toHaveBeenCalled();
  });
});

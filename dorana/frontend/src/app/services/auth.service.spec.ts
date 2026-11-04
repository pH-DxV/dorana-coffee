import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { AuthService } from './auth.service';
import { LoginDTO, AuthDTOResponse } from '../models/auth.model';

describe('AuthService', () => {
  let service: AuthService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(AuthService);
    httpTesting = TestBed.inject(HttpTestingController);
    service.logout();
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should perform login and store token and user in localStorage', () => {
    const creds: LoginDTO = { login: 'admin', senha: '123' };
    const mockResponse: AuthDTOResponse = {
      id: 1,
      login: 'admin',
      nome: 'Administrador',
      email: 'admin@coffehouse.com',
      token: 'jwt-mock-token',
      perfil: 'Administrador'
    };

    service.login(creds).subscribe(res => {
      expect(res.body?.token).toBe('jwt-mock-token');
      expect(service.getToken()).toBe('jwt-mock-token');
      expect(service.isAuthenticated()).toBe(true);
      expect(service.currentUser()?.login).toBe('admin');
    });

    const req = httpTesting.expectOne('http://localhost:8080/auth/login');
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should logout and clear localStorage and currentUser signal', () => {
    service.setToken('dummy-token');
    expect(service.isAuthenticated()).toBe(true);

    service.logout();
    expect(service.getToken()).toBeNull();
    expect(service.isAuthenticated()).toBe(false);
    expect(service.currentUser()).toBeNull();
  });
});

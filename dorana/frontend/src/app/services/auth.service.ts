import { Injectable, signal } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { LoginDTO, AuthDTOResponse } from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly apiUrl = 'http://localhost:8080/auth';
  private readonly TOKEN_KEY = 'lgc_auth_token';
  private readonly USER_KEY = 'lgc_auth_user';

  readonly currentUser = signal<AuthDTOResponse | null>(this.getStoredUser());

  constructor(private http: HttpClient) {}

  login(credentials: LoginDTO): Observable<HttpResponse<AuthDTOResponse>> {
    return this.http.post<AuthDTOResponse>(`${this.apiUrl}/login`, credentials, {
      observe: 'response'
    }).pipe(
      tap(response => {
        let token = response.body?.token;
        if (!token) {
          const authHeader = response.headers.get('Authorization');
          if (authHeader && authHeader.startsWith('Bearer ')) {
            token = authHeader.substring(7);
          }
        }

        if (token) {
          this.setToken(token);
        }

        if (response.body) {
          const userData = { ...response.body, token };
          this.setStoredUser(userData);
          this.currentUser.set(userData);
        }
      })
    );
  }

  logout(): void {
    if (typeof window !== 'undefined') {
      localStorage.removeItem(this.TOKEN_KEY);
      localStorage.removeItem(this.USER_KEY);
    }
    this.currentUser.set(null);
  }

  getToken(): string | null {
    if (typeof window !== 'undefined') {
      return localStorage.getItem(this.TOKEN_KEY);
    }
    return null;
  }

  setToken(token: string): void {
    if (typeof window !== 'undefined') {
      localStorage.setItem(this.TOKEN_KEY, token);
    }
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  isAdmin(): boolean {
    const user = this.currentUser();
    if (!user) return false;
    if (user.perfil && user.perfil.toLowerCase().includes('admin')) {
      return true;
    }
    if (Array.isArray(user.perfis)) {
      return user.perfis.some(p =>
        p?.id === 1 ||
        (typeof p?.label === 'string' && p.label.toLowerCase().includes('admin'))
      );
    }
    return false;
  }

  private setStoredUser(user: AuthDTOResponse): void {
    if (typeof window !== 'undefined') {
      localStorage.setItem(this.USER_KEY, JSON.stringify(user));
    }
  }

  private getStoredUser(): AuthDTOResponse | null {
    if (typeof window !== 'undefined') {
      const user = localStorage.getItem(this.USER_KEY);
      return user ? JSON.parse(user) : null;
    }
    return null;
  }
}

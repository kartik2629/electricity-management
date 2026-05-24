import { Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/auth`;

  // Session signals
  currentUser = signal<any>(this.getStoredUser());

  private getStoredUser(): any {
    if (typeof window !== 'undefined') {
      const user = localStorage.getItem('user_session');
      return user ? JSON.parse(user) : null;
    }
    return null;
  }

  register(registerData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, registerData);
  }

  login(credentials: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, credentials).pipe(
      tap(res => {
        if (res && res.token) {
          localStorage.setItem('user_session', JSON.stringify(res));
          this.currentUser.set(res);
        }
      })
    );
  }

  logout(): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.post(`${this.apiUrl}/logout`, {}, { headers }).pipe(
      tap(() => {
        localStorage.removeItem('user_session');
        this.currentUser.set(null);
      })
    );
  }

  changePassword(passwordData: any): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.post(`${this.apiUrl}/change-password`, passwordData, { headers });
  }

  getToken(): string | null {
    const user = this.currentUser();
    return user ? user.token : null;
  }

  getRole(): string | null {
    const user = this.currentUser();
    return user ? user.role : null;
  }

  getUserName(): string | null {
    const user = this.currentUser();
    return user ? user.fullName : null;
  }

  getUserId(): string | null {
    const user = this.currentUser();
    return user ? user.userId : null;
  }

  getCustomerId(): string | null {
    const user = this.currentUser();
    return user ? user.customerId : null;
  }

  getEmail(): string | null {
    const user = this.currentUser();
    return user ? user.email : null;
  }

  isLoggedIn(): boolean {
    return this.currentUser() !== null;
  }

  getAuthHeaders(): HttpHeaders {
    const token = this.getToken();
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }
}

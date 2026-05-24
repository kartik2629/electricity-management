import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private apiUrl = `${environment.apiUrl}/customer`;

  getProfile(): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get(`${this.apiUrl}/profile`, { headers });
  }

  getBills(): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get(`${this.apiUrl}/bills`, { headers });
  }

  getLatestBill(): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get(`${this.apiUrl}/bills/latest`, { headers });
  }

  payBills(payload: any): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    return this.http.post(`${this.apiUrl}/pay`, payload, { headers });
  }

  getTransactionDetails(transactionId: string): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get(`${this.apiUrl}/transactions/${transactionId}`, { headers });
  }

  getConsumers(): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get(`${this.apiUrl}/consumers`, { headers });
  }

  registerComplaint(payload: any): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    return this.http.post(`${this.apiUrl}/complaints`, payload, { headers });
  }

  getComplaints(): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get(`${this.apiUrl}/complaints`, { headers });
  }

  searchComplaints(complaintId: string, status: string): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    let query = '';
    if (complaintId) query += `complaintId=${complaintId}&`;
    if (status) query += `status=${status}&`;
    return this.http.get(`${this.apiUrl}/complaints/search?${query}`, { headers });
  }
}

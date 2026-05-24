import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from './auth.service';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private apiUrl = `${environment.apiUrl}/admin`;

  addCustomer(payload: any): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    return this.http.post(`${this.apiUrl}/customers`, payload, { headers });
  }

  searchCustomers(query: string): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get(`${this.apiUrl}/customers/search?query=${query}`, { headers });
  }

  addConsumer(customerId: string, payload: any): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    return this.http.post(`${this.apiUrl}/consumers?customerId=${customerId}`, payload, { headers });
  }

  getConsumers(page: number, size: number, status: string, customerType: string, search: string): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    let query = `page=${page}&size=${size}&status=${status}&customerType=${customerType}`;
    if (search) {
      query += `&search=${search}`;
    }
    return this.http.get(`${this.apiUrl}/consumers?${query}`, { headers });
  }

  updateConsumer(consumerNo: string, payload: any): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    return this.http.put(`${this.apiUrl}/consumers/${consumerNo}`, payload, { headers });
  }

  toggleConsumerStatus(consumerNo: string): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    return this.http.post(`${this.apiUrl}/consumers/${consumerNo}/toggle-status`, {}, { headers });
  }

  addBill(consumerNo: string, payload: any): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    return this.http.post(`${this.apiUrl}/bills?consumerNo=${consumerNo}`, payload, { headers });
  }

  getBills(consumerNo: string, status: string, page: number, size: number): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    let query = `page=${page}&size=${size}&status=${status}`;
    if (consumerNo) {
      query += `&consumerNo=${consumerNo}`;
    }
    return this.http.get(`${this.apiUrl}/bills?${query}`, { headers });
  }

  getComplaints(status: string, complaintId: string, smeId: string): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    let query = `status=${status}`;
    if (complaintId) query += `&complaintId=${complaintId}`;
    if (smeId) query += `&smeId=${smeId}`;
    return this.http.get(`${this.apiUrl}/complaints?${query}`, { headers });
  }

  getSmes(): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get(`${this.apiUrl}/smes`, { headers });
  }

  assignComplaint(complaintId: string, smeId: string): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    return this.http.post(`${this.apiUrl}/complaints/${complaintId}/assign?smeId=${smeId}`, {}, { headers });
  }

  getDashboardStats(): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get(`${this.apiUrl}/dashboard-stats`, { headers });
  }
}

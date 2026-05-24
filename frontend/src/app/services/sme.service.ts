import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from './auth.service';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SmeService {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private apiUrl = `${environment.apiUrl}/sme`;

  getAssignedComplaints(status: string): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    return this.http.get(`${this.apiUrl}/complaints?status=${status}`, { headers });
  }

  resolveComplaint(complaintId: string, status: string, notes: string): Observable<any> {
    const headers = this.authService.getAuthHeaders();
    return this.http.post(
      `${this.apiUrl}/complaints/${complaintId}/resolve`,
      { status, resolutionNotes: notes },
      { headers }
    );
  }
}

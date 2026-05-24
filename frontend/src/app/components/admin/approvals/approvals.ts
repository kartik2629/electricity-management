import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../../services/admin.service';

@Component({
  selector: 'app-admin-approvals',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './approvals.html',
  styleUrl: './approvals.css'
})
export class AdminApprovals implements OnInit {
  private adminService = inject(AdminService);
  
  requests = signal<any[]>([]);
  isLoading = signal<boolean>(true);
  errorMessage = signal<string | null>(null);

  ngOnInit() {
    this.fetchRequests();
  }

  fetchRequests() {
    this.isLoading.set(true);
    this.adminService.getPendingRequests().subscribe({
      next: (res) => {
        this.requests.set(res);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.errorMessage.set('Failed to load pending requests.');
        this.isLoading.set(false);
      }
    });
  }

  approve(requestId: string) {
    this.adminService.approveConnection(requestId).subscribe({
      next: () => this.fetchRequests(),
      error: () => this.errorMessage.set('Approval failed.')
    });
  }

  reject(requestId: string) {
    this.adminService.rejectConnection(requestId).subscribe({
      next: () => this.fetchRequests(),
      error: () => this.errorMessage.set('Rejection failed.')
    });
  }
}

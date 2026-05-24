import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { SmeService } from '../../services/sme.service';
import { ThemeService } from '../../services/theme.service';
import { NotificationsComponent } from '../shared/notifications/notifications';

@Component({
  selector: 'app-sme',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterOutlet, FormsModule, NotificationsComponent],
  templateUrl: './sme.html',
  styleUrl: './sme.css'
})
export class SmeDashboard implements OnInit {
  private authService = inject(AuthService);
  private smeService = inject(SmeService);
  private router = inject(Router);
  public themeService = inject(ThemeService);

  complaints = signal<any[]>([]);
  isLoading = signal<boolean>(true);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);

  // Filters
  statusFilter = signal<string>('ALL');

  // Resolve Modal State
  showResolveModal = signal<boolean>(false);
  selectedComplaint = signal<any | null>(null);
  notes = signal<string>('');
  selectedStatus = signal<string>('RESOLVED');
  isSubmitting = signal<boolean>(false);

  ngOnInit() {
    this.loadComplaints();
  }

  loadComplaints() {
    this.isLoading.set(true);
    this.errorMessage.set(null);
    this.smeService.getAssignedComplaints(this.statusFilter()).subscribe({
      next: (data) => {
        this.complaints.set(data || []);
        this.isLoading.set(false);
      },
      error: () => {
        this.errorMessage.set('Failed to load assigned disputes.');
        this.isLoading.set(false);
      }
    });
  }

  onFilterChange() {
    this.loadComplaints();
  }

  openResolveModal(complaint: any) {
    this.selectedComplaint.set(complaint);
    this.notes.set(complaint.notes || '');
    this.selectedStatus.set(complaint.status === 'PENDING' ? 'IN_PROGRESS' : complaint.status);
    this.showResolveModal.set(true);
  }

  closeResolveModal() {
    this.showResolveModal.set(false);
    this.selectedComplaint.set(null);
    this.notes.set('');
  }

  submitResolution() {
    const comp = this.selectedComplaint();
    if (!comp || !this.notes().trim()) return;

    this.isSubmitting.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    this.smeService.resolveComplaint(comp.complaintId, this.selectedStatus(), this.notes()).subscribe({
      next: () => {
        this.successMessage.set(`Dispute ticket #${comp.complaintId} updated successfully.`);
        this.isSubmitting.set(false);
        this.closeResolveModal();
        this.loadComplaints();
      },
      error: (err) => {
        this.errorMessage.set(err.error?.message || 'Failed to update dispute ticket.');
        this.isSubmitting.set(false);
      }
    });
  }

  logout() {
    this.authService.logout().subscribe(() => {
      this.router.navigate(['/login']);
    });
  }
}

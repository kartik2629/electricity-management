import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../../services/admin.service';

@Component({
  selector: 'app-admin-complaints-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './complaints.html',
  styleUrl: './complaints.css'
})
export class AdminComplaintsList implements OnInit {
  private adminService = inject(AdminService);

  complaints = signal<any[]>([]);
  smeList = signal<any[]>([]);
  isLoading = signal<boolean>(true);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);

  // Filters
  statusFilter = signal<string>('ALL');
  complaintIdSearch = signal<string>('');
  smeFilter = signal<string>('');

  // Assignment Modal
  showAssignModal = signal<boolean>(false);
  selectedComplaint = signal<any | null>(null);
  selectedSmeId = signal<string>('');
  isAssigning = signal<boolean>(false);

  // Detail Modal
  showDetailModal = signal<boolean>(false);
  detailedComplaint = signal<any | null>(null);

  ngOnInit() {
    this.loadComplaints();
    this.loadSmes();
  }

  loadComplaints() {
    this.isLoading.set(true);
    this.adminService.getComplaints(
      this.statusFilter(),
      this.complaintIdSearch(),
      this.smeFilter()
    ).subscribe({
      next: (data) => {
        this.complaints.set(data || []);
        this.isLoading.set(false);
      },
      error: () => {
        this.errorMessage.set('Failed to load complaints.');
        this.isLoading.set(false);
      }
    });
  }

  loadSmes() {
    this.adminService.getSmes().subscribe({
      next: (data) => {
        this.smeList.set(data || []);
      }
    });
  }

  onFilterChange() {
    this.loadComplaints();
  }

  openAssignModal(complaint: any) {
    this.selectedComplaint.set(complaint);
    this.selectedSmeId.set(complaint.assignedSme?.userId || '');
    this.showAssignModal.set(true);
  }

  closeAssignModal() {
    this.showAssignModal.set(false);
    this.selectedComplaint.set(null);
    this.selectedSmeId.set('');
  }

  submitAssignment() {
    const comp = this.selectedComplaint();
    const smeId = this.selectedSmeId();
    if (!comp || !smeId) return;

    this.isAssigning.set(true);
    this.adminService.assignComplaint(comp.complaintId, smeId).subscribe({
      next: () => {
        this.successMessage.set(`Complaint #${comp.complaintId} successfully assigned.`);
        this.isAssigning.set(false);
        this.closeAssignModal();
        this.loadComplaints();
      },
      error: (err) => {
        this.errorMessage.set(err.error?.message || 'Failed to assign complaint.');
        this.isAssigning.set(false);
      }
    });
  }

  openDetailModal(complaint: any) {
    this.detailedComplaint.set(complaint);
    this.showDetailModal.set(true);
  }

  closeDetailModal() {
    this.showDetailModal.set(false);
    this.detailedComplaint.set(null);
  }
}

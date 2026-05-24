import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CustomerService } from '../../../services/customer.service';

@Component({
  selector: 'app-complaint-status',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './complaint-status.html',
  styleUrl: './complaint-status.css'
})
export class ComplaintStatus implements OnInit {
  private customerService = inject(CustomerService);

  complaintId = signal<string>('');
  statusFilter = signal<string>(''); // empty means ALL

  complaints = signal<any[]>([]);
  selectedComplaint = signal<any | null>(null);
  isLoading = signal<boolean>(false);
  hasSearched = signal<boolean>(false);
  errorMessage = signal<string | null>(null);

  ngOnInit() {
    // Initial fetch to load everything
    this.search();
  }

  search() {
    this.isLoading.set(true);
    this.errorMessage.set(null);
    this.hasSearched.set(true);
    this.selectedComplaint.set(null);

    this.customerService.searchComplaints(this.complaintId(), this.statusFilter()).subscribe({
      next: (res) => {
        this.complaints.set(res);
        this.isLoading.set(false);
      },
      error: () => {
        this.errorMessage.set('Failed to search complaints.');
        this.isLoading.set(false);
      }
    });
  }

  selectComplaint(complaint: any) {
    this.selectedComplaint.set(complaint);
  }

  clearSearch() {
    this.complaintId.set('');
    this.statusFilter.set('');
    this.search();
  }
}

import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CustomerService } from '../../../services/customer.service';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-complaint-history',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './complaints.html',
  styleUrl: './complaints.css'
})
export class ComplaintHistory implements OnInit {
  private customerService = inject(CustomerService);

  complaints = signal<any[]>([]);
  isLoading = signal<boolean>(true);
  errorMessage = signal<string | null>(null);

  // Filters
  searchTerm = signal<string>('');
  statusFilter = signal<string>('ALL');

  selectedComplaint = signal<any | null>(null);

  ngOnInit() {
    this.fetchComplaints();
  }

  fetchComplaints() {
    this.isLoading.set(true);
    this.customerService.getComplaints().subscribe({
      next: (data) => {
        // Sort newest first
        data.sort((a: any, b: any) => new Date(b.dateSubmitted).getTime() - new Date(a.dateSubmitted).getTime());
        this.complaints.set(data);
        this.isLoading.set(false);
      },
      error: () => {
        this.errorMessage.set('Failed to fetch complaint history.');
        this.isLoading.set(false);
      }
    });
  }

  filteredComplaints = computed(() => {
    let list = [...this.complaints()];
    
    // Status Filter
    const status = this.statusFilter();
    if (status !== 'ALL') {
      list = list.filter(c => c.status === status);
    }

    // Keyword Search (complaintId, description, complaintType)
    const term = this.searchTerm().toLowerCase().trim();
    if (term) {
      list = list.filter(c => 
        c.complaintId.toLowerCase().includes(term) ||
        c.description.toLowerCase().includes(term) ||
        c.complaintType.toLowerCase().includes(term) ||
        c.category.toLowerCase().includes(term)
      );
    }

    return list;
  });

  selectComplaint(complaint: any) {
    this.selectedComplaint.set(complaint);
  }

  closeDetails() {
    this.selectedComplaint.set(null);
  }
}

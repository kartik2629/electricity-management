import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CustomerService } from '../../../services/customer.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-bill-history',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './history.html',
  styleUrl: './history.css'
})
export class BillHistory implements OnInit {
  private customerService = inject(CustomerService);

  bills = signal<any[]>([]);
  isLoading = signal<boolean>(true);
  errorMessage = signal<string | null>(null);

  // Filters & Sorting state
  statusFilter = signal<string>('ALL'); // ALL, PAID, UNPAID
  sortBy = signal<string>('billDateDesc'); // billDateDesc, billDateAsc, dueDateDesc, dueDateAsc, amountDesc, amountAsc
  
  // Date range filters (default to past 6 months)
  startDate = signal<string>('');
  endDate = signal<string>('');

  ngOnInit() {
    this.setDefaultDateRange();
    this.fetchBills();
  }

  setDefaultDateRange() {
    const end = new Date();
    const start = new Date();
    start.setMonth(start.getMonth() - 6);

    this.endDate.set(this.formatDate(end));
    this.startDate.set(this.formatDate(start));
  }

  formatDate(date: Date): string {
    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, '0');
    const dd = String(date.getDate()).padStart(2, '0');
    return `${yyyy}-${mm}-${dd}`;
  }

  fetchBills() {
    this.isLoading.set(true);
    this.customerService.getBills().subscribe({
      next: (data) => {
        this.bills.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.errorMessage.set('Failed to fetch bills.');
        this.isLoading.set(false);
      }
    });
  }

  // Filtered and sorted bills
  processedBills = computed(() => {
    let list = [...this.bills()];

    // 1. Filter by status
    const status = this.statusFilter();
    if (status !== 'ALL') {
      list = list.filter(b => b.status === status);
    }

    // 2. Filter by date range (billDate)
    const startStr = this.startDate();
    const endStr = this.endDate();
    if (startStr) {
      const start = new Date(startStr);
      list = list.filter(b => new Date(b.billDate) >= start);
    }
    if (endStr) {
      const end = new Date(endStr);
      end.setHours(23, 59, 59, 999);
      list = list.filter(b => new Date(b.billDate) <= end);
    }

    // 3. Sort records
    const sort = this.sortBy();
    list.sort((a, b) => {
      if (sort === 'billDateDesc') {
        return new Date(b.billDate).getTime() - new Date(a.billDate).getTime();
      } else if (sort === 'billDateAsc') {
        return new Date(a.billDate).getTime() - new Date(b.billDate).getTime();
      } else if (sort === 'dueDateDesc') {
        return new Date(b.dueDate).getTime() - new Date(a.dueDate).getTime();
      } else if (sort === 'dueDateAsc') {
        return new Date(a.dueDate).getTime() - new Date(b.dueDate).getTime();
      } else if (sort === 'amountDesc') {
        return b.billAmount - a.billAmount;
      } else if (sort === 'amountAsc') {
        return a.billAmount - b.billAmount;
      }
      return 0;
    });

    return list;
  });

  resetFilters() {
    this.setDefaultDateRange();
    this.statusFilter.set('ALL');
    this.sortBy.set('billDateDesc');
  }

  isOverdue(dueDateStr: string): boolean {
    const dueDate = new Date(dueDateStr);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return today > dueDate;
  }
}

import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { AdminService } from '../../../services/admin.service';

@Component({
  selector: 'app-admin-bills-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './bills.html',
  styleUrl: './bills.css'
})
export class AdminBillsList implements OnInit {
  private adminService = inject(AdminService);
  private fb = inject(FormBuilder);

  // Listing state
  bills = signal<any[]>([]);
  totalElements = signal<number>(0);
  totalPages = signal<number>(0);
  currentPage = signal<number>(0);
  pageSize = 8;

  consumerFilter = signal<string>('');
  statusFilter = signal<string>('ALL');
  isLoading = signal<boolean>(true);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);

  // Add Bill Modal state
  showAddModal = signal<boolean>(false);
  billForm!: FormGroup;
  isSubmitting = signal<boolean>(false);

  // Month list helper
  months = [
    'January', 'February', 'March', 'April', 'May', 'June',
    'July', 'August', 'September', 'October', 'November', 'December'
  ];
  years = [2025, 2026, 2027];

  ngOnInit() {
    this.initBillForm();
    this.loadBills();
  }

  initBillForm() {
    this.billForm = this.fb.group({
      consumerNo: ['', [Validators.required, Validators.pattern('^[0-9]{13}$')]],
      billingMonth: ['', Validators.required],
      billingYear: ['', Validators.required],
      billDate: ['', Validators.required],
      dueDate: ['', Validators.required],
      disconnectionDate: ['', Validators.required],
      billAmount: ['', [Validators.required, Validators.min(0.01)]],
      lateFee: [0.0, [Validators.required, Validators.min(0)]]
    });
  }

  loadBills() {
    this.isLoading.set(true);
    this.adminService.getBills(
      this.consumerFilter(),
      this.statusFilter(),
      this.currentPage(),
      this.pageSize
    ).subscribe({
      next: (res) => {
        this.bills.set(res.content || []);
        this.totalElements.set(res.totalElements || 0);
        this.totalPages.set(res.totalPages || 0);
        this.isLoading.set(false);
      },
      error: () => {
        this.errorMessage.set('Failed to load bills.');
        this.isLoading.set(false);
      }
    });
  }

  onFilterChange() {
    this.currentPage.set(0);
    this.loadBills();
  }

  setPage(page: number) {
    if (page >= 0 && page < this.totalPages()) {
      this.currentPage.set(page);
      this.loadBills();
    }
  }

  openAddModal() {
    this.showAddModal.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.billForm.reset({
      consumerNo: '',
      billingMonth: '',
      billingYear: 2026,
      billDate: '',
      dueDate: '',
      disconnectionDate: '',
      billAmount: '',
      lateFee: 0.0
    });
  }

  closeAddModal() {
    this.showAddModal.set(false);
  }

  onSubmitBill() {
    if (this.billForm.invalid) {
      this.billForm.markAllAsTouched();
      return;
    }

    const val = this.billForm.value;

    // Date logical validations
    const bDate = new Date(val.billDate);
    const dDate = new Date(val.dueDate);
    const discDate = new Date(val.disconnectionDate);

    if (dDate < bDate) {
      this.errorMessage.set('Due Date cannot be earlier than Bill Date.');
      return;
    }

    if (discDate < dDate) {
      this.errorMessage.set('Disconnection Date cannot be earlier than Due Date.');
      return;
    }

    this.isSubmitting.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    const billingPeriod = `${val.billingMonth} ${val.billingYear}`;
    const payload = {
      billingPeriod,
      billDate: val.billDate,
      dueDate: val.dueDate,
      disconnectionDate: val.disconnectionDate,
      billAmount: val.billAmount,
      lateFee: val.lateFee
    };

    this.adminService.addBill(val.consumerNo, payload).subscribe({
      next: () => {
        this.successMessage.set(`Bill successfully generated for Consumer No ${val.consumerNo}`);
        this.showAddModal.set(false);
        this.isSubmitting.set(false);
        this.loadBills();
      },
      error: (err) => {
        this.errorMessage.set(err.error?.message || 'Failed to generate bill. Double-check duplicate periods.');
        this.isSubmitting.set(false);
      }
    });
  }
}

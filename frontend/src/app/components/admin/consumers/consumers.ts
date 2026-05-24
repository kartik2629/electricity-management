import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AdminService } from '../../../services/admin.service';

@Component({
  selector: 'app-admin-consumers-list',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './consumers.html',
  styleUrl: './consumers.css'
})
export class AdminConsumersList implements OnInit {
  private adminService = inject(AdminService);
  private fb = inject(FormBuilder);

  // Pagination & Filtering state
  consumers = signal<any[]>([]);
  totalElements = signal<number>(0);
  totalPages = signal<number>(0);
  currentPage = signal<number>(0);
  pageSize = 8;

  statusFilter = signal<string>('ALL');
  typeFilter = signal<string>('ALL');
  searchQuery = signal<string>('');
  isLoading = signal<boolean>(true);
  errorMessage = signal<string | null>(null);

  // Modal State for Edit
  selectedConsumer = signal<any | null>(null);
  editForm!: FormGroup;
  isSaving = signal<boolean>(false);

  // Custom Confirmation Modal State
  showConfirmModal = signal<boolean>(false);
  confirmMessage = signal<string>('');
  pendingAction = signal<(() => void) | null>(null);

  // Electrical Sections options
  electricalSections = ['NORTH SECTION', 'SOUTH SECTION', 'EAST SECTION', 'WEST SECTION', 'CENTRAL SECTION'];

  ngOnInit() {
    this.initEditForm();
    this.loadConsumers();
  }

  initEditForm() {
    this.editForm = this.fb.group({
      address: ['', Validators.required],
      electricalSection: ['', Validators.required],
      customerType: ['', Validators.required],
      status: ['', Validators.required]
    });
  }

  loadConsumers() {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.adminService.getConsumers(
      this.currentPage(),
      this.pageSize,
      this.statusFilter(),
      this.typeFilter(),
      this.searchQuery()
    ).subscribe({
      next: (res) => {
        this.consumers.set(res.content || []);
        this.totalElements.set(res.totalElements || 0);
        this.totalPages.set(res.totalPages || 0);
        this.isLoading.set(false);
      },
      error: () => {
        this.errorMessage.set('Failed to load consumer connections.');
        this.isLoading.set(false);
      }
    });
  }

  onFilterChange() {
    this.currentPage.set(0);
    this.loadConsumers();
  }

  setPage(page: number) {
    if (page >= 0 && page < this.totalPages()) {
      this.currentPage.set(page);
      this.loadConsumers();
    }
  }

  openEditModal(consumer: any) {
    this.selectedConsumer.set(consumer);
    this.editForm.patchValue({
      address: consumer.address,
      electricalSection: consumer.electricalSection,
      customerType: consumer.customerType,
      status: consumer.status
    });
  }

  closeEditModal() {
    this.selectedConsumer.set(null);
  }

  onUpdate() {
    if (this.editForm.invalid) {
      this.editForm.markAllAsTouched();
      return;
    }

    const consumer = this.selectedConsumer();
    if (!consumer) return;

    this.isSaving.set(true);
    this.adminService.updateConsumer(consumer.consumerNo, this.editForm.value).subscribe({
      next: () => {
        this.isSaving.set(false);
        this.closeEditModal();
        this.loadConsumers();
      },
      error: (err) => {
        this.errorMessage.set(err.error?.message || 'Failed to update consumer details.');
        this.isSaving.set(false);
      }
    });
  }

  toggleStatus(consumer: any) {
    const action = consumer.status === 'ACTIVE' ? 'disconnect' : 'reconnect';
    this.confirmMessage.set(`Are you sure you want to ${action} Consumer connection #${consumer.consumerNo}?`);
    
    this.pendingAction.set(() => {
      this.adminService.toggleConsumerStatus(consumer.consumerNo).subscribe({
        next: () => {
          this.loadConsumers();
          this.closeConfirmModal();
        },
        error: (err) => {
          this.errorMessage.set(err.error?.message || 'Failed to toggle status.');
          this.closeConfirmModal();
        }
      });
    });
    this.showConfirmModal.set(true);
  }

  closeConfirmModal() {
    this.showConfirmModal.set(false);
    this.pendingAction.set(null);
  }

  triggerConfirmAction() {
    const action = this.pendingAction();
    if (action) {
      action();
    }
  }
}

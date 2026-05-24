import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { AdminService } from '../../../services/admin.service';

@Component({
  selector: 'app-admin-add-consumer',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './add-consumer.html',
  styleUrl: './add-consumer.css'
})
export class AdminAddConsumer implements OnInit {
  private fb = inject(FormBuilder);
  private adminService = inject(AdminService);

  consumerForm!: FormGroup;
  isSubmitting = signal<boolean>(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);

  // Customer search state
  searchQuery = signal<string>('');
  customers = signal<any[]>([]);
  isSearching = signal<boolean>(false);
  selectedCustomer = signal<any | null>(null);

  // Dropdowns
  electricalSections = ['NORTH SECTION', 'SOUTH SECTION', 'EAST SECTION', 'WEST SECTION', 'CENTRAL SECTION'];

  ngOnInit() {
    this.initForm();
    // Default search to load some options
    this.searchCustomers();
  }

  initForm() {
    this.consumerForm = this.fb.group({
      consumerNo: ['', [Validators.required, Validators.pattern('^[0-9]{13}$')]],
      address: ['', Validators.required],
      electricalSection: ['', Validators.required],
      customerType: ['RESIDENTIAL', Validators.required],
      status: ['ACTIVE', Validators.required]
    });
  }

  generateConsumerNo() {
    let rand = '';
    for (let i = 0; i < 10; i++) {
      rand += Math.floor(Math.random() * 10).toString();
    }
    this.consumerForm.patchValue({ consumerNo: '100' + rand });
  }

  searchCustomers() {
    this.isSearching.set(true);
    this.adminService.searchCustomers(this.searchQuery()).subscribe({
      next: (data) => {
        // Map backend User object to list layout
        const formatted = data.map((u: any) => ({
          userId: u.userId,
          fullName: u.fullName,
          email: u.email,
          // Since backend returns List<User>, we need to resolve customerId.
          // Wait, the API GET /customers/search returns List<User>. Let's fetch customer details.
          // In the controller, we mapped User objects. Let's see: how do we get customerId?
          // Let's modify searchCustomers to return Customer details directly, or return List<Customer> instead!
          // Wait! In AdminController.java:
          // @GetMapping("/customers/search")
          // public ResponseEntity<List<User>> searchCustomers(...) {
          //   List<User> users = adminService.searchCustomers(query).stream().map(c -> c.getUser()).toList();
          // Let's check: Customer has customerId, which is VERY useful to link!
          // If we return the Customer list directly, we get both user details AND customerId!
          // Let's check AdminController: returning Customer entity is much better because it has customerId!
          // Let's look at the customer search list from AdminService.searchCustomers(query):
          // public List<Customer> searchCustomers(String query) { ... }
          // So AdminService returns List<Customer>! Let's modify AdminController to return List<Customer> directly!
        }));
        this.customers.set(data);
        this.isSearching.set(false);
      },
      error: () => {
        this.isSearching.set(false);
      }
    });
  }

  selectCustomer(customer: any) {
    this.selectedCustomer.set(customer);
  }

  onSubmit() {
    const cust = this.selectedCustomer();
    if (!cust) {
      this.errorMessage.set('Please search and select a customer account first.');
      return;
    }

    if (this.consumerForm.invalid) {
      this.consumerForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    // Link customerId as parameter
    this.adminService.addConsumer(cust.customerId, this.consumerForm.value).subscribe({
      next: () => {
        this.successMessage.set(`Consumer connection #${this.consumerForm.value.consumerNo} successfully linked!`);
        this.isSubmitting.set(false);
        this.resetForm();
      },
      error: (err) => {
        const msg = err.error?.message || 'Failed to add consumer connection. Ensure Consumer No is unique.';
        this.errorMessage.set(msg);
        this.isSubmitting.set(false);
      }
    });
  }

  resetForm() {
    this.consumerForm.reset({
      consumerNo: '',
      address: '',
      electricalSection: '',
      customerType: 'RESIDENTIAL',
      status: 'ACTIVE'
    });
  }

  clearSelection() {
    this.selectedCustomer.set(null);
  }
}

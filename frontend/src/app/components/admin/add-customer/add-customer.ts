import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AdminService } from '../../../services/admin.service';

@Component({
  selector: 'app-admin-add-customer',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './add-customer.html',
  styleUrl: './add-customer.css'
})
export class AdminAddCustomer implements OnInit {
  private fb = inject(FormBuilder);
  private adminService = inject(AdminService);

  customerForm!: FormGroup;
  isSubmitting = signal<boolean>(false);
  errorMessage = signal<string | null>(null);

  // Success state for displaying generated credentials
  createdCustomer = signal<any | null>(null);

  ngOnInit() {
    this.initForm();
  }

  initForm() {
    this.customerForm = this.fb.group({
      userId: ['', [Validators.required, Validators.minLength(3), Validators.pattern('^[a-zA-Z0-9_]+$')]],
      fullName: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      mobileNumber: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]]
    });
  }

  onSubmit() {
    if (this.customerForm.invalid) {
      this.customerForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.errorMessage.set(null);

    this.adminService.addCustomer(this.customerForm.value).subscribe({
      next: (res) => {
        this.createdCustomer.set(res);
        this.isSubmitting.set(false);
      },
      error: (err) => {
        const msg = err.error?.message || 'Failed to register customer. Username or email might be taken.';
        this.errorMessage.set(msg);
        this.isSubmitting.set(false);
      }
    });
  }

  resetForm() {
    this.createdCustomer.set(null);
    this.customerForm.reset();
  }

  copySuccess = signal<boolean>(false);

  copyToClipboard(text: string) {
    navigator.clipboard.writeText(text).then(() => {
      this.copySuccess.set(true);
      setTimeout(() => this.copySuccess.set(false), 2000);
    });
  }
}

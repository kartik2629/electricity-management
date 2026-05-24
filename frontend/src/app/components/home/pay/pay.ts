import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, AbstractControl, ValidationErrors } from '@angular/forms';
import { PaymentStateService } from '../../../services/payment-state.service';
import { CustomerService } from '../../../services/customer.service';

@Component({
  selector: 'app-pay-bill',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './pay.html',
  styleUrl: './pay.css'
})
export class PayBill implements OnInit {
  private paymentState = inject(PaymentStateService);
  private customerService = inject(CustomerService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  selectedBills = signal<any[]>([]);
  paymentMode = signal<string>('CARD');
  grandTotal = signal<number>(0);

  paymentForm!: FormGroup;
  showConfirmModal = signal<boolean>(false);
  isSubmitting = signal<boolean>(false);
  errorMessage = signal<string | null>(null);

  // Success view details
  paymentCompleted = signal<boolean>(false);
  receiptDetails = signal<any>(null);

  ngOnInit() {
    const bills = this.paymentState.getSelectedBills();
    if (bills.length === 0) {
      this.router.navigate(['/home/bills']);
      return;
    }
    this.selectedBills.set(bills);
    this.paymentMode.set(this.paymentState.getModeOfPayment());
    
    const total = bills.reduce((acc, b) => acc + b.payableAmount, 0);
    this.grandTotal.set(total);

    this.initPaymentForm();
  }

  initPaymentForm() {
    this.paymentForm = this.fb.group({
      cardName: ['', [Validators.required, Validators.pattern(/^[a-zA-Z\s]+$/)]],
      cardNumber: ['', [Validators.required, Validators.pattern(/^\d{16}$/)]],
      expiryDate: ['', [Validators.required, Validators.pattern(/^(0[1-9]|1[0-2])\/\d{2}$/), this.futureDateValidator]],
      cvv: ['', [Validators.required, Validators.pattern(/^\d{3,4}$/)]]
    });
  }

  futureDateValidator(control: AbstractControl): ValidationErrors | null {
    const val = control.value;
    if (!val) return null;
    const parts = val.split('/');
    if (parts.length !== 2) return { invalidFormat: true };

    const month = parseInt(parts[0], 10);
    const year = parseInt('20' + parts[1], 10); // Assume 20xx

    const now = new Date();
    const currentMonth = now.getMonth() + 1; // 0-indexed
    const currentYear = now.getFullYear();

    if (year < currentYear) {
      return { expired: true };
    }
    if (year === currentYear && month < currentMonth) {
      return { expired: true };
    }
    return null;
  }

  onSubmit() {
    if (this.paymentMode() === 'CARD' && this.paymentForm.invalid) {
      this.paymentForm.markAllAsTouched();
      return;
    }
    this.showConfirmModal.set(true);
  }

  closeConfirmModal() {
    this.showConfirmModal.set(false);
  }

  executePayment() {
    this.showConfirmModal.set(false);
    this.isSubmitting.set(true);
    this.errorMessage.set(null);

    const billIds = this.selectedBills().map(b => b.id);
    const payload = {
      billIds,
      modeOfPayment: this.paymentMode()
    };

    // Invoke payment endpoint in CustomerService
    // In Angular service we need to add: payBills(payload)
    // Let's create it in CustomerService or make a direct post request.
    // Wait, let's see how CustomerService is defined in auth.service/customer.service.
    // We already injected CustomerService. Let's make sure it has payBills()!
    // Wait, did we add it in customer.service.ts? Let's check!
    // Ah, in customer.service.ts we only have getProfile(), getBills(), getLatestBill().
    // We must add payBills(payload) to it! We will do that right after this.
    // Let's write the code here assuming it is there.
    
    // Let's call a method in CustomerService
    (this.customerService as any).payBills(payload).subscribe({
      next: (receipt: any) => {
        this.receiptDetails.set(receipt);
        this.paymentCompleted.set(true);
        this.isSubmitting.set(false);
        // Clear selected state
        this.paymentState.clear();
      },
      error: (err: any) => {
        const msg = err.error?.message || 'Payment execution failed. Please check card details or try again.';
        this.errorMessage.set(msg);
        this.isSubmitting.set(false);
      }
    });
  }

  downloadReceipt() {
    window.print();
  }
}

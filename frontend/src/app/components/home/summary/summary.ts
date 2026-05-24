import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { PaymentStateService } from '../../../services/payment-state.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-bill-summary',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './summary.html',
  styleUrl: './summary.css'
})
export class BillSummary implements OnInit {
  private paymentState = inject(PaymentStateService);
  private router = inject(Router);

  selectedBills = signal<any[]>([]);
  paymentMode = signal<string>('CARD');

  grandTotal = signal<number>(0);

  ngOnInit() {
    const bills = this.paymentState.getSelectedBills();
    if (bills.length === 0) {
      // Redirect back if no bills selected
      this.router.navigate(['/home/bills']);
      return;
    }
    this.selectedBills.set(bills);
    this.calculateGrandTotal();
  }

  calculateGrandTotal() {
    const total = this.selectedBills().reduce((acc, bill) => acc + bill.payableAmount, 0);
    this.grandTotal.set(total);
  }

  onModeChange(event: any) {
    this.paymentMode.set(event.target.value);
  }

  proceedToPayment() {
    this.paymentState.setModeOfPayment(this.paymentMode());
    this.router.navigate(['/home/pay']);
  }
}

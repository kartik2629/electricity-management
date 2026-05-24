import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { CustomerService } from '../../../services/customer.service';
import { PaymentStateService } from '../../../services/payment-state.service';

@Component({
  selector: 'app-bills-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './bills.html',
  styleUrl: './bills.css'
})
export class BillsList implements OnInit {
  private customerService = inject(CustomerService);
  private paymentState = inject(PaymentStateService);
  private router = inject(Router);

  bills = signal<any[]>([]);
  isLoading = signal<boolean>(true);
  errorMessage = signal<string | null>(null);

  // Selected bill IDs mapped to boolean
  selectedMap = signal<{ [key: number]: boolean }>({});

  unpaidBills = computed(() => this.bills().filter(b => b.status === 'UNPAID'));
  paidBills = computed(() => this.bills().filter(b => b.status === 'PAID'));

  // Calculate running total of selected unpaid bills
  runningTotal = computed(() => {
    const selMap = this.selectedMap();
    return this.unpaidBills().reduce((acc, bill) => {
      if (selMap[bill.id]) {
        // Late fee is applied if date is overdue
        const payable = bill.billAmount + (this.isOverdue(bill.dueDate) && bill.lateFee <= 0 ? 10.0 : bill.lateFee);
        return acc + payable;
      }
      return acc;
    }, 0);
  });

  isAllSelected = computed(() => {
    const unpaid = this.unpaidBills();
    if (unpaid.length === 0) return false;
    const selMap = this.selectedMap();
    return unpaid.every(b => selMap[b.id]);
  });

  ngOnInit() {
    this.fetchBills();
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

  isOverdue(dueDateStr: string): boolean {
    const dueDate = new Date(dueDateStr);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return today > dueDate;
  }

  toggleBill(billId: number) {
    const current = { ...this.selectedMap() };
    current[billId] = !current[billId];
    this.selectedMap.set(current);
  }

  toggleAll(event: any) {
    const checked = event.target.checked;
    const current: { [key: number]: boolean } = {};
    if (checked) {
      this.unpaidBills().forEach(b => {
        current[b.id] = true;
      });
    }
    this.selectedMap.set(current);
  }

  proceedToPay() {
    const selectedList = this.unpaidBills().filter(b => this.selectedMap()[b.id]).map(b => {
      // Calculate final amount with late fee if overdue
      const finalLateFee = this.isOverdue(b.dueDate) && b.lateFee <= 0 ? 10.00 : b.lateFee;
      return {
        ...b,
        payableAmount: b.billAmount + finalLateFee,
        calculatedLateFee: finalLateFee
      };
    });

    if (selectedList.length === 0) return;

    this.paymentState.setSelectedBills(selectedList);
    this.router.navigate(['/home/summary']);
  }
}

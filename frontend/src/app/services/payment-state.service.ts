import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class PaymentStateService {
  // Selected bills for checkout
  selectedBills = signal<any[]>([]);
  modeOfPayment = signal<string>('CARD');
  
  // Successful transaction receipt details
  transactionReceipt = signal<any>(null);

  setSelectedBills(bills: any[]) {
    this.selectedBills.set(bills);
  }

  getSelectedBills(): any[] {
    return this.selectedBills();
  }

  setModeOfPayment(mode: string) {
    this.modeOfPayment.set(mode);
  }

  getModeOfPayment(): string {
    return this.modeOfPayment();
  }

  clear() {
    this.selectedBills.set([]);
    this.modeOfPayment.set('CARD');
    this.transactionReceipt.set(null);
  }
}

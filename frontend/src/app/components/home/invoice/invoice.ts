import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CustomerService } from '../../../services/customer.service';

@Component({
  selector: 'app-invoice-view',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './invoice.html',
  styleUrl: './invoice.css'
})
export class InvoiceView implements OnInit {
  private route = inject(ActivatedRoute);
  private customerService = inject(CustomerService);

  transactionId = signal<string | null>(null);
  invoice = signal<any>(null);
  isLoading = signal<boolean>(true);
  errorMessage = signal<string | null>(null);

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const id = params.get('transactionId');
      this.transactionId.set(id);
      if (id) {
        this.fetchInvoiceDetails(id);
      } else {
        this.errorMessage.set('Invalid Transaction ID.');
        this.isLoading.set(false);
      }
    });
  }

  fetchInvoiceDetails(id: string) {
    this.isLoading.set(true);
    this.customerService.getTransactionDetails(id).subscribe({
      next: (data) => {
        this.invoice.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        const msg = err.error?.message || 'Invoice failed to load or access is denied.';
        this.errorMessage.set(msg);
        this.isLoading.set(false);
      }
    });
  }

  printInvoice() {
    window.print();
  }
}

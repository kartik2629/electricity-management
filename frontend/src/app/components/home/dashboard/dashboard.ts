import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CustomerService } from '../../../services/customer.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {
  private customerService = inject(CustomerService);

  profileData = signal<any>(null);
  latestBill = signal<any>(null);
  isLoading = signal<boolean>(true);
  todayDate = new Date();

  ngOnInit() {
    this.loadDashboardData();
  }

  loadDashboardData() {
    this.isLoading.set(true);
    this.customerService.getProfile().subscribe({
      next: (profile) => {
        this.profileData.set(profile);
        this.customerService.getLatestBill().subscribe({
          next: (bill) => {
            if (bill && !bill.message) {
              this.latestBill.set(bill);
            }
            this.isLoading.set(false);
          },
          error: () => {
            this.isLoading.set(false);
          }
        });
      },
      error: () => {
        this.isLoading.set(false);
      }
    });
  }
}

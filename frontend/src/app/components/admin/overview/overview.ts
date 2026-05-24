import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../../services/admin.service';

@Component({
  selector: 'app-admin-overview',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './overview.html',
  styleUrl: './overview.css'
})
export class AdminOverview implements OnInit {
  private adminService = inject(AdminService);

  isLoading = signal<boolean>(true);
  stats = signal<any>(null);

  // SVG donut chart helpers (circumference = 2 * PI * 45 ≈ 283)
  getDonutDash(val: number, total: number): string {
    if (!total) return '0 283';
    return `${(val / total) * 283} 283`;
  }

  getDonutOffset(segments: number[], index: number, total: number): number {
    if (!total) return 0;
    let offset = 0;
    for (let i = 0; i < index; i++) {
      offset += (segments[i] / total) * 283;
    }
    return -offset;
  }

  // Gauge arc helper (semi-circle: circumference of r=45 half = 141.5)
  getGaugeArc(pct: number): string {
    const arc = (pct / 100) * 141.5;
    return `${arc} 283`;
  }

  ngOnInit() {
    this.loadStats();
  }

  loadStats() {
    this.isLoading.set(true);
    this.adminService.getDashboardStats().subscribe({
      next: (res) => {
        this.stats.set(res);
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
      }
    });
  }
}

import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ThemeService } from '../../services/theme.service';
import { NotificationsComponent } from '../shared/notifications/notifications';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterOutlet, NotificationsComponent],
  templateUrl: './admin.html',
  styleUrl: './admin.css'
})
export class AdminDashboard {
  private authService = inject(AuthService);
  private router = inject(Router);
  public themeService = inject(ThemeService);

  logout() {
    this.authService.logout().subscribe(() => {
      this.router.navigate(['/login']);
    });
  }
}

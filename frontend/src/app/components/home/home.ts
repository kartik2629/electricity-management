import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ThemeService } from '../../services/theme.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterOutlet],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home implements OnInit {
  protected authService = inject(AuthService);
  private router = inject(Router);
  public themeService = inject(ThemeService);

  userName = signal<string | null>('');
  customerId = signal<string | null>('');
  userEmail = signal<string | null>('');

  ngOnInit() {
    this.userName.set(this.authService.getUserName());
    this.customerId.set(this.authService.getCustomerId());
    this.userEmail.set(this.authService.getEmail());
  }

  logout() {
    this.authService.logout().subscribe({
      next: () => {
        this.router.navigate(['/login']);
      },
      error: () => {
        this.router.navigate(['/login']);
      }
    });
  }
}

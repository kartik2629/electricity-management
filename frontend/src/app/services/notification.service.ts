import { Injectable, inject, signal } from '@angular/core';
import { AuthService } from './auth.service';
import { environment } from '../../environments/environment';

export interface AppNotification {
  id: string;
  message: string;
  type: string;
  timestamp: string;
  read: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private authService = inject(AuthService);
  private eventSource: EventSource | null = null;
  
  notifications = signal<AppNotification[]>([]);
  unreadCount = signal<number>(0);
  
  // Toasts
  activeToasts = signal<AppNotification[]>([]);

  connect() {
    if (this.eventSource) return;
    
    const token = this.authService.getToken();
    if (!token) return;

    // Use SSE Polyfill or native EventSource with query param token since native EventSource doesn't support Authorization headers easily.
    // However, our backend checks request.getAttribute("currentUser") which is populated by AuthInterceptor parsing the Authorization header.
    // Wait! EventSource does not send custom headers.
    // We must pass the token as a query parameter and modify AuthInterceptor to read it.
    this.eventSource = new EventSource(`${environment.apiUrl}/notifications/subscribe?token=${token}`);

    this.eventSource.addEventListener('notification', (event: any) => {
      const data = JSON.parse(event.data);
      const newNotif: AppNotification = { ...data, read: false };
      
      this.notifications.update(n => [newNotif, ...n]);
      this.unreadCount.update(c => c + 1);
      
      this.showToast(newNotif);
    });

    this.eventSource.onerror = (error) => {
      console.error('SSE Error:', error);
      this.disconnect();
      // Attempt reconnect after 5s
      setTimeout(() => this.connect(), 5000);
    };
  }

  disconnect() {
    if (this.eventSource) {
      this.eventSource.close();
      this.eventSource = null;
    }
  }

  markAsRead(id: string) {
    this.notifications.update(n => {
      const idx = n.findIndex(x => x.id === id);
      if (idx !== -1 && !n[idx].read) {
        n[idx].read = true;
        this.unreadCount.update(c => Math.max(0, c - 1));
      }
      return [...n];
    });
  }

  markAllAsRead() {
    this.notifications.update(n => n.map(x => ({ ...x, read: true })));
    this.unreadCount.set(0);
  }

  private showToast(notif: AppNotification) {
    this.activeToasts.update(t => [...t, notif]);
    setTimeout(() => {
      this.activeToasts.update(t => t.filter(x => x.id !== notif.id));
    }, 5000);
  }
}

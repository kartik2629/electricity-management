import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  isDarkMode = signal<boolean>(false);

  constructor() {
    const saved = localStorage.getItem('theme');
    if (saved === 'dark') {
      this.setDark();
    } else {
      this.setLight();
    }
  }

  toggleTheme() {
    if (this.isDarkMode()) {
      this.setLight();
    } else {
      this.setDark();
    }
  }

  private setDark() {
    this.isDarkMode.set(true);
    document.body.classList.add('dark-theme');
    localStorage.setItem('theme', 'dark');
  }

  private setLight() {
    this.isDarkMode.set(false);
    document.body.classList.remove('dark-theme');
    localStorage.setItem('theme', 'light');
  }
}

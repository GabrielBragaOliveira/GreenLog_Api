import { Injectable, inject, signal, effect } from '@angular/core';
import { DOCUMENT } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private document = inject(DOCUMENT);
  isDarkMode = signal<boolean>(false);

  constructor() {
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
      this.isDarkMode.set(true);
    }

    effect(() => {
      const isDark = this.isDarkMode();
      const themeName = isDark ? 'aura-dark-green' : 'aura-light-green';
      const themeLink = this.document.getElementById('app-theme') as HTMLLinkElement;

      if (themeLink) {
        themeLink.href = `assets/themes/${themeName}/theme.css`;
      }

      localStorage.setItem('theme', isDark ? 'dark' : 'light');
      
      if (isDark) {
        this.document.body.classList.add('dark-mode');
      } else {
        this.document.body.classList.remove('dark-mode');
      }
    });
  }

  toggleTheme() {
    this.isDarkMode.update(current => !current);
  }
}
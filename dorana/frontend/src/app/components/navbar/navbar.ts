import { Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NavigationEnd, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { filter } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class NavbarComponent {
  readonly authService = inject(AuthService);
  readonly cartService = inject(CartService);
  private readonly router = inject(Router);

  searchTerm = '';
  mobileMenuOpen = signal(false);
  currentPath = signal('/');

  readonly isAdminRoute = computed(() => {
    const p = this.currentPath();
    return (
      p.startsWith('/admin') ||
      p.startsWith('/cafes') ||
      p.startsWith('/estoque') ||
      p.startsWith('/marcas') ||
      p.startsWith('/categorias') ||
      p.startsWith('/estados') ||
      p.startsWith('/municipios') ||
      p.startsWith('/usuarios')
    );
  });

  constructor() {
    this.currentPath.set(this.router.url || '/');
    this.router.events
      .pipe(filter((e): e is NavigationEnd => e instanceof NavigationEnd))
      .subscribe(event => {
        this.currentPath.set(event.urlAfterRedirects || event.url);
        this.mobileMenuOpen.set(false);
      });
  }

  onSearch(): void {
    const q = this.searchTerm.trim();
    this.router.navigate(['/catalogo'], {
      queryParams: q ? { q } : {}
    });
  }

  toggleMobileMenu(): void {
    this.mobileMenuOpen.update(v => !v);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}

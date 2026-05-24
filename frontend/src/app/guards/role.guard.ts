import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const roleGuard = (route: ActivatedRouteSnapshot) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const expectedRoles = route.data['roles'] as Array<string>;
  const userRole = authService.getRole();

  if (authService.isLoggedIn() && userRole && expectedRoles.includes(userRole.toUpperCase())) {
    return true;
  }

  router.navigate(['/login']);
  return false;
};

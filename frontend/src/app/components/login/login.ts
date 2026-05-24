import { Component, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  loginForm: FormGroup;
  changePasswordForm: FormGroup;

  errorMessage = signal<string | null>(null);
  isSubmitting = signal<boolean>(false);
  
  // First time login flow
  showChangePasswordPrompt = signal<boolean>(false);
  tempSessionData = signal<any>(null); // holds login session info temporarily before password change

  constructor() {
    this.loginForm = this.fb.group({
      userId: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });

    this.changePasswordForm = this.fb.group({
      oldPassword: ['', [Validators.required]],
      newPassword: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/)
      ]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });
  }

  private passwordMatchValidator(control: any) {
    const newPwd = control.get('newPassword');
    const confirmPwd = control.get('confirmPassword');
    if (newPwd && confirmPwd && newPwd.value !== confirmPwd.value) {
      confirmPwd.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }
    return null;
  }

  onSubmit() {
    this.errorMessage.set(null);
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    const credentials = this.loginForm.value;

    this.authService.login(credentials).subscribe({
      next: (res) => {
        this.isSubmitting.set(false);
        if (res.firstLogin) {
          // Force password change
          this.tempSessionData.set(res);
          this.showChangePasswordPrompt.set(true);
          this.changePasswordForm.patchValue({ oldPassword: credentials.password });
        } else {
          this.redirectByRole(res.role);
        }
      },
      error: (err) => {
        this.isSubmitting.set(false);
        this.errorMessage.set(err.error?.message || 'Invalid username or password.');
      }
    });
  }

  onChangePasswordSubmit() {
    this.errorMessage.set(null);
    if (this.changePasswordForm.invalid) {
      this.changePasswordForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    
    // Call change password using temporary session
    this.authService.changePassword(this.changePasswordForm.value).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        // Clear firstLogin flag, save to active session, and redirect
        const session = this.tempSessionData();
        session.isFirstLogin = false;
        localStorage.setItem('user_session', JSON.stringify(session));
        this.authService.currentUser.set(session);
        this.redirectByRole(session.role);
      },
      error: (err) => {
        this.isSubmitting.set(false);
        this.errorMessage.set(err.error?.message || 'Password change failed.');
      }
    });
  }

  private redirectByRole(role: string) {
    const roleUpper = role.toUpperCase();
    if (roleUpper === 'ADMIN') {
      this.router.navigate(['/admin']);
    } else if (roleUpper === 'SME') {
      this.router.navigate(['/sme']);
    } else {
      this.router.navigate(['/home']);
    }
  }
}

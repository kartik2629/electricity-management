import { Component, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, AbstractControl } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  registerForm: FormGroup;
  errorMessage = signal<string | null>(null);
  successData = signal<any>(null); // holds { customerId, customerName, email } upon success
  isSubmitting = signal<boolean>(false);

  constructor() {
    this.registerForm = this.fb.group({
      consumerNo: ['', [Validators.required, Validators.pattern(/^\d{13}$/)]],
      fullName: ['', [Validators.required, Validators.pattern(/^[a-zA-Z\s]+$/), Validators.maxLength(50)]],
      address: ['', [Validators.required, Validators.minLength(10)]],
      email: ['', [Validators.required, Validators.email]],
      countryCode: ['+91', [Validators.required]],
      mobileNumber: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]],
      customerType: ['RESIDENTIAL', [Validators.required]],
      electricalSection: ['OFFICE', [Validators.required]],
      userId: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(20)]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/)
      ]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });
  }

  private passwordMatchValidator(control: AbstractControl): { [key: string]: boolean } | null {
    const password = control.get('password');
    const confirmPassword = control.get('confirmPassword');
    if (password && confirmPassword && password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }
    return null;
  }

  onSubmit() {
    this.errorMessage.set(null);
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    const formValues = { ...this.registerForm.value };
    formValues.mobileNumber = formValues.countryCode + '-' + formValues.mobileNumber;
    delete formValues.countryCode;

    this.authService.register(formValues).subscribe({
      next: (res) => {
        this.isSubmitting.set(false);
        this.successData.set(res);
        this.registerForm.reset();
      },
      error: (err) => {
        this.isSubmitting.set(false);
        const serverMsg = err.error?.message || 'Registration failed. Please check inputs.';
        // Map specific validation errors from REST API if any
        if (typeof err.error === 'object' && !err.error.message) {
          const keys = Object.keys(err.error);
          if (keys.length > 0) {
            this.errorMessage.set(err.error[keys[0]]);
            return;
          }
        }
        this.errorMessage.set(serverMsg);
      }
    });
  }
}

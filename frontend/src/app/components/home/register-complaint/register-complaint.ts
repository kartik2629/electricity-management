import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CustomerService } from '../../../services/customer.service';

@Component({
  selector: 'app-register-complaint',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register-complaint.html',
  styleUrl: './register-complaint.css'
})
export class RegisterComplaint implements OnInit {
  private fb = inject(FormBuilder);
  private customerService = inject(CustomerService);
  private router = inject(Router);

  complaintForm!: FormGroup;
  consumers = signal<any[]>([]);
  profile = signal<any>(null);
  isLoading = signal<boolean>(true);
  isSubmitting = signal<boolean>(false);
  errorMessage = signal<string | null>(null);

  // Success details state
  submittedComplaint = signal<any | null>(null);

  // Complaint Types & Categories Definition
  complaintTypes = [
    { value: 'BILLING_ISSUE', label: 'Billing Issue' },
    { value: 'POWER_OUTAGE', label: 'Power Outage' },
    { value: 'METER_READING_ISSUE', label: 'Meter Reading Issue' }
  ];

  categoriesMap: { [key: string]: string[] } = {
    'BILLING_ISSUE': ['Incorrect Bill Amount', 'Payment Failed But Deducted', 'Double Billing'],
    'POWER_OUTAGE': ['Total Blackout', 'Fluctuating Voltage', 'Transformer Sparking'],
    'METER_READING_ISSUE': ['Faulty Display', 'Incorrect Meter Reading', 'Physical Meter Damage']
  };

  availableCategories = signal<string[]>([]);

  ngOnInit() {
    this.initForm();
    this.loadInitialData();
  }

  initForm() {
    this.complaintForm = this.fb.group({
      consumerNo: ['', Validators.required],
      complaintType: ['', Validators.required],
      category: ['', Validators.required],
      description: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(500)]],
      preferredContactMethod: ['EMAIL', Validators.required],
      contactDetails: ['', Validators.required]
    });

    // Listen for changes in Complaint Type to update Categories
    this.complaintForm.get('complaintType')?.valueChanges.subscribe((type) => {
      this.availableCategories.set(this.categoriesMap[type] || []);
      this.complaintForm.get('category')?.setValue('');
    });

    // Listen for changes in Preferred Contact Method to auto-populate contactDetails
    this.complaintForm.get('preferredContactMethod')?.valueChanges.subscribe((method) => {
      this.updateContactDetailsField(method);
    });
  }

  loadInitialData() {
    this.isLoading.set(true);
    // Fetch both profile and consumers in parallel
    this.customerService.getProfile().subscribe({
      next: (profileData) => {
        this.profile.set(profileData);
        this.customerService.getConsumers().subscribe({
          next: (consumersList) => {
            this.consumers.set(consumersList);
            if (consumersList.length > 0) {
              this.complaintForm.get('consumerNo')?.setValue(consumersList[0].consumerNo);
            }
            this.updateContactDetailsField('EMAIL');
            this.isLoading.set(false);
          },
          error: () => {
            this.errorMessage.set('Failed to load consumer accounts.');
            this.isLoading.set(false);
          }
        });
      },
      error: () => {
        this.errorMessage.set('Failed to load profile details.');
        this.isLoading.set(false);
      }
    });
  }

  updateContactDetailsField(method: string) {
    const prof = this.profile();
    if (!prof) return;

    if (method === 'EMAIL') {
      this.complaintForm.get('contactDetails')?.setValue(prof.email || prof.contactEmail || '');
    } else if (method === 'PHONE') {
      this.complaintForm.get('contactDetails')?.setValue(prof.mobileNumber || prof.contactPhone || '');
    }
  }

  onSubmit() {
    if (this.complaintForm.invalid) {
      this.complaintForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.errorMessage.set(null);

    this.customerService.registerComplaint(this.complaintForm.value).subscribe({
      next: (res) => {
        this.submittedComplaint.set(res);
        this.isSubmitting.set(false);
      },
      error: (err) => {
        const msg = err.error?.message || 'Failed to lodge complaint. Please check fields.';
        this.errorMessage.set(msg);
        this.isSubmitting.set(false);
      }
    });
  }

  resetForm() {
    this.submittedComplaint.set(null);
    this.complaintForm.reset({
      consumerNo: this.consumers().length > 0 ? this.consumers()[0].consumerNo : '',
      complaintType: '',
      category: '',
      description: '',
      preferredContactMethod: 'EMAIL',
      contactDetails: ''
    });
    this.updateContactDetailsField('EMAIL');
  }
}

import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
    selector: 'app-register',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, RouterLink],
    template: `
        <div class="paper container">
            <h2 class="text-center">Register</h2>
            <form [formGroup]="registerForm" (ngSubmit)="onSubmit()" class="form-group">
                <div class="form-group">
                    <label for="username">Username</label>
                    <input 
                        type="text" 
                        id="username" 
                        formControlName="username" 
                        class="input-block">
                    <div class="text-danger" *ngIf="registerForm.get('username')?.touched && registerForm.get('username')?.errors?.['required']">
                        Username is required
                    </div>
                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <input 
                        type="password" 
                        id="password" 
                        formControlName="password" 
                        class="input-block">
                    <div class="text-danger" *ngIf="registerForm.get('password')?.touched && registerForm.get('password')?.errors?.['required']">
                        Password is required
                    </div>
                    <div class="text-danger" *ngIf="registerForm.get('password')?.touched && registerForm.get('password')?.errors?.['minlength']">
                        Password must be at least 6 characters
                    </div>
                </div>

                <div class="text-danger margin-bottom" *ngIf="error">{{ error }}</div>

                <button type="submit" class="btn-block" [disabled]="!registerForm.valid">Register</button>
                <p class="text-center margin-top">Already have an account? <a routerLink="/login">Login here</a></p>
            </form>
        </div>
    `,
    styles: [`
        .container {
            max-width: 400px;
        }
        .text-danger {
            color: var(--danger);
            font-size: 0.875rem;
        }
    `]
})
export class RegisterComponent {
    registerForm: FormGroup;
    error: string = '';

    constructor(
        private fb: FormBuilder,
        private authService: AuthService,
        private router: Router
    ) {
        this.registerForm = this.fb.group({
            username: ['', Validators.required],
            password: ['', [Validators.required, Validators.minLength(6)]]
        });
    }

    onSubmit(): void {
        if (this.registerForm.valid) {
            this.authService.register(this.registerForm.value).subscribe({
                next: () => {
                    this.router.navigate(['/dashboard']);
                },
                error: (error) => {
                    this.error = 'Registration failed. Please try again.';
                    console.error('Registration error:', error);
                }
            });
        }
    }
} 
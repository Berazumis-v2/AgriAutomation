import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, RouterLink],
    template: `
        <div class="paper container">
            <h2 class="text-center">Login</h2>
            <form [formGroup]="loginForm" (ngSubmit)="onSubmit()" class="form-group">
                <div class="form-group">
                    <label for="username">Username</label>
                    <input 
                        type="text" 
                        id="username" 
                        formControlName="username" 
                        class="input-block">
                    <div class="text-danger" *ngIf="loginForm.get('username')?.touched && loginForm.get('username')?.errors?.['required']">
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
                    <div class="text-danger" *ngIf="loginForm.get('password')?.touched && loginForm.get('password')?.errors?.['required']">
                        Password is required
                    </div>
                </div>

                <div class="text-danger margin-bottom" *ngIf="error">{{ error }}</div>

                <button type="submit" class="btn-block" [disabled]="!loginForm.valid">Login</button>
                <p class="text-center margin-top">Don't have an account? <a routerLink="/register">Register here</a></p>
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
export class LoginComponent {
    loginForm: FormGroup;
    error: string = '';

    constructor(
        private fb: FormBuilder,
        private authService: AuthService,
        private router: Router
    ) {
        this.loginForm = this.fb.group({
            username: ['', Validators.required],
            password: ['', Validators.required]
        });
    }

    onSubmit(): void {
        if (this.loginForm.valid) {
            this.authService.login(this.loginForm.value).subscribe({
                next: () => {
                    this.router.navigate(['/dashboard']);
                },
                error: (error) => {
                    this.error = 'Login failed. Please check your credentials.';
                    console.error('Login error:', error);
                }
            });
        }
    }
} 
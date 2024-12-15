import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-dashboard',
    standalone: true,
    imports: [CommonModule],
    template: `
        <div class="paper container">
            <h2 class="text-center">Dashboard</h2>
            
            <div class="card margin-bottom">
                <div class="card-body">
                    <div class="token-header">
                        <h4 class="card-title">Your Access Token</h4>
                        <div class="button-group">
                            <button class="btn-small margin-right" (click)="refreshToken()">
                                Refresh Token
                            </button>
                            <button class="btn-small" (click)="toggleToken()">
                                {{ showToken ? 'Hide Token' : 'Show Token' }}
                            </button>
                        </div>
                    </div>
                    <div class="token-display" *ngIf="showToken">
                        {{ accessToken }}
                    </div>
                    <div class="token-placeholder" *ngIf="!showToken">
                        ••••••••••••••••••••••
                    </div>
                </div>
            </div>

            <div class="card margin-bottom">
                <div class="card-body">
                    <h4 class="card-title">Refresh Token Status</h4>
                    <div [class]="'status-message ' + (hasRefreshToken ? 'status-success' : 'status-error')">
                        <span class="status-icon">{{ hasRefreshToken ? '✓' : '✗' }}</span>
                        {{ hasRefreshToken ? 'Refresh token cookie is present' : 'No refresh token cookie found' }}
                    </div>
                </div>
            </div>
        </div>
    `,
    styles: [`
        .token-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1rem;
        }
        .token-display {
            padding: 1rem;
            background-color: var(--primary-shaded-70);
            border-radius: 4px;
            word-break: break-all;
            font-family: monospace;
            font-size: 0.875rem;
        }
        .token-placeholder {
            padding: 1rem;
            background-color: var(--primary-shaded-70);
            border-radius: 4px;
            font-family: monospace;
            font-size: 0.875rem;
            text-align: center;
        }
        .btn-small {
            padding: 0.2rem 0.4rem;
        }
        .status-message {
            padding: 1rem;
            border-radius: 4px;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        .status-success {
            background-color: var(--success-light);
            color: var(--success-text);
        }
        .status-error {
            background-color: var(--danger-light);
            color: var(--danger-text);
        }
        .status-icon {
            font-size: 1.2rem;
        }
        .button-group {
            display: flex;
            gap: 0.5rem;
        }
        .margin-right {
            margin-right: 0.5rem;
        }
    `]
})
export class DashboardComponent implements OnInit {
    accessToken: string = '';
    showToken: boolean = false;
    hasRefreshToken: boolean = false;

    constructor(private authService: AuthService) {}

    ngOnInit() {
        this.accessToken = this.authService.getToken() || 'No token found';
        this.checkRefreshToken();
    }

    toggleToken() {
        this.showToken = !this.showToken;
    }

    private checkRefreshToken() {
        // For HttpOnly cookies, we can't read the value directly
        // but we can check if it exists by making a request to the server
        this.authService.checkRefreshToken().subscribe({
            next: (isValid) => {
                this.hasRefreshToken = isValid;
            },
            error: () => {
                this.hasRefreshToken = false;
            }
        });
    }

    refreshToken() {
        this.authService.refreshToken().subscribe({
            next: () => {
                this.accessToken = this.authService.getToken() || 'No token found';
                this.checkRefreshToken();
            },
            error: () => {
                this.accessToken = 'Token refresh failed';
            }
        });
    }
} 
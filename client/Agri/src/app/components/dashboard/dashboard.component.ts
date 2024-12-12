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
                    <h4 class="card-title">Your Access Token</h4>
                    <div class="token-display">
                        {{ accessToken }}
                    </div>
                </div>
            </div>
        </div>
    `,
    styles: [`
        .token-display {
            padding: 1rem;
            background-color: var(--primary-shaded-70);
            border-radius: 4px;
            word-break: break-all;
            font-family: monospace;
            font-size: 0.875rem;
        }
    `]
})
export class DashboardComponent implements OnInit {
    accessToken: string = '';

    constructor(private authService: AuthService) {}

    ngOnInit() {
        this.accessToken = this.authService.getToken() || 'No token found';
    }

    logout() {
        this.authService.logout();
    }
} 
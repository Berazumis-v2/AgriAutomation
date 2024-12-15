import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { MessageComponent } from '../../components/shared/message.component';
import { FooterComponent } from '../../components/shared/footer.component';
import { BackgroundComponent } from '../../components/shared/background.component';

@Component({
    selector: 'app-main-layout',
    standalone: true,
    imports: [CommonModule, RouterOutlet, RouterLink, MessageComponent, FooterComponent, BackgroundComponent],
    template: `
        <app-background></app-background>
        <app-message></app-message>
        <nav class="border split-nav">
            <div class="nav-brand">
                <h3>Agri Automation</h3>
            </div>
            <div class="collapsible">
                <input id="collapsible1" type="checkbox" name="collapsible1">
                <label for="collapsible1">
                    <div class="bar1"></div>
                    <div class="bar2"></div>
                    <div class="bar3"></div>
                </label>
                <div class="collapsible-body">
                    <ul class="inline">
                        <li><a routerLink="/plant-care-systems">Plant Care Systems</a></li>
                        <li *ngIf="!isLoggedIn"><a routerLink="/login">Login</a></li>
                        <li *ngIf="!isLoggedIn"><a routerLink="/register">Register</a></li>
                        <li *ngIf="isLoggedIn"><a routerLink="/dashboard">Dashboard</a></li>
                        <li *ngIf="isLoggedIn"><a href="#" (click)="logout($event)">Logout</a></li>
                    </ul>
                </div>
            </div>
        </nav>

        <main class="container margin-top-large">
            <router-outlet></router-outlet>
        </main>

        <app-footer></app-footer>
    `,
    styles: [`
        :host {
            display: flex;
            flex-direction: column;
            min-height: 100vh;
        }

        nav {
            padding: 1rem;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .nav-brand h3 {
            margin: 0;
            font-size: 1.5rem;
        }

        main {
            flex: 1;
        }
    `]
})
export class MainLayoutComponent {
    constructor(private authService: AuthService) {}

    get isLoggedIn(): boolean {
        return this.authService.isLoggedIn();
    }

    logout(event: Event): void {
        event.preventDefault();
        this.authService.logout();
    }
} 
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-footer',
    standalone: true,
    imports: [CommonModule],
    template: `
        <footer class="footer">
            <div class="container">
                <div class="content">
                    <p>© 2024 Agri Automation. Developed for T120B165</p>
                </div>
            </div>
        </footer>
    `,
    styles: [`
        .footer {
            background-color: var(--primary-shaded-70);
            padding: 1rem 0;
            margin-top: 2rem;
            width: 100%;
        }

        .container {
            max-width: 960px;
            margin: 0 auto;
            padding: 0 1rem;
        }

        .content {
            text-align: center;
        }

        p {
            margin: 0.5rem 0;
            color: var(--primary);
        }
    `]
})
export class FooterComponent {}
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-background',
    standalone: true,
    imports: [CommonModule],
    template: `
        <div class="background-image"></div>
    `,
    styles: [`
        .background-image {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: url('../../../../public//background.jpg') center/cover no-repeat;
            opacity: 0.3;
            pointer-events: none;
        }
    `]
})
export class BackgroundComponent {} 
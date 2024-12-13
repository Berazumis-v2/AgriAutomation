import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MessageService } from '../../services/message.service';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-message',
    standalone: true,
    imports: [CommonModule],
    template: `
        <div *ngIf="message" class="message-container" [ngClass]="message.type">
            {{ message.text }}
            <button class="close-btn" (click)="clear()">×</button>
        </div>
    `,
    styles: [`
        .message-container {
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 1rem;
            border-radius: 4px;
            z-index: 1000;
            display: flex;
            align-items: center;
            gap: 1rem;
        }

        .error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }

        .success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }

        .warning {
            background-color: #fff3cd;
            color: #856404;
            border: 1px solid #ffeeba;
        }

        .close-btn {
            background: none;
            border: none;
            font-size: 1.5rem;
            cursor: pointer;
            padding: 0;
            margin: 0;
            line-height: 1;
        }
    `]
})
export class MessageComponent implements OnInit, OnDestroy {
    message: { text: string; type: string } | null = null;
    private subscription: Subscription | null = null;

    constructor(private messageService: MessageService) {}

    ngOnInit() {
        this.subscription = this.messageService.message$.subscribe(
            message => this.message = message
        );
    }

    ngOnDestroy() {
        this.subscription?.unsubscribe();
    }

    clear() {
        this.messageService.clear();
    }
} 
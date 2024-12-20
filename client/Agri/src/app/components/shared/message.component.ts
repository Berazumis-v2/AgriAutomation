import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MessageService, Message } from '../../services/message.service';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-message',
    standalone: true,
    imports: [CommonModule],
    template: `
        <div class="messages-container">
            <div *ngFor="let message of messages" 
                 class="message-container" 
                 [ngClass]="message.type">
                {{ message.text }}
                <button class="close-btn" (click)="removeMessage(message.id)">×</button>
            </div>
        </div>
    `,
    styles: [`
        .messages-container {
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 1000;
            display: flex;
            flex-direction: column;
            gap: 10px;
            max-width: 400px;
        }

        .message-container {
            padding: 1rem;
            border-radius: 4px;
            display: flex;
            align-items: center;
            gap: 1rem;
            animation: slideIn 0.3s ease-out;
        }

        @keyframes slideIn {
            from {
                transform: translateX(100%);
                opacity: 0;
            }
            to {
                transform: translateX(0);
                opacity: 1;
            }
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

        .info {
            background-color: #cce5ff;
            color: #004085;
            border: 1px solid #b8daff;
        }

        .close-btn {
            background: none;
            border: none;
            font-size: 1.5rem;
            cursor: pointer;
            padding: 0;
            margin: 0;
            line-height: 1;
            margin-left: auto;
        }
    `]
})
export class MessageComponent implements OnInit, OnDestroy {
    messages: Message[] = [];
    private subscription: Subscription | null = null;

    constructor(private messageService: MessageService) {}

    ngOnInit() {
        this.subscription = this.messageService.messages$.subscribe(
            messages => this.messages = messages
        );
    }

    ngOnDestroy() {
        this.subscription?.unsubscribe();
    }

    removeMessage(id: number) {
        this.messageService.removeMessage(id);
    }
} 
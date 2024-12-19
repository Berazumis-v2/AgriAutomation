import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface Message {
    id: number;
    text: string;
    type: 'error' | 'success' | 'warning' | 'info';
    timestamp: number;
}

@Injectable({
    providedIn: 'root'
})
export class MessageService {
    private messageSubject = new BehaviorSubject<Message[]>([]);
    messages$ = this.messageSubject.asObservable();
    private nextId = 1;

    private addMessage(text: string, type: Message['type']) {
        const newMessage: Message = {
            id: this.nextId++,
            text,
            type,
            timestamp: Date.now()
        };
        
        const currentMessages = this.messageSubject.value;
        this.messageSubject.next([...currentMessages, newMessage]);

        // Auto-remove message after 5 seconds
        setTimeout(() => {
            this.removeMessage(newMessage.id);
        }, 60000);
    }

    showError(text: string) {
        this.addMessage(text, 'error');
    }

    showSuccess(text: string) {
        this.addMessage(text, 'success');
    }

    showWarning(text: string) {
        this.addMessage(text, 'warning');
    }

    showInfo(text: string) {
        this.addMessage(text, 'info');
    }

    removeMessage(id: number) {
        const currentMessages = this.messageSubject.value;
        this.messageSubject.next(currentMessages.filter(msg => msg.id !== id));
    }

    clear() {
        this.messageSubject.next([]);
    }
} 
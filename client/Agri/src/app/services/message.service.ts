import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface Message {
    text: string;
    type: 'error' | 'success' | 'warning';
}

@Injectable({
    providedIn: 'root'
})
export class MessageService {
    private messageSubject = new BehaviorSubject<Message | null>(null);
    message$ = this.messageSubject.asObservable();

    showError(text: string) {
        this.messageSubject.next({ text, type: 'error' });
    }

    showSuccess(text: string) {
        this.messageSubject.next({ text, type: 'success' });
    }

    showWarning(text: string) {
        this.messageSubject.next({ text, type: 'warning' });
    }

    clear() {
        this.messageSubject.next(null);
    }
} 
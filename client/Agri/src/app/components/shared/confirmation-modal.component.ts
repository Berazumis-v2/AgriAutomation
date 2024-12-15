import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-confirmation-modal',
    standalone: true,
    imports: [CommonModule],
    template: `
        <div class="modal-overlay" *ngIf="show" (click)="onCancel()">
            <div class="modal-content" (click)="$event.stopPropagation()">
                <h3>Confirm Deletion</h3>
                <p>Are you sure you want to delete <strong>{{itemName}}</strong>?</p>
                <p class="warning">This action cannot be undone.</p>
                
                <div class="button-group">
                    <button class="btn-secondary" (click)="onCancel()">Cancel</button>
                    <button class="btn-danger" (click)="onConfirm()">Delete</button>
                </div>
            </div>
        </div>
    `,
    styles: [`
        .modal-overlay {
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background-color: rgba(0, 0, 0, 0.5);
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 1000;
        }

        .modal-content {
            background: var(--main-background);
            padding: 2rem;
            border-radius: 8px;
            max-width: 400px;
            width: 90%;
            text-align: center;
        }

        .warning {
            color: var(--danger);
            font-size: 0.875rem;
            margin: 1rem 0;
        }

        .button-group {
            display: flex;
            justify-content: center;
            gap: 1rem;
            margin-top: 1.5rem;
        }

        h3 {
            margin-top: 0;
        }
    `]
})
export class ConfirmationModalComponent {
    @Input() show = false;
    @Input() itemName = '';
    @Output() confirm = new EventEmitter<void>();
    @Output() cancel = new EventEmitter<void>();

    onConfirm() {
        this.confirm.emit();
    }

    onCancel() {
        this.cancel.emit();
    }
} 
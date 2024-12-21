import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { PlantCareSystemService } from '../../services/plant-care-system.service';
import { PlantCareSystem } from '../../interfaces/plant-care-system.interface';
import { MessageService } from '../../services/message.service';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { FormsModule } from '@angular/forms';
import { ConfirmationModalComponent } from '../shared/confirmation-modal.component';

interface SortOption {
    label: string;
    value: string;
    direction: 'asc' | 'desc';
}

@Component({
    selector: 'app-plant-care-list',
    standalone: true,
    imports: [CommonModule, RouterLink, FormsModule, ConfirmationModalComponent],
    template: `
        <div class="paper container">
            <div class="banner-section">
                <div class="banner-image-container">
                    <img src="care-systems.jpg" alt="Agricultural Systems" class="height-[20%]">
                </div>
                <h2 class="section-title text-center">Plant Care Systems</h2>
            </div>
            
            <a *ngIf="isLoggedIn" routerLink="new" class="paper-btn btn-primary btn-block margin-bottom text-center">
                Add New System
            </a>
            
            <div class="search-container margin-bottom">
                <div class="search-wrapper">
                    <input 
                        type="text" 
                        [(ngModel)]="searchTerm" 
                        (input)="onSearch()"
                        placeholder="Search by name..."
                        class="search-input">
                    <input 
                        type="number" 
                        [(ngModel)]="searchId" 
                        (input)="onSearch()"
                        placeholder="ID"
                        class="id-input">
                    <button 
                        *ngIf="searchTerm || searchId" 
                        (click)="clearSearch()" 
                        class="btn-small clear-btn">
                        Clear
                    </button>
                </div>
            </div>

            <div class="sort-container margin-bottom">
                <select (change)="onSort($event)" class="sort-select">
                    <option *ngFor="let option of sortOptions" 
                            [selected]="option === selectedSort">
                        {{option.label}}
                    </option>
                </select>
            </div>

            <div *ngIf="filteredSystems.length === 0" class="empty-message">
                <p>No plant care systems found</p>
                <p *ngIf="searchTerm" class="sub-text">Try adjusting your search term</p>
            </div>

            <div class="grid-container" *ngIf="filteredSystems.length > 0">
                <div class="card system-card" *ngFor="let system of filteredSystems">
                    <div class="card-body">
                        <div class="card-header">
                            <span class="system-id">#{{system.id}}</span>
                            <h4 class="card-title">{{system.name}}</h4>
                        </div>
                        <p class="description">{{system.description}}</p>
                        <div class="details">
                            <p><strong>Automation:</strong> {{system.automationEnabled ? 'Enabled' : 'Disabled'}}</p>
                            <p><strong>Maintenance:</strong> {{system.maintenanceTimeStamp | date:'medium'}}</p>
                            <p><strong>Created By:</strong> {{system.createdBy.username}}</p>
                        </div>
                        <div class="card-actions">
                            <button class="btn-small" (click)="viewSensors(system.id)">Sensors</button>
                            <div *ngIf="isLoggedIn" class="action-buttons">
                                <button class="btn-small" (click)="editSystem(system.id)" title="Edit">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                                        <path d="M12.146.146a.5.5 0 0 1 .708 0l3 3a.5.5 0 0 1 0 .708l-10 10a.5.5 0 0 1-.168.11l-5 2a.5.5 0 0 1-.65-.65l2-5a.5.5 0 0 1 .11-.168l10-10zM11.207 2.5 13.5 4.793 14.793 3.5 12.5 1.207 11.207 2.5zm1.586 3L10.5 3.207 4 9.707V10h.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.5h.293l6.5-6.5zm-9.761 5.175-.106.106-1.528 3.821 3.821-1.528.106-.106A.5.5 0 0 1 5 12.5V12h-.5a.5.5 0 0 1-.5-.5V11h-.5a.5.5 0 0 1-.468-.325z"/>
                                    </svg>
                                </button>
                                <button class="btn-small btn-danger" (click)="deleteSystem(system)" title="Delete">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                                        <path d="M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0V6z"/>
                                        <path fill-rule="evenodd" d="M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1v1zM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4H4.118zM2.5 3V2h11v1h-11z"/>
                                    </svg>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <app-confirmation-modal
                [show]="showDeleteModal"
                [itemName]="systemToDelete?.name || ''"
                (confirm)="confirmDelete()"
                (cancel)="cancelDelete()"
            ></app-confirmation-modal>
        </div>
    `,
    styles: [`
        .grid-container {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 1rem;
            padding: 1rem 0;
        }

        .system-card {
            height: 100%;
            display: flex;
            flex-direction: column;
        }

        .card-body {
            flex: 1;
            display: flex;
            flex-direction: column;
        }

        .description {
            flex-grow: 1;
            margin: 0.5rem 0;
        }

        .details {
            margin: 1rem 0;
            p {
                margin: 0.25rem 0;
            }
        }

        .actions {
            display: flex;
            gap: 0.5rem;
            justify-content: flex-end;
            margin-top: auto;
        }

        .btn-small {
            padding: 0.2rem 0.4rem;
            display: inline-flex;
            align-items: center;
            justify-content: center;

            svg {
                width: 16px;
                height: 16px;
            }
        }

        @media (max-width: 600px) {
            .grid-container {
                grid-template-columns: 1fr;
            }
        }

         .banner-section {
        margin: -2rem -2rem 2rem -2rem;
        text-align: center;  /* Center the section content */
    }

    .banner-image-container {
        min-height: 200px;
        display: flex;
        align-items: center;
        justify-content: center;
        background-color: var(--primary-shaded-70);
        padding: 1rem;
        margin: 0 auto;  /* Center the container */
    }

    .section-title {
        padding: 1rem;
        margin: 0;
        background-color: var(--primary-shaded-70);
        text-align: center;  /* Center the title */
    }

    .search-container {
        max-width: 600px;
        margin: 0 auto;
    }

    .search-wrapper {
        display: flex;
        gap: 0.5rem;
        align-items: center;
    }

    .search-input {
        flex: 1;
    }

    .id-input {
        width: 80px;
        text-align: center;
    }

    .clear-btn {
        background-color: var(--danger-light);
        color: var(--danger);
        border: 1px solid var(--danger);
        &:hover {
            background-color: var(--danger);
            color: white;
        }
    }

    .empty-message {
        text-align: center;
        padding: 2rem;
        background-color: var(--primary-shaded-70);
        border-radius: 4px;
    }

    .sub-text {
        color: var(--muted);
        font-size: 0.875rem;
        margin-top: 0.5rem;
    }

    .card-header {
        display: flex;
        align-items: baseline;
        gap: 0.5rem;
        margin-bottom: 0.5rem;
    }

    .system-id {
        font-size: 0.75rem;
        color: var(--muted);
        font-weight: normal;
    }

    .card-title {
        margin: 0;
        flex: 1;
    }

    .card-actions {
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 0.5rem;
        margin-top: auto;
    }

    .action-buttons {
        display: flex;
        gap: 0.5rem;
    }

    .btn-icon {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        padding: 0.4rem;
        border-radius: 4px;
        transition: all 0.2s ease;
        
        svg {
            width: 16px;
            height: 16px;
        }

        &:hover {
            transform: translateY(-1px);
        }

        &.btn-danger {
            background-color: var(--danger-light);
            color: var(--danger);
            border: 1px solid var(--danger);
            
            &:hover {
                background-color: var(--danger);
                color: white;
            }
        }

        &.btn-secondary {
            background-color: var(--secondary-light);
            color: var(--secondary);
            border: 1px solid var(--secondary);
            
            &:hover {
                background-color: var(--secondary);
                color: white;
            }
        }
    }

    .sort-container {
        max-width: 600px;
        margin: 0 auto;
    }

    .sort-select {
        width: 100%;
        padding: 0.5rem;
        border: 1px solid var(--primary);
        border-radius: 4px;
        background-color: white;
        cursor: pointer;
        
        &:focus {
            outline: none;
            border-color: var(--primary);
            box-shadow: 0 0 0 2px rgba(var(--primary-rgb), 0.1);
        }
    }
    `]
})
export class PlantCareListComponent implements OnInit {
    systems: PlantCareSystem[] = [];
    filteredSystems: PlantCareSystem[] = [];
    searchTerm: string = '';
    searchId: string = '';
    showDeleteModal = false;
    systemToDelete: PlantCareSystem | null = null;

    sortOptions: SortOption[] = [
        { label: 'ID (Ascending)', value: 'id', direction: 'asc' },
        { label: 'ID (Descending)', value: 'id', direction: 'desc' },
        { label: 'Name (A-Z)', value: 'name', direction: 'asc' },
        { label: 'Name (Z-A)', value: 'name', direction: 'desc' },
        { label: 'Created By (A-Z)', value: 'createdBy', direction: 'asc' },
        { label: 'Created By (Z-A)', value: 'createdBy', direction: 'desc' },
    ];
    
    selectedSort: SortOption = this.sortOptions[0];

    constructor(
        private plantCareService: PlantCareSystemService,
        private router: Router,
        private messageService: MessageService,
        private authService: AuthService
    ) {}

    ngOnInit() {
        this.loadSystems();
    }

    loadSystems() {
        this.plantCareService.getAll().subscribe({
            next: (data) => {
                this.systems = data;
                this.filteredSystems = data;
            },
            error: (error) => {
                console.error('Error loading systems:', error);
                // You might want to add error handling/display here
            }
        });
    }

    deleteSystem(system: PlantCareSystem) {
        this.systemToDelete = system;
        this.showDeleteModal = true;
    }

    confirmDelete() {
        if (this.systemToDelete) {
            this.plantCareService.delete(this.systemToDelete.id).subscribe({
                next: () => {
                    this.messageService.showSuccess('System deleted successfully');
                    this.loadSystems();
                },
                error: (error: HttpErrorResponse) => {
                    if (error.status === 403) {
                        this.messageService.showError('You are not authorized to delete this system');
                    } else {
                        this.messageService.showError('Error deleting system');
                    }
                }
            });
        }
        this.showDeleteModal = false;
        this.systemToDelete = null;
    }

    cancelDelete() {
        this.showDeleteModal = false;
        this.systemToDelete = null;
    }

    editSystem(id: number) {
        this.plantCareService.getById(id).subscribe({
            next: () => {
                this.router.navigate(['/plant-care-systems', id, 'edit']);
            },
            error: (error: HttpErrorResponse) => {
                if (error.status === 403) {
                    this.messageService.showError('You are not authorized to edit this system');
                } else {
                    this.messageService.showError('Error accessing system');
                }
            }
        });
    }

    viewSensors(id: number) {
        this.router.navigate(['/plant-care-systems', id, 'sensors']);
    }

    get isLoggedIn(): boolean {
        return this.authService.isLoggedIn();
    }

    onSearch() {
        this.filteredSystems = this.systems.filter(system => {
            const nameMatch = system.name.toLowerCase().includes(this.searchTerm.toLowerCase());
            const idMatch = this.searchId ? system.id === Number(this.searchId) : true;
            return nameMatch && idMatch;
        });
        this.applySorting();
    }

    clearSearch() {
        this.searchTerm = '';
        this.searchId = '';
        this.filteredSystems = [...this.systems];
        this.applySorting();
    }

    onSort(event: Event) {
        const select = event.target as HTMLSelectElement;
        this.selectedSort = this.sortOptions[select.selectedIndex];
        this.applySorting();
    }

    private applySorting() {
        this.filteredSystems.sort((a, b) => {
            let compareResult = 0;
            
            switch (this.selectedSort.value) {
                case 'id':
                    compareResult = a.id - b.id;
                    break;
                case 'name':
                    compareResult = a.name.localeCompare(b.name);
                    break;
                case 'createdBy':
                    compareResult = a.createdBy.username.localeCompare(b.createdBy.username);
                    break;
            }
            
            return this.selectedSort.direction === 'asc' ? compareResult : -compareResult;
        });
    }
} 
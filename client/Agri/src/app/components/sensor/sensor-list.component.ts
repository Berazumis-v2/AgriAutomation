import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router, ActivatedRoute } from '@angular/router';
import { SensorService } from '../../services/sensor.service';
import { Sensor } from '../../interfaces/sensor.interface';
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
    selector: 'app-sensor-list',
    standalone: true,
    imports: [CommonModule, RouterLink, FormsModule, ConfirmationModalComponent],
    template: `
        <div class="paper container">
            <div class="banner-section">
                <div class="banner-image-container">
                    <img src="sensors.jpg" alt="Agricultural Sensors">
                </div>
                <h2 class="section-title text-center">Sensors for Plant Care System #{{plantCareSystemId}}</h2>
            </div>
            
            <a *ngIf="isLoggedIn" [routerLink]="['new']" class="paper-btn btn-primary btn-block margin-bottom text-center">
                Add New Sensor
            </a>

            <div class="search-container margin-bottom">
                <div class="search-wrapper">
                    <input 
                        type="text" 
                        [(ngModel)]="searchTerm" 
                        (input)="onSearch()"
                        placeholder="Search by model..."
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

            <div *ngIf="filteredSensors.length === 0" class="empty-message">
                <p>No sensors found</p>
                <p *ngIf="searchTerm" class="sub-text">Try adjusting your search term</p>
            </div>

            <div class="grid-container" *ngIf="filteredSensors.length > 0">
                <div class="card sensor-card" *ngFor="let sensor of filteredSensors">
                    <div class="card-body">
                        <div class="card-header">
                            <span class="sensor-id">#{{sensor.id}}</span>
                            <h4 class="card-title">{{sensor.model}}</h4>
                        </div>
                        <div class="readings">
                            <div class="reading-item">
                                <span class="label">Temperature:</span>
                                <span class="value">{{sensor.temperature}}°C</span>
                            </div>
                            <div class="reading-item">
                                <span class="label">Humidity:</span>
                                <span class="value">{{sensor.humidity}}%</span>
                            </div>
                        </div>
                        <div class="details">
                            <p><strong>Last Reading:</strong> {{sensor.readingTimestamp | date:'medium'}}</p>
                            <p><strong>Last Calibration:</strong> {{sensor.calibrationTimestamp | date:'medium'}}</p>
                            <p><strong>Created By:</strong> {{sensor.createdBy.username}}</p>
                        </div>
                        <div class="card-actions">
                            <button class="btn-small" (click)="viewPlants(sensor.id)">Plants</button>
                            <div *ngIf="isLoggedIn" class="action-buttons">
                                <button class="btn-small" (click)="editSensor(sensor.id)" title="Edit">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                                        <path d="M12.146.146a.5.5 0 0 1 .708 0l3 3a.5.5 0 0 1 0 .708l-10 10a.5.5 0 0 1-.168.11l-5 2a.5.5 0 0 1-.65-.65l2-5a.5.5 0 0 1 .11-.168l10-10zM11.207 2.5 13.5 4.793 14.793 3.5 12.5 1.207 11.207 2.5zm1.586 3L10.5 3.207 4 9.707V10h.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.5h.293l6.5-6.5zm-9.761 5.175-.106.106-1.528 3.821 3.821-1.528.106-.106A.5.5 0 0 1 5 12.5V12h-.5a.5.5 0 0 1-.5-.5V11h-.5a.5.5 0 0 1-.468-.325z"/>
                                    </svg>
                                </button>
                                <button class="btn-small btn-danger" (click)="deleteSensor(sensor)" title="Delete">
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

            <button class="paper-btn btn-block margin-top" (click)="backToPlantCareSystems()">
                Back to Plant Care Systems
            </button>

            <app-confirmation-modal
                [show]="showDeleteModal"
                [itemName]="sensorToDelete?.model || ''"
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

        .sensor-card {
            height: 100%;
            display: flex;
            flex-direction: column;
        }

        .card-body {
            flex: 1;
            display: flex;
            flex-direction: column;
        }

        .readings {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 1rem;
            margin: 1rem 0;
            padding: 0.5rem;
            background-color: var(--primary-shaded-70);
            border-radius: 4px;
        }

        .reading-item {
            text-align: center;
            
            .label {
                display: block;
                font-size: 0.875rem;
                color: var(--muted);
            }
            
            .value {
                font-size: 1.25rem;
                font-weight: bold;
            }
        }

        .details {
            margin: 1rem 0;
            flex-grow: 1;
            p {
                margin: 0.25rem 0;
            }
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

        .margin-left {
            margin-left: 1rem;
        }

        @media (max-width: 600px) {
            .grid-container {
                grid-template-columns: 1fr;
            }
        }

        .banner-section {
            margin: -2rem -2rem 2rem -2rem;
            text-align: center;
        }

        .banner-image {
            width: 100%;
            height: 10%;
            object-fit: cover;
        }

        .banner-image-container {
            min-height: 200px;
            display: flex;
            align-items: center;
            justify-content: center;
            background-color: var(--primary-shaded-70);
            padding: 1rem;
            margin: 0 auto;
        }

        .section-title {
            padding: 1rem;
            margin: 0;
            background-color: var(--primary-shaded-70);
            text-align: center;
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

        .sensor-id {
            font-size: 0.75rem;
            color: var(--muted);
            font-weight: normal;
        }

        .card-title {
            margin: 0;
            flex: 1;
        }

        .id-input {
            width: 80px;
            text-align: center;
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
export class SensorListComponent implements OnInit {
    sensors: Sensor[] = [];
    filteredSensors: Sensor[] = [];
    searchTerm: string = '';
    searchId: string = '';
    plantCareSystemId: number;
    showDeleteModal = false;
    sensorToDelete: Sensor | null = null;

    sortOptions: SortOption[] = [
        { label: 'ID (Ascending)', value: 'id', direction: 'asc' },
        { label: 'ID (Descending)', value: 'id', direction: 'desc' },
        { label: 'Model (A-Z)', value: 'model', direction: 'asc' },
        { label: 'Model (Z-A)', value: 'model', direction: 'desc' },
        { label: 'Temperature (Low-High)', value: 'temperature', direction: 'asc' },
        { label: 'Temperature (High-Low)', value: 'temperature', direction: 'desc' },
        { label: 'Humidity (Low-High)', value: 'humidity', direction: 'asc' },
        { label: 'Humidity (High-Low)', value: 'humidity', direction: 'desc' },
        { label: 'Created By (A-Z)', value: 'createdBy', direction: 'asc' },
        { label: 'Created By (Z-A)', value: 'createdBy', direction: 'desc' },
    ];
    
    selectedSort: SortOption = this.sortOptions[0];

    constructor(
        private sensorService: SensorService,
        private router: Router,
        private route: ActivatedRoute,
        private messageService: MessageService,
        private authService: AuthService
    ) {
        this.plantCareSystemId = Number(this.route.snapshot.paramMap.get('plantCareSystemId'));
    }

    ngOnInit() {
        this.loadSensors();
    }

    loadSensors() {
        this.sensorService.getAll(this.plantCareSystemId).subscribe({
            next: (data) => {
                this.sensors = data;
                this.filteredSensors = data;
            },
            error: (error) => {
                console.error('Error loading sensors:', error);
            }
        });
    }

    deleteSensor(sensor: Sensor) {
        this.sensorToDelete = sensor;
        this.showDeleteModal = true;
    }

    confirmDelete() {
        if (this.sensorToDelete) {
            this.sensorService.delete(this.plantCareSystemId, this.sensorToDelete.id).subscribe({
                next: () => {
                    this.loadSensors();
                    this.messageService.showSuccess('Sensor deleted successfully');
                },
                error: (error: HttpErrorResponse) => {
                    if (error.status === 403) {
                        this.messageService.showError('You are not authorized to delete this sensor');
                    } else {
                        this.messageService.showError('Error deleting sensor');
                    }
                }
            });
        }
        this.showDeleteModal = false;
        this.sensorToDelete = null;
    }

    cancelDelete() {
        this.showDeleteModal = false;
        this.sensorToDelete = null;
    }

    editSensor(id: number) {
        this.sensorService.getById(this.plantCareSystemId, id).subscribe({
            next: () => {
                this.router.navigate([id, 'edit'], { relativeTo: this.route });
            },
            error: (error: HttpErrorResponse) => {
                if (error.status === 403) {
                    this.messageService.showError('You are not authorized to edit this sensor');
                } else {
                    this.messageService.showError('Error accessing sensor');
                }
            }
        });
    }

    viewPlants(id: number) {
        this.router.navigate([id, 'plants'], { relativeTo: this.route });
    }

    backToPlantCareSystems() {
        this.router.navigate(['/plant-care-systems']);
    }

    get isLoggedIn(): boolean {
        return this.authService.isLoggedIn();
    }

    onSearch() {
        this.filteredSensors = this.sensors.filter(sensor => {
            const modelMatch = sensor.model.toLowerCase().includes(this.searchTerm.toLowerCase());
            const idMatch = this.searchId ? sensor.id === Number(this.searchId) : true;
            return modelMatch && idMatch;
        });
        this.applySorting();
    }

    clearSearch() {
        this.searchTerm = '';
        this.searchId = '';
        this.filteredSensors = [...this.sensors];
        this.applySorting();
    }

    onSort(event: Event) {
        const select = event.target as HTMLSelectElement;
        this.selectedSort = this.sortOptions[select.selectedIndex];
        this.applySorting();
    }

    private applySorting() {
        this.filteredSensors.sort((a, b) => {
            let compareResult = 0;
            
            switch (this.selectedSort.value) {
                case 'id':
                    compareResult = a.id - b.id;
                    break;
                case 'model':
                    compareResult = a.model.localeCompare(b.model);
                    break;
                case 'temperature':
                    compareResult = a.temperature - b.temperature;
                    break;
                case 'humidity':
                    compareResult = a.humidity - b.humidity;
                    break;
                case 'createdBy':
                    compareResult = a.createdBy.username.localeCompare(b.createdBy.username);
                    break;
            }
            
            return this.selectedSort.direction === 'asc' ? compareResult : -compareResult;
        });
    }
} 
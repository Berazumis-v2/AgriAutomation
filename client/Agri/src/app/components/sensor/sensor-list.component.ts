import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router, ActivatedRoute } from '@angular/router';
import { SensorService } from '../../services/sensor.service';
import { Sensor } from '../../interfaces/sensor.interface';
import { MessageService } from '../../services/message.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
    selector: 'app-sensor-list',
    standalone: true,
    imports: [CommonModule, RouterLink],
    template: `
        <div class="paper container">
            <div class="row">
                <div class="col-fill">
                    <h2>Sensors for Plant Care System #{{plantCareSystemId}}</h2>
                </div>
                <div class="col">
                    <button class="paper-btn" (click)="backToPlantCareSystems()">Back to Plant Care Systems</button>
                    <a [routerLink]="['new']" class="paper-btn btn-primary margin-left">Add New Sensor</a>
                </div>
            </div>

            <div class="row">
                <div class="col-12">
                    <table>
                        <thead>
                            <tr>
                                <th>Model</th>
                                <th>Temperature</th>
                                <th>Humidity</th>
                                <th>Last Reading</th>
                                <th>Last Calibration</th>
                                <th>Created By</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr *ngFor="let sensor of sensors">
                                <td>{{sensor.model}}</td>
                                <td>{{sensor.temperature}}°C</td>
                                <td>{{sensor.humidity}}%</td>
                                <td>{{sensor.readingTimestamp | date:'medium'}}</td>
                                <td>{{sensor.calibrationTimestamp | date:'medium'}}</td>
                                <td>{{sensor.createdBy.username}}</td>
                                <td>
                                    <div class="row flex-edges">
                                        <button class="btn-small" (click)="editSensor(sensor.id)">Edit</button>
                                        <button class="btn-small" (click)="viewPlants(sensor.id)">Plants</button>
                                        <button class="btn-small btn-danger" (click)="deleteSensor(sensor.id)">Delete</button>
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    `,
    styles: [`
        .btn-small {
            padding: 0.2rem 0.4rem;
            margin: 0 0.2rem;
        }
        .margin-left {
            margin-left: 1rem;
        }
    `]
})
export class SensorListComponent implements OnInit {
    sensors: Sensor[] = [];
    plantCareSystemId: number;

    constructor(
        private sensorService: SensorService,
        private router: Router,
        private route: ActivatedRoute,
        private messageService: MessageService
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
            },
            error: (error) => {
                console.error('Error loading sensors:', error);
            }
        });
    }

    deleteSensor(id: number) {
        if (confirm('Are you sure you want to delete this sensor?')) {
            this.sensorService.delete(this.plantCareSystemId, id).subscribe({
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
} 
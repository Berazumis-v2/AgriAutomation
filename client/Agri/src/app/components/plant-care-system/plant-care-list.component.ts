import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { PlantCareSystemService } from '../../services/plant-care-system.service';
import { PlantCareSystem } from '../../interfaces/plant-care-system.interface';

@Component({
    selector: 'app-plant-care-list',
    standalone: true,
    imports: [CommonModule, RouterLink],
    template: `
        <div class="paper container">
            <div class="row">
                <div class="col-fill">
                    <h2>Plant Care Systems</h2>
                </div>
                <div class="col">
                    <a routerLink="new" class="paper-btn btn-primary">Add New System</a>
                </div>
            </div>

            <div class="row">
                <div class="col-12">
                    <table>
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>Description</th>
                                <th>Automation</th>
                                <th>Maintenance Date</th>
                                <th>Created By</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr *ngFor="let system of systems">
                                <td>{{system.name}}</td>
                                <td>{{system.description}}</td>
                                <td>{{system.automationEnabled ? 'Enabled' : 'Disabled'}}</td>
                                <td>{{system.maintenanceTimeStamp | date:'medium'}}</td>
                                <td>{{system.createdBy.username}}</td>
                                <td>
                                    <div class="row flex-edges">
                                        <button class="btn-small" (click)="editSystem(system.id)">Edit</button>
                                        <button class="btn-small" (click)="viewSensors(system.id)">Sensors</button>
                                        <button class="btn-small btn-danger" (click)="deleteSystem(system.id)">Delete</button>
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
    `]
})
export class PlantCareListComponent implements OnInit {
    systems: PlantCareSystem[] = [];

    constructor(
        private plantCareService: PlantCareSystemService,
        private router: Router
    ) {}

    ngOnInit() {
        this.loadSystems();
    }

    loadSystems() {
        this.plantCareService.getAll().subscribe({
            next: (data) => {
                this.systems = data;
            },
            error: (error) => {
                console.error('Error loading systems:', error);
                // You might want to add error handling/display here
            }
        });
    }

    deleteSystem(id: number) {
        if (confirm('Are you sure you want to delete this system?')) {
            this.plantCareService.delete(id).subscribe({
                next: () => {
                    this.loadSystems();
                },
                error: (error) => {
                    console.error('Error deleting system:', error);
                    // Add error handling here
                }
            });
        }
    }

    editSystem(id: number) {
        this.router.navigate(['/plant-care-systems', id, 'edit']);
    }

    viewSensors(id: number) {
        this.router.navigate(['/plant-care-systems', id, 'sensors']);
    }
} 
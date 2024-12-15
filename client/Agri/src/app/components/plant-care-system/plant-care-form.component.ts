import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { PlantCareSystemService } from '../../services/plant-care-system.service';
import { MessageService } from '../../services/message.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
    selector: 'app-plant-care-form',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule],
    template: `
        <div class="paper container">
            <h2>{{isEditing ? 'Edit' : 'Create'}} Plant Care System</h2>
            
            <form [formGroup]="form" (ngSubmit)="onSubmit()" class="form-group">
                <div class="form-group">
                    <label for="name">Name</label>
                    <input 
                        type="text" 
                        id="name" 
                        formControlName="name" 
                        class="input-block">
                    <div class="text-danger" *ngIf="form.get('name')?.touched && form.get('name')?.errors?.['required']">
                        Name is required
                    </div>
                </div>

                <div class="form-group">
                    <label for="description">Description</label>
                    <textarea 
                        id="description" 
                        formControlName="description" 
                        class="input-block">
                    </textarea>
                </div>

                <div class="form-group">
                    <label for="maintenanceTimeStamp">Maintenance Date</label>
                    <input 
                        type="datetime-local" 
                        id="maintenanceTimeStamp" 
                        formControlName="maintenanceTimeStamp" 
                        class="input-block">
                    <div class="text-danger" *ngIf="form.get('maintenanceTimeStamp')?.touched && form.get('maintenanceTimeStamp')?.errors?.['required']">
                        Maintenance date is required
                    </div>
                </div>

                <div class="form-group">
                    <label>
                        <input 
                            type="checkbox" 
                            formControlName="automationEnabled">
                        Enable Automation
                    </label>
                </div>

                <div class="row flex-edges">
                    <button type="button" class="btn-secondary" (click)="goBack()">Cancel</button>
                    <button type="submit" class="btn-primary" [disabled]="!form.valid">
                        {{isEditing ? 'Update' : 'Create'}}
                    </button>
                </div>
            </form>
        </div>
    `
})
export class PlantCareFormComponent implements OnInit {
    form: FormGroup;
    isEditing = false;
    systemId?: number;

    constructor(
        private fb: FormBuilder,
        private plantCareService: PlantCareSystemService,
        private router: Router,
        private route: ActivatedRoute,
        private messageService: MessageService
    ) {
        this.form = this.fb.group({
            name: ['', Validators.required],
            description: [''],
            automationEnabled: [false],
            maintenanceTimeStamp: ['', Validators.required]
        });
    }

    ngOnInit() {
        this.systemId = Number(this.route.snapshot.paramMap.get('id'));
        if (this.systemId) {
            this.isEditing = true;
            this.loadSystem(this.systemId);
        } else {
            const now = new Date();
            now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
            this.form.patchValue({
                maintenanceTimeStamp: now.toISOString().slice(0, 16)
            });
        }
    }

    loadSystem(id: number) {
        this.plantCareService.getById(id).subscribe({
            next: (system) => {
                const maintenanceDate = new Date(system.maintenanceTimeStamp);
                maintenanceDate.setMinutes(maintenanceDate.getMinutes() - maintenanceDate.getTimezoneOffset());
                
                this.form.patchValue({
                    name: system.name,
                    description: system.description,
                    automationEnabled: system.automationEnabled,
                    maintenanceTimeStamp: maintenanceDate.toISOString().slice(0, 16)
                });
            },
            error: (error) => {
                console.error('Error loading system:', error);
            }
        });
    }

    onSubmit() {
        if (this.form.valid) {
            const operation = this.isEditing
                ? this.plantCareService.update(this.systemId!, this.form.value)
                : this.plantCareService.create(this.form.value);

            operation.subscribe({
                next: () => {
                    this.messageService.showSuccess(
                        `System successfully ${this.isEditing ? 'updated' : 'created'}`
                    );
                    this.router.navigate(['/plant-care-systems']);
                },
                error: (error: HttpErrorResponse) => {
                    if (error.status === 403) {
                        this.messageService.showError('You are not authorized to modify this system');
                    } else {
                        this.messageService.showError(`Error ${this.isEditing ? 'updating' : 'creating'} system`);
                    }
                }
            });
        }
    }

    goBack() {
        this.router.navigate(['/plant-care-systems']);
    }
} 
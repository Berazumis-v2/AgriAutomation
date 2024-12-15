import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { SensorService } from '../../services/sensor.service';
import { MessageService } from '../../services/message.service';
import { HttpErrorResponse } from '@angular/common/http';
import { CustomValidators } from '../../validators/custom-validators';

@Component({
    selector: 'app-sensor-form',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule],
    template: `
        <div class="paper container">
            <h2>{{isEditing ? 'Edit' : 'Create'}} Sensor</h2>
            
            <form [formGroup]="form" (ngSubmit)="onSubmit()" class="form-group">
                <div class="form-group">
                    <label for="model">Model</label>
                    <input 
                        type="text" 
                        id="model" 
                        formControlName="model" 
                        class="input-block">
                    <div class="validation-feedback" *ngIf="form.get('model')?.touched">
                        <div *ngIf="form.get('model')?.errors?.['required']" class="text-danger">
                            Model is required
                        </div>
                        <div *ngIf="form.get('model')?.errors?.['minlength'] || form.get('model')?.errors?.['maxlength']" class="text-danger">
                            Model must be between 2 and 50 characters
                        </div>
                    </div>
                </div>

                <div class="form-group">
                    <label for="temperature">Temperature (°C)</label>
                    <input 
                        type="number" 
                        id="temperature" 
                        formControlName="temperature" 
                        class="input-block">
                    <div class="validation-feedback" *ngIf="form.get('temperature')?.touched && form.get('temperature')?.errors?.['temperature']" class="text-danger">
                        {{form.get('temperature')?.errors?.['message']}}
                    </div>
                </div>

                <div class="form-group">
                    <label for="humidity">Humidity (%)</label>
                    <input 
                        type="number" 
                        id="humidity" 
                        formControlName="humidity" 
                        class="input-block">
                    <div class="validation-feedback" *ngIf="form.get('humidity')?.touched && form.get('humidity')?.errors?.['humidity']" class="text-danger">
                        {{form.get('humidity')?.errors?.['message']}}
                    </div>
                </div>

                <div class="form-group">
                    <label for="calibrationTimestamp">Calibration Date</label>
                    <input 
                        type="datetime-local" 
                        id="calibrationTimestamp" 
                        formControlName="calibrationTimestamp" 
                        class="input-block">
                    <div class="text-danger" *ngIf="form.get('calibrationTimestamp')?.touched && form.get('calibrationTimestamp')?.errors?.['required']">
                        Calibration date is required
                    </div>
                </div>

                <div class="row flex-edges">
                    <button type="button" class="btn-secondary" (click)="goBack()">Cancel</button>
                    <button type="submit" class="btn-primary" [disabled]="!form.valid">
                        {{isEditing ? 'Update' : 'Create'}}
                    </button>
                </div>
            </form>
        </div>
    `,
    styles: [`
        .container {
            max-width: 600px;
        }
        .validation-feedback {
            font-size: 0.875rem;
            margin-top: 0.25rem;
        }
        .text-danger {
            color: var(--danger);
        }
    `]
})
export class SensorFormComponent implements OnInit {
    form: FormGroup;
    isEditing = false;
    sensorId?: number;
    plantCareSystemId: number;

    constructor(
        private fb: FormBuilder,
        private sensorService: SensorService,
        private router: Router,
        private route: ActivatedRoute,
        private messageService: MessageService
    ) {
        this.plantCareSystemId = Number(this.route.snapshot.paramMap.get('plantCareSystemId'));
        
        this.form = this.fb.group({
            model: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
            temperature: [0, [CustomValidators.temperature]],
            humidity: [0, [CustomValidators.humidity]],
            calibrationTimestamp: ['', Validators.required]
        });
    }

    ngOnInit() {
        this.sensorId = Number(this.route.snapshot.paramMap.get('id'));
        if (this.sensorId) {
            this.isEditing = true;
            this.loadSensor(this.sensorId);
        } else {
            // Set default calibration timestamp to current date/time for new sensors
            const now = new Date();
            now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
            this.form.patchValue({
                calibrationTimestamp: now.toISOString().slice(0, 16)
            });
        }
    }

    loadSensor(id: number) {
        this.sensorService.getById(this.plantCareSystemId, id).subscribe({
            next: (sensor) => {
                const calibrationDate = new Date(sensor.calibrationTimestamp);
                calibrationDate.setMinutes(calibrationDate.getMinutes() - calibrationDate.getTimezoneOffset());
                
                this.form.patchValue({
                    model: sensor.model,
                    temperature: sensor.temperature,
                    humidity: sensor.humidity,
                    calibrationTimestamp: calibrationDate.toISOString().slice(0, 16)
                });
            },
            error: (error) => {
                console.error('Error loading sensor:', error);
            }
        });
    }

    onSubmit() {
        if (this.form.valid) {
            const operation = this.isEditing
                ? this.sensorService.update(this.plantCareSystemId, this.sensorId!, this.form.value)
                : this.sensorService.create(this.plantCareSystemId, this.form.value);

            operation.subscribe({
                next: () => {
                    this.messageService.showSuccess(
                        `Sensor successfully ${this.isEditing ? 'updated' : 'created'}`
                    );
                    this.goBack();
                },
                error: (error: HttpErrorResponse) => {
                    if (error.status === 403) {
                        this.messageService.showError('You are not authorized to modify this sensor');
                    } else {
                        this.messageService.showError(`Error ${this.isEditing ? 'updating' : 'creating'} sensor`);
                    }
                }
            });
        }
    }

    goBack() {
        this.router.navigate(['/plant-care-systems', this.plantCareSystemId, 'sensors']);
    }
} 
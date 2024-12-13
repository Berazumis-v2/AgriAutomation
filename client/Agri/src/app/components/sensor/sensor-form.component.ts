import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { SensorService } from '../../services/sensor.service';

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
                    <div class="text-danger" *ngIf="form.get('model')?.touched && form.get('model')?.errors?.['required']">
                        Model is required
                    </div>
                </div>

                <div class="form-group">
                    <label for="temperature">Temperature (°C)</label>
                    <input 
                        type="number" 
                        id="temperature" 
                        formControlName="temperature" 
                        class="input-block">
                </div>

                <div class="form-group">
                    <label for="humidity">Humidity (%)</label>
                    <input 
                        type="number" 
                        id="humidity" 
                        formControlName="humidity" 
                        class="input-block">
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
export class SensorFormComponent implements OnInit {
    form: FormGroup;
    isEditing = false;
    sensorId?: number;
    plantCareSystemId: number;

    constructor(
        private fb: FormBuilder,
        private sensorService: SensorService,
        private router: Router,
        private route: ActivatedRoute
    ) {
        this.plantCareSystemId = Number(this.route.snapshot.paramMap.get('plantCareSystemId'));
        
        this.form = this.fb.group({
            model: ['', Validators.required],
            temperature: [0],
            humidity: [0]
        });
    }

    ngOnInit() {
        this.sensorId = Number(this.route.snapshot.paramMap.get('id'));
        if (this.sensorId) {
            this.isEditing = true;
            this.loadSensor(this.sensorId);
        }
    }

    loadSensor(id: number) {
        this.sensorService.getById(this.plantCareSystemId, id).subscribe({
            next: (sensor) => {
                this.form.patchValue({
                    model: sensor.model,
                    temperature: sensor.temperature,
                    humidity: sensor.humidity
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
                    this.goBack();
                },
                error: (error) => {
                    console.error('Error saving sensor:', error);
                }
            });
        }
    }

    goBack() {
        this.router.navigate(['/plant-care-systems', this.plantCareSystemId, 'sensors']);
    }
} 
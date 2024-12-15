import { AbstractControl, ValidationErrors } from '@angular/forms';

export class CustomValidators {
    static temperature(control: AbstractControl): ValidationErrors | null {
        const value = control.value;
        if (value === null || value === '') return null;
        
        if (value < -50 || value > 100) {
            return { temperature: true, message: 'Temperature must be between -50°C and 100°C' };
        }
        return null;
    }

    static humidity(control: AbstractControl): ValidationErrors | null {
        const value = control.value;
        if (value === null || value === '') return null;
        
        if (value < 0 || value > 100) {
            return { humidity: true, message: 'Humidity must be between 0% and 100%' };
        }
        return null;
    }

    static growthStage(control: AbstractControl): ValidationErrors | null {
        const validStages = ['Seedling', 'Vegetative', 'Flowering', 'Fruiting'];
        const value = control.value;
        
        if (!validStages.includes(value)) {
            return { growthStage: true, message: 'Invalid growth stage' };
        }
        return null;
    }
} 
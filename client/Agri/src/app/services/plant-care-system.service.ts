import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PlantCareSystem, PlantCareSystemRequest } from '../interfaces/plant-care-system.interface';

@Injectable({
    providedIn: 'root'
})
export class PlantCareSystemService {
    private apiUrl = `${environment.apiUrl}/plantcaresystems`;

    constructor(private http: HttpClient) {}

    getAll(): Observable<PlantCareSystem[]> {
        return this.http.get<PlantCareSystem[]>(this.apiUrl);
    }

    getById(id: number): Observable<PlantCareSystem> {
        return this.http.get<PlantCareSystem>(`${this.apiUrl}/${id}`);
    }

    create(system: PlantCareSystemRequest): Observable<PlantCareSystem> {
        return this.http.post<PlantCareSystem>(this.apiUrl, system);
    }

    update(id: number, system: PlantCareSystemRequest): Observable<PlantCareSystem> {
        return this.http.put<PlantCareSystem>(`${this.apiUrl}/${id}`, system);
    }

    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
} 
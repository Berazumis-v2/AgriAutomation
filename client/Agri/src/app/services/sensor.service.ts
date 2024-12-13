import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Sensor, SensorRequest } from '../interfaces/sensor.interface';

@Injectable({
    providedIn: 'root'
})
export class SensorService {
    constructor(private http: HttpClient) {}

    getAll(plantCareSystemId: number): Observable<Sensor[]> {
        return this.http.get<Sensor[]>(`${environment.apiUrl}/plantcaresystems/${plantCareSystemId}/sensors`);
    }

    getById(plantCareSystemId: number, sensorId: number): Observable<Sensor> {
        return this.http.get<Sensor>(`${environment.apiUrl}/plantcaresystems/${plantCareSystemId}/sensors/${sensorId}`);
    }

    create(plantCareSystemId: number, sensor: SensorRequest): Observable<Sensor> {
        return this.http.post<Sensor>(`${environment.apiUrl}/plantcaresystems/${plantCareSystemId}/sensors`, sensor);
    }

    update(plantCareSystemId: number, sensorId: number, sensor: SensorRequest): Observable<Sensor> {
        return this.http.put<Sensor>(`${environment.apiUrl}/plantcaresystems/${plantCareSystemId}/sensors/${sensorId}`, sensor);
    }

    delete(plantCareSystemId: number, sensorId: number): Observable<void> {
        return this.http.delete<void>(`${environment.apiUrl}/plantcaresystems/${plantCareSystemId}/sensors/${sensorId}`);
    }
} 
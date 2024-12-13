export interface Sensor {
    id: number;
    model: string;
    temperature: number;
    humidity: number;
    readingTimestamp: string;
    calibrationTimestamp: string;
    plantCareSystemId: number;
    createdBy: {
        username: string;
    };
}

export interface SensorRequest {
    model: string;
    temperature: number;
    humidity: number;
} 
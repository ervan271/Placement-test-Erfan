const API_BASE_URL = 'http://localhost:8080/api';

class APIClient {
    // Medicines
    async getMedicines() {
        try {
            const response = await fetch(`${API_BASE_URL}/medicines`);
            if (!response.ok) throw new Error('Failed to fetch medicines');
            return await response.json();
        } catch (error) {
            console.error('Error:', error);
            throw error;
        }
    }

    async getMedicineById(id) {
        try {
            const response = await fetch(`${API_BASE_URL}/medicines/${id}`);
            if (!response.ok) throw new Error('Failed to fetch medicine');
            return await response.json();
        } catch (error) {
            console.error('Error:', error);
            throw error;
        }
    }

    async addMedicine(medicine) {
        try {
            const response = await fetch(`${API_BASE_URL}/medicines`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(medicine)
            });
            if (!response.ok) throw new Error('Failed to add medicine');
            return await response.json();
        } catch (error) {
            console.error('Error:', error);
            throw error;
        }
    }

    async updateMedicine(id, medicine) {
        try {
            const response = await fetch(`${API_BASE_URL}/medicines/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(medicine)
            });
            if (!response.ok) throw new Error('Failed to update medicine');
            return await response.json();
        } catch (error) {
            console.error('Error:', error);
            throw error;
        }
    }

    async deleteMedicine(id) {
        try {
            const response = await fetch(`${API_BASE_URL}/medicines/${id}`, {
                method: 'DELETE'
            });
            if (!response.ok) throw new Error('Failed to delete medicine');
            return await response.json();
        } catch (error) {
            console.error('Error:', error);
            throw error;
        }
    }

    async getLowStockMedicines() {
        try {
            const response = await fetch(`${API_BASE_URL}/medicines/low-stock`);
            if (!response.ok) throw new Error('Failed to fetch low stock medicines');
            return await response.json();
        } catch (error) {
            console.error('Error:', error);
            throw error;
        }
    }

    async getNearExpiryMedicines() {
        try {
            const response = await fetch(`${API_BASE_URL}/medicines/near-expiry`);
            if (!response.ok) throw new Error('Failed to fetch near expiry medicines');
            return await response.json();
        } catch (error) {
            console.error('Error:', error);
            throw error;
        }
    }

    // Patients
    async getPatients() {
        try {
            const response = await fetch(`${API_BASE_URL}/patients`);
            if (!response.ok) throw new Error('Failed to fetch patients');
            return await response.json();
        } catch (error) {
            console.error('Error:', error);
            throw error;
        }
    }

    async getPatientById(id) {
        try {
            const response = await fetch(`${API_BASE_URL}/patients/${id}`);
            if (!response.ok) throw new Error('Failed to fetch patient');
            return await response.json();
        } catch (error) {
            console.error('Error:', error);
            throw error;
        }
    }

    async addPatient(patient) {
        try {
            const response = await fetch(`${API_BASE_URL}/patients`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(patient)
            });
            if (!response.ok) throw new Error('Failed to add patient');
            return await response.json();
        } catch (error) {
            console.error('Error:', error);
            throw error;
        }
    }

    async updatePatient(id, patient) {
        try {
            const response = await fetch(`${API_BASE_URL}/patients/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(patient)
            });
            if (!response.ok) throw new Error('Failed to update patient');
            return await response.json();
        } catch (error) {
            console.error('Error:', error);
            throw error;
        }
    }

    async deletePatient(id) {
        try {
            const response = await fetch(`${API_BASE_URL}/patients/${id}`, {
                method: 'DELETE'
            });
            if (!response.ok) throw new Error('Failed to delete patient');
            return await response.json();
        } catch (error) {
            console.error('Error:', error);
            throw error;
        }
    }
}

const api = new APIClient();
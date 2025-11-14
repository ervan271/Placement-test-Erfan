let currentEditPatientId = null;

// Load patients
async function loadPatients() {
    try {
        const patients = await api.getPatients();
        displayPatients(patients);
        document.getElementById('patientCount').textContent = patients.length;
    } catch (error) {
        showAlert('Error loading patients', 'error');
    }
}

// Display patients in table
function displayPatients(patients) {
    const tbody = document.getElementById('patientsTableBody');
    tbody.innerHTML = '';

    if (patients.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="empty-state">Tidak ada data pasien</td></tr>';
        return;
    }

    patients.forEach((patient, index) => {
        const row = createPatientRow(patient, index + 1);
        tbody.appendChild(row);
    });
}

// Create patient row
function createPatientRow(patient, number) {
    const row = document.createElement('tr');
    
    row.innerHTML = `
        <td>${number}</td>
        <td>${patient.full_name}</td>
        <td>${patient.phone}</td>
        <td>${patient.address}</td>
        <td>${formatDateTime(patient.created_at)}</td>
        <td>
            <div class="action-buttons">
                <button class="btn-edit" onclick="editPatient(${patient.id})">Ubah</button>
                <button class="btn-delete" onclick="confirmDeletePatient(${patient.id})">Hapus</button>
            </div>
        </td>
    `;

    return row;
}

// Format date time
function formatDateTime(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('id-ID', { 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Format phone number
function formatPhoneNumber(phone) {
    // Remove non-digit characters
    const cleaned = phone.replace(/\D/g, '');
    // Add dashes in format: 62-999-999-9999
    return cleaned.replace(/(\d{2})(\d{3})(\d{3})(\d{4})/, '$1-$2-$3-$4');
}

// Edit patient
async function editPatient(id) {
    try {
        const patient = await api.getPatientById(id);
        
        document.getElementById('patientName').value = patient.full_name;
        document.getElementById('patientPhone').value = patient.phone;
        document.getElementById('patientAddress').value = patient.address;
        
        currentEditPatientId = id;
        document.getElementById('cancelEditPatientBtn').style.display = 'block';
        
        // Scroll to form
        document.querySelectorAll('.form-section')[1].scrollIntoView({ behavior: 'smooth' });
        
        showAlert('Form telah diisi dengan data pasien', 'warning');
    } catch (error) {
        showAlert('Error loading patient data', 'error');
    }
}

// Delete patient with confirmation
function confirmDeletePatient(id) {
    showConfirmModal('Apakah Anda yakin ingin menghapus pasien ini?', () => {
        deletePatient(id);
    });
}

// Delete patient
async function deletePatient(id) {
    try {
        await api.deletePatient(id);
        showAlert('Pasien berhasil dihapus', 'success');
        loadPatients();
    } catch (error) {
        showAlert('Error deleting patient', 'error');
    }
}

// Validate phone format
function validatePhoneFormat(phone) {
    const pattern = /^62-\d{3}-\d{3}-\d{4}$/;
    return pattern.test(phone);
}

// Handle form submit
document.getElementById('patientForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const phone = document.getElementById('patientPhone').value;
    
    if (!validatePhoneFormat(phone)) {
        showAlert('Format nomor telepon tidak valid. Gunakan format: 62-999-999-9999', 'error');
        return;
    }

    const patient = {
        full_name: document.getElementById('patientName').value,
        phone: phone,
        address: document.getElementById('patientAddress').value
    };

    try {
        if (currentEditPatientId) {
            await api.updatePatient(currentEditPatientId, patient);
            showAlert('Pasien berhasil diupdate', 'success');
            currentEditPatientId = null;
        } else {
            await api.addPatient(patient);
            showAlert('Pasien berhasil ditambahkan', 'success');
        }

        document.getElementById('patientForm').reset();
        document.getElementById('cancelEditPatientBtn').style.display = 'none';
        loadPatients();
    } catch (error) {
        showAlert('Error saving patient', 'error');
    }
});

// Cancel edit
document.getElementById('cancelEditPatientBtn').addEventListener('click', () => {
    currentEditPatientId = null;
    document.getElementById('patientForm').reset();
    document.getElementById('cancelEditPatientBtn').style.display = 'none';
    showAlert('Edit dibatalkan', 'warning');
});

// Initialize
loadPatients();
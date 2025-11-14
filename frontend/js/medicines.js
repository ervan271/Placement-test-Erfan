let currentEditMedicineId = null;

// Load medicines
async function loadMedicines() {
    try {
        const medicines = await api.getMedicines();
        displayMedicines(medicines);
        document.getElementById('medicineCount').textContent = medicines.length;
    } catch (error) {
        showAlert('Error loading medicines', 'error');
    }
}

// Display medicines in table
function displayMedicines(medicines) {
    const tbody = document.getElementById('medicinesTableBody');
    tbody.innerHTML = '';

    if (medicines.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" class="empty-state">Tidak ada data obat</td></tr>';
        return;
    }

    medicines.forEach((medicine, index) => {
        const row = createMedicineRow(medicine, index + 1);
        tbody.appendChild(row);
    });
}

// Create medicine row
function createMedicineRow(medicine, number) {
    const row = document.createElement('tr');
    
    // Check stock status
    const stockClass = medicine.stock < medicine.min_stock ? 'stock-danger' : 'stock-ok';
    
    // Check expiry date
    const expiryClass = isNearExpiry(medicine.expiry_date) ? 'expiry-warning' : '';
    
    const statusBadge = medicine.status === 'active' 
        ? '<span class="badge badge-active">ACTIVE</span>' 
        : '<span class="badge badge-inactive">INACTIVE</span>';

    row.innerHTML = `
        <td>${number}</td>
        <td>${medicine.name}</td>
        <td>${medicine.unit}</td>
        <td>Rp ${formatCurrency(medicine.price)}</td>
        <td class="${stockClass}">${medicine.stock}</td>
        <td>${medicine.min_stock}</td>
        <td class="${expiryClass}">${formatDate(medicine.expiry_date)}</td>
        <td>${statusBadge}</td>
        <td>
            <div class="action-buttons">
                <button class="btn-edit" onclick="editMedicine(${medicine.id})">Ubah</button>
                <button class="btn-delete" onclick="confirmDeleteMedicine(${medicine.id})">Hapus</button>
            </div>
        </td>
    `;

    return row;
}

// Check if expiry date is near (within 30 days)
function isNearExpiry(expiryDate) {
    const expiry = new Date(expiryDate);
    const today = new Date();
    const thirtyDaysLater = new Date(today.getTime() + 30 * 24 * 60 * 60 * 1000);
    
    return expiry <= thirtyDaysLater && expiry > today;
}

// Format date
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('id-ID', { 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric' 
    });
}

// Format currency
function formatCurrency(amount) {
    return new Intl.NumberFormat('id-ID').format(amount);
}

// Edit medicine
async function editMedicine(id) {
    try {
        const medicine = await api.getMedicineById(id);
        
        document.getElementById('medicineName').value = medicine.name;
        document.getElementById('medicineUnit').value = medicine.unit;
        document.getElementById('medicinePrice').value = medicine.price;
        document.getElementById('medicineStock').value = medicine.stock;
        document.getElementById('medicineMinStock').value = medicine.min_stock;
        document.getElementById('medicineExpiry').value = medicine.expiry_date;
        document.getElementById('medicineStatus').value = medicine.status;
        
        currentEditMedicineId = id;
        document.getElementById('cancelEditBtn').style.display = 'block';
        
        // Scroll to form
        document.querySelector('.form-section').scrollIntoView({ behavior: 'smooth' });
        
        showAlert('Form telah diisi dengan data obat', 'warning');
    } catch (error) {
        showAlert('Error loading medicine data', 'error');
    }
}

// Delete medicine with confirmation
function confirmDeleteMedicine(id) {
    showConfirmModal('Apakah Anda yakin ingin menghapus obat ini?', () => {
        deleteMedicine(id);
    });
}

// Delete medicine
async function deleteMedicine(id) {
    try {
        await api.deleteMedicine(id);
        showAlert('Obat berhasil dihapus', 'success');
        loadMedicines();
    } catch (error) {
        showAlert('Error deleting medicine', 'error');
    }
}

// Handle form submit
document.getElementById('medicineForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const medicine = {
        name: document.getElementById('medicineName').value,
        unit: document.getElementById('medicineUnit').value,
        price: parseFloat(document.getElementById('medicinePrice').value),
        stock: parseInt(document.getElementById('medicineStock').value),
        min_stock: parseInt(document.getElementById('medicineMinStock').value),
        expiry_date: document.getElementById('medicineExpiry').value,
        status: document.getElementById('medicineStatus').value
    };

    try {
        if (currentEditMedicineId) {
            await api.updateMedicine(currentEditMedicineId, medicine);
            showAlert('Obat berhasil diupdate', 'success');
            currentEditMedicineId = null;
        } else {
            await api.addMedicine(medicine);
            showAlert('Obat berhasil ditambahkan', 'success');
        }

        document.getElementById('medicineForm').reset();
        document.getElementById('medicineMinStock').value = '10';
        document.getElementById('cancelEditBtn').style.display = 'none';
        loadMedicines();
    } catch (error) {
        showAlert('Error saving medicine', 'error');
    }
});

// Cancel edit
document.getElementById('cancelEditBtn').addEventListener('click', () => {
    currentEditMedicineId = null;
    document.getElementById('medicineForm').reset();
    document.getElementById('medicineMinStock').value = '10';
    document.getElementById('cancelEditBtn').style.display = 'none';
    showAlert('Edit dibatalkan', 'warning');
});

// Filter buttons
document.getElementById('showLowStockBtn').addEventListener('click', async () => {
    try {
        const medicines = await api.getLowStockMedicines();
        displayMedicines(medicines);
        showAlert('Menampilkan obat dengan stok rendah', 'warning');
    } catch (error) {
        showAlert('Error loading low stock medicines', 'error');
    }
});

document.getElementById('showNearExpiryBtn').addEventListener('click', async () => {
    try {
        const medicines = await api.getNearExpiryMedicines();
        displayMedicines(medicines);
        showAlert('Menampilkan obat yang hampir kadaluarsa', 'warning');
    } catch (error) {
        showAlert('Error loading near expiry medicines', 'error');
    }
});

document.getElementById('showAllMedicinesBtn').addEventListener('click', () => {
    loadMedicines();
    showAlert('Menampilkan semua obat', 'info');
});

// Initialize
loadMedicines();
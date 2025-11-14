// Navigation
document.querySelectorAll('.nav-link').forEach(link => {
    link.addEventListener('click', (e) => {
        e.preventDefault();
        
        const page = link.getAttribute('data-page');
        
        // Hide all pages
        document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
        
        // Show selected page
        document.getElementById(`${page}-page`).classList.add('active');
        
        // Update nav active state
        document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));
        link.classList.add('active');
    });
});

// Show alert/toast
function showAlert(message, type = 'info') {
    const alertBox = document.getElementById('alertBox');
    alertBox.textContent = message;
    alertBox.className = `alert alert-${type}`;
    alertBox.style.display = 'block';
    
    setTimeout(() => {
        alertBox.style.display = 'none';
    }, 3000);
}


//Modal konfirmasi
function showConfirmModal(message, onConfirm) {
    const modal = document.getElementById('confirmModal');
    const confirmYesBtn = document.getElementById('confirmYesBtn');
    const confirmNoBtn = document.getElementById('confirmNoBtn');
    const confirmMessage = document.getElementById('confirmMessage');

    // Set pesan
    confirmMessage.textContent = message;

    // Tampilkan modal
    modal.classList.add('active');

    // Hapus listener lama dulu supaya tidak menumpuk
    confirmYesBtn.replaceWith(confirmYesBtn.cloneNode(true));
    confirmNoBtn.replaceWith(confirmNoBtn.cloneNode(true));

    // Ambil ulang tombol setelah clone
    const newYesBtn = document.getElementById('confirmYesBtn');
    const newNoBtn = document.getElementById('confirmNoBtn');

    // Fungsi untuk menutup modal
    const closeConfirmModal = () => {
        modal.classList.remove('active');
    };

    // Event handler
    newYesBtn.addEventListener('click', () => {
        onConfirm();
        closeConfirmModal();
    });

    newNoBtn.addEventListener('click', closeConfirmModal);
}

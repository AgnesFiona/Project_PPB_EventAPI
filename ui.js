const tbody = document.getElementById("table-body");
const form = document.getElementById("event-form");

// DEKLARASI VARIABEL FORM UNTUK MENGHINDARI ReferenceError
const eventIdInput = document.getElementById("event-id");
const titleInput = document.getElementById("title");
const dateInput = document.getElementById("date");
const timeInput = document.getElementById("time");
const locationInput = document.getElementById("location");
const statusSelect = document.getElementById("status");
const descriptionTextarea = document.getElementById("description");


/**
 * Memuat semua event dan merender ke dalam tabel
 */
async function loadEvents() {
    // Tampilkan pesan loading
    tbody.innerHTML = "<tr><td colspan='7'>Loading...</td></tr>"; 

    // Ambil data event
    const events = await getAllEvents(); 

    // Kosongkan tabel sebelum diisi
    tbody.innerHTML = ""; 

    if (!events || events.length === 0) {
        tbody.innerHTML = "<tr><td colspan='7'>Tidak ada data event.</td></tr>";
        return;
    }

    // Loop dan tambahkan baris untuk setiap event
    events.forEach(ev => {
        // Karena ev.time dari API mungkin 09:00:00, kita hanya ambil HH:MM
        const timeDisplay = ev.time.substring(0, 5); 

        const row = `
            <tr>
                <td>${ev.id}</td>
                <td>${ev.title}</td>
                <td>${ev.date}</td>
                <td>${timeDisplay}</td>
                <td>${ev.location}</td>
                <td>${ev.status}</td>
                <td>
                    <button onclick='editEvent(${JSON.stringify(ev)})'>Edit</button> 
                    <button onclick='removeEvent(${ev.id})'>Hapus</button>
                </td>
            </tr>
        `;
        tbody.innerHTML += row;
    });
}

/**
 * Mengisi formulir dengan data event yang dipilih untuk diedit
 */
function editEvent(ev) {
    document.getElementById("form-title").innerText = "Edit Event (ID: " + ev.id + ")";
    eventIdInput.value = ev.id; // Menyimpan ID yang akan diedit

    titleInput.value = ev.title;
    dateInput.value = ev.date;
    timeInput.value = ev.time.substring(0, 5); // Ambil HH:MM untuk input type="time"
    locationInput.value = ev.location;
    statusSelect.value = ev.status;
    descriptionTextarea.value = ev.description || "";
}

/**
 * Menghapus event berdasarkan ID
 */
async function removeEvent(id) {
    if (!confirm("Yakin ingin hapus event dengan ID " + id + " ini?")) return;
    
    const res = await deleteEventById(id);
    alert(res.message);
    loadEvents(); // Muat ulang data setelah penghapusan
}

/**
 * Menangani submit form (baik untuk Create maupun Update)
 */
form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const id = eventIdInput.value;
    
    // Siapkan data untuk dikirim ke API
    const data = {
        title: titleInput.value,
        date: dateInput.value,
        time: timeInput.value + ":00", // API membutuhkan HH:MM:SS
        location: locationInput.value,
        status: statusSelect.value,
        description: descriptionTextarea.value
    };

    let res;

    if (id) {
        // Jika ada ID, lakukan UPDATE (PUT)
        res = await updateEvent(id, data); 
    } else {
        // Jika tidak ada ID, lakukan CREATE (POST)
        res = await createEvent(data);
    }

    alert(res.message);
    loadEvents(); // Muat ulang data setelah operasi CRUD
    
    // Bersihkan formulir dan kembalikan judul ke "Tambah Event"
    form.reset(); 
    eventIdInput.value = "";
    document.getElementById("form-title").innerText = "Tambah Event";
});

// Panggil fungsi loadEvents saat pertama kali halaman dimuat
loadEvents();

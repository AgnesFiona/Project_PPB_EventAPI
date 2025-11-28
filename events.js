async function getAllEvents() {
    try {
        const json = await apiGet(); 
        return json.data || []; 
    } catch (error) {
        console.error("Error fetching events:", error);
        alert("Gagal memuat data event. Cek koneksi API.");
        return [];
    }
}

async function createEvent(data) {
    return await apiPost(data);
}

async function updateEvent(id, data) {
    return await apiPut(id, data);
}

async function deleteEventById(id) {
    return await apiDelete(id);
}

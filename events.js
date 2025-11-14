async function getAllEvents() {
  const json = await apiGet();
  return json.data;
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
const API = "http://104.248.153.158/event-api/api.php";

async function apiGet(params = "") {
  const res = await fetch(`${API}${params}`);
  return res.json();
}

async function apiPost(data) {
  const res = await fetch(API, {
    method: "POST",
    headers: {"Content-Type": "application/json"},
    body: JSON.stringify(data)
  });
  return res.json();
}

async function apiPut(id, data) {
  const res = await fetch(`${API}?id=${id}`, {
    method: "PUT",
    headers: {"Content-Type": "application/json"},
    body: JSON.stringify(data)
  });
  return res.json();
}

async function apiDelete(id) {
  const res = await fetch(`${API}?id=${id}`, {
    method: "DELETE"
  });
  return res.json();
}

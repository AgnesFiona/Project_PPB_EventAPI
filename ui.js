const tbody = document.getElementById("table-body");
const form = document.getElementById("event-form");

async function loadEvents() {
  tbody.innerHTML = "<tr><td colspan='7'>Loading...</td></tr>";

  const events = await getAllEvents();

  tbody.innerHTML = "";

  events.forEach(ev => {
    const row = `
        <tr>
            <td>${ev.id}</td>
            <td>${ev.title}</td>
            <td>${ev.date}</td>
            <td>${ev.time}</td>
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

function editEvent(ev) {
  document.getElementById("form-title").innerText = "Edit Event";
  document.getElementById("event-id").value = ev.id;

  document.getElementById("title").value = ev.title;
  document.getElementById("date").value = ev.date;
  document.getElementById("time").value = ev.time;
  document.getElementById("location").value = ev.location;
  document.getElementById("status").value = ev.status;
  document.getElementById("description").value = ev.description || "";
}

async function removeEvent(id) {
  if (!confirm("Yakin ingin hapus event ini?")) return;
  const res = await deleteEventById(id);
  alert(res.message);
  loadEvents();
}

form.addEventListener("submit", async (e) => {
  e.preventDefault();

  const id = document.getElementById("event-id").value;
  const data = {
    title: title.value,
    date: date.value,
    time: time.value,
    location: location.value,
    status: status.value,
    description: description.value
  };

  let res;

  if (id) {
    res = await updateEvent(id, data);
  } else {
    res = await createEvent(data);
  }

  alert(res.message);
  loadEvents();
  form.reset();
});

loadEvents();
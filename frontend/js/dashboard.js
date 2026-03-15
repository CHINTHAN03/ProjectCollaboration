const API_URL = 'http://localhost:7070/api';

const currentUser = localStorage.getItem('currentUser');
const userRole = localStorage.getItem('userRole');

if (!currentUser) window.location.href = 'index.html';
document.getElementById('welcomeMessage').textContent = `User: ${currentUser} | Role: ${userRole}`;

// --- ROLE BASED ACCESS CONTROL (RBAC) ---
if (userRole === "Admin") {
    document.getElementById('adminBtn').classList.remove('hidden');
}
if (userRole === "Developer") {
    document.getElementById('showProjectModalBtn').classList.add('hidden'); // Devs can't create projects
}

function logout() {
    localStorage.clear();
    window.location.href = 'index.html';
}

let currentProjectId = null;
let currentTask = null;

document.addEventListener('DOMContentLoaded', loadProjects);

function showToast(message, isError = false) {
    const container = document.getElementById('toastContainer');
    const toast = document.createElement('div');
    toast.className = 'toast';
    toast.style.backgroundColor = isError ? '#ef4444' : '#1e293b';
    toast.textContent = message;
    container.appendChild(toast);
    setTimeout(() => { toast.remove(); }, 3000);
}

// --- Admin Console Logic ---
async function openAdminConsole() {
    document.getElementById('adminModal').classList.remove('hidden');
    loadAdminUsers();
}

async function wipeDatabase() {
    if(confirm("CRITICAL WARNING: This will permanently delete ALL projects, tasks, and comments to save cluster space. Users will remain. Are you absolutely sure?")) {
        try {
            await fetch(`${API_URL}/admin/wipe`, { method: 'DELETE' });
            showToast("System purged successfully.");
            setTimeout(() => location.reload(), 1500);
        } catch (e) { showToast("Failed to wipe database.", true); }
    }
}

async function loadAdminUsers() {
    const res = await fetch(`${API_URL}/admin/users`);
    const users = await res.json();
    const list = document.getElementById('adminUserList');
    list.innerHTML = '';
    users.forEach(u => {
        list.innerHTML += `
            <div style="display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid var(--border);">
                <span><strong>${u.username}</strong> (${u.role})</span>
                <button onclick="deleteUser('${u.username}')" style="background:none; border:none; color: #ef4444; cursor:pointer; font-weight:bold;">Remove</button>
            </div>
        `;
    });
}

async function deleteUser(username) {
    if(username === currentUser) {
        showToast("You cannot delete yourself.", true);
        return;
    }
    if(confirm(`Remove user ${username} permanently?`)) {
        await fetch(`${API_URL}/admin/users/${username}`, { method: 'DELETE' });
        showToast("User removed.");
        loadAdminUsers();
    }
}

// --- Projects & Analytics ---
async function loadProjects() {
    const res = await fetch(`${API_URL}/projects`);
    const projects = await res.json();
    const select = document.getElementById('projectSelect');
    select.innerHTML = '<option value="">-- Select a Project --</option>';
    projects.forEach(p => select.innerHTML += `<option value="${p.id}">${p.name}</option>`);
}

document.getElementById('projectSelect').addEventListener('change', (e) => {
    currentProjectId = e.target.value;
    if (currentProjectId) {
        document.getElementById('currentProjectTitle').textContent = `Workspace: ${e.target.options[e.target.selectedIndex].text}`;
        document.getElementById('showTaskModalBtn').classList.remove('hidden');
        document.getElementById('kanbanBoard').classList.remove('hidden');
        document.getElementById('analyticsPanel').classList.remove('hidden');
        loadTasks();
        loadAnalytics();
    }
});

async function loadAnalytics() {
    if (!currentProjectId) return;
    const res = await fetch(`${API_URL}/projects/${currentProjectId}/analytics`);
    const metrics = await res.json();
    document.getElementById('statTotal').textContent = metrics.totalTasks;
    document.getElementById('statCompleted').textContent = metrics.completedTasks;
    document.getElementById('progressBar').style.width = `${metrics.completionPercentage}%`;
    document.getElementById('statPercentage').textContent = `${metrics.completionPercentage}% Complete`;
}

// --- Tasks ---
async function loadTasks() {
    if (!currentProjectId) return;
    const res = await fetch(`${API_URL}/projects/${currentProjectId}/tasks`);
    const tasks = await res.json();

    document.querySelectorAll('.task-list').forEach(list => list.innerHTML = '');

    tasks.forEach(task => {
        const targetColumn = document.getElementById(`list-${task.status.replace(/\s+/g, '')}`);
        if (targetColumn) {
            const el = document.createElement('div');
            el.className = 'task-card';
            el.draggable = true;
            el.id = task.id;
            el.ondragstart = drag;
            el.onclick = () => openTaskDetails(task);

            el.innerHTML = `
                <div class="task-title">${task.title}</div>
                <div class="task-meta"><span>${task.assignedTo}</span><span style="color: var(--primary)">${task.status}</span></div>
            `;
            targetColumn.appendChild(el);
        }
    });
}

function drag(ev) { ev.dataTransfer.setData("taskId", ev.target.id); }
function allowDrop(ev) { ev.preventDefault(); }

async function drop(ev, newStatus) {
    ev.preventDefault();
    const taskId = ev.dataTransfer.getData("taskId");
    document.getElementById(`list-${newStatus.replace(/\s+/g, '')}`).appendChild(document.getElementById(taskId));

    await fetch(`${API_URL}/tasks/${taskId}/status`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ status: newStatus })
    });
    showToast(`Task moved to ${newStatus}`);
    loadAnalytics();
}

// --- Forms & Intelligence Engine ---
function closeModals() {
    document.querySelectorAll('.modal').forEach(m => m.classList.add('hidden'));
}

// ---> THE BUG FIX <---
document.getElementById('showProjectModalBtn').onclick = () => {
    document.getElementById('projectModal').classList.remove('hidden');
};

document.getElementById('projectForm').onsubmit = async (e) => {
    e.preventDefault();
    await fetch(`${API_URL}/projects`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            name: document.getElementById('projName').value,
            description: document.getElementById('projDesc').value,
            deadline: document.getElementById('projDeadline').value,
            managerUsername: currentUser,
            teamMembers: [currentUser], // In a real app, this would be a multi-select dropdown
            status: "Active"
        })
    });
    closeModals(); e.target.reset(); loadProjects(); showToast("Project Created!");
};

document.getElementById('showTaskModalBtn').onclick = async () => {
    document.getElementById('taskModal').classList.remove('hidden');
    document.getElementById('recommendationBadge').classList.add('hidden');
    document.getElementById('taskAssignee').value = "Loading recommendation...";

    try {
        const res = await fetch(`${API_URL}/projects/${currentProjectId}/recommend`);
        const data = await res.json();
        document.getElementById('taskAssignee').value = data.recommendedUser;
        document.getElementById('recommendationBadge').classList.remove('hidden');
    } catch(e) { document.getElementById('taskAssignee').value = currentUser; }
};

document.getElementById('taskForm').onsubmit = async (e) => {
    e.preventDefault();
    await fetch(`${API_URL}/tasks`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            projectId: currentProjectId,
            title: document.getElementById('taskTitle').value,
            description: document.getElementById('taskDesc').value,
            assignedTo: document.getElementById('taskAssignee').value,
            priority: document.getElementById('taskPriority').value,
            status: "To Do"
        })
    });
    closeModals(); e.target.reset(); loadTasks(); loadAnalytics(); showToast("Task Created!");
};

// --- Task Details & Comments ---
function openTaskDetails(task) {
    currentTask = task;
    document.getElementById('panelTaskTitle').textContent = task.title;
    document.getElementById('panelTaskStatus').textContent = task.status;
    document.getElementById('panelTaskAssignee').textContent = task.assignedTo;
    document.getElementById('panelTaskDesc').textContent = task.description;

    document.getElementById('taskDetailsPanel').classList.remove('hidden');
    document.getElementById('taskDetailsPanel').style.visibility = 'visible';
    loadComments();
}

function closeTaskDetails() {
    document.getElementById('taskDetailsPanel').classList.add('hidden');
    currentTask = null;
}

async function loadComments() {
    if(!currentTask) return;
    const res = await fetch(`${API_URL}/tasks/${currentTask.id}/comments`);
    const comments = await res.json();

    const feed = document.getElementById('commentsFeed');
    feed.innerHTML = '';

    if(comments.length === 0) {
        feed.innerHTML = '<p style="font-size:12px; color:var(--text-muted)">No comments yet.</p>';
    } else {
        comments.forEach(c => {
            const date = new Date(c.timestamp).toLocaleString();
            feed.innerHTML += `
                <div class="comment">
                    <div class="comment-header"><span class="comment-author">${c.username}</span><span>${date}</span></div>
                    <div>${c.text}</div>
                </div>`;
        });
    }
}

document.getElementById('commentForm').onsubmit = async (e) => {
    e.preventDefault();
    const text = document.getElementById('newCommentText').value;

    await fetch(`${API_URL}/tasks/${currentTask.id}/comments`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: currentUser, text: text })
    });

    document.getElementById('newCommentText').value = '';
    loadComments();
};
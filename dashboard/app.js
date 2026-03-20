const API_BASE = "http://localhost:8080";

// --- STATE MANAGEMENT ---
let currentUser = null;
let token = localStorage.getItem("collabnex_token") || null;

// --- INITIALIZATION ---
document.addEventListener("DOMContentLoaded", () => {
    setupTabListeners();
    checkExistingAuth();
    setupAuthListeners();
    setupFreelancerListeners();
    setupClientListeners();
    setupAdminListeners();
});

// --- HELPER: API WRAPPER ---
async function apiCall(endpoint, method = "GET", body = null, isMultipart = false) {
    const headers = {};
    if (token) headers["Authorization"] = `Bearer ${token}`;
    if (!isMultipart && body) headers["Content-Type"] = "application/json";

    const config = {
        method,
        headers,
        body: isMultipart ? body : (body ? JSON.stringify(body) : null)
    };

    try {
        const response = await fetch(`${API_BASE}${endpoint}`, config);
        
        // Safely parse JSON or return empty object for empty responses
        const text = await response.text();
        let data = {};
        try {
            data = text ? JSON.parse(text) : {};
        } catch (e) {
            console.warn("Received non-JSON response:", text);
            data = { success: response.ok, message: text };
        }
        
        logResponse(data, `${method} ${endpoint}`);

        if (!response.ok) {
            if (response.status === 401 || response.status === 403) {
                showNotif("Auth Error: Check console for details.");
            }
            throw new Error(data.message || "Something went wrong");
        }
        return data.data;
    } catch (err) {
        showNotif(err.message);
        console.error(err);
        return null;
    }
}

function logResponse(data, action) {
    const log = document.getElementById("logContent");
    const timestamp = new Date().toLocaleTimeString();
    log.innerHTML = `[${timestamp}] ${action} -> ${JSON.stringify(data, null, 2)}\n\n` + log.innerHTML;
}

function showNotif(msg) {
    const bar = document.getElementById("notif-bar");
    bar.textContent = msg;
    bar.style.transform = "translateY(0)";
    setTimeout(() => { bar.style.transform = "translateY(100px)"; }, 3000);
}

// --- TAB SYSTEM ---
function setupTabListeners() {
    document.querySelectorAll(".tab-link").forEach(link => {
        link.addEventListener("click", () => {
            const target = link.dataset.tab;
            document.querySelectorAll(".tab-link").forEach(l => l.classList.remove("active"));
            document.querySelectorAll(".tab-pane").forEach(p => p.classList.remove("active"));
            
            link.classList.add("active");
            document.getElementById(target).classList.add("active");
        });
    });
}

// --- AUTH LOGIC ---
function checkExistingAuth() {
    if (token) {
        const storedUser = localStorage.getItem("collabnex_user");
        if (storedUser) {
            currentUser = JSON.parse(storedUser);
            updateUserUI();
        }
    }
}

function updateUserUI() {
    if (currentUser) {
        document.getElementById("currentUser").textContent = `${currentUser.name} (${currentUser.role})`;
        document.getElementById("logoutBtn").style.display = "block";
        if (currentUser.role === "CLIENT") {
            document.getElementById("clientStatusBadge").textContent = currentUser.isPaidMember ? "PAID" : "FREE";
            document.getElementById("clientStatusBadge").className = `badge ${currentUser.isPaidMember ? 'badge-paid' : ''}`;
        }
    } else {
        document.getElementById("currentUser").textContent = "Not Logged In";
        document.getElementById("logoutBtn").style.display = "none";
    }
}

function setupAuthListeners() {
    // Role selection toggle for resume
    document.getElementById("regRole").addEventListener("change", (e) => {
        document.getElementById("resumeGroup").style.display = e.target.value === "FREELANCER" ? "block" : "none";
    });

    // Register
    document.getElementById("registerForm").addEventListener("submit", async (e) => {
        e.preventDefault();
        const formData = new FormData();
        formData.append("name", document.getElementById("regName").value);
        formData.append("email", document.getElementById("regEmail").value);
        formData.append("password", document.getElementById("regPassword").value);
        formData.append("phoneNumber", document.getElementById("regPhone").value);
        formData.append("role", document.getElementById("regRole").value);
        
        const resumeFile = document.getElementById("regResume").files[0];
        if (resumeFile) formData.append("resume", resumeFile);

        const data = await apiCall("/auth/register", "POST", formData, true);
        if (data) {
            token = data.token;
            currentUser = data.user;
            localStorage.setItem("collabnex_token", token);
            localStorage.setItem("collabnex_user", JSON.stringify(currentUser));
            updateUserUI();
            showNotif(`Success! Logged in as ${currentUser.role}`);
        }
    });

    // Login
    document.getElementById("loginForm").addEventListener("submit", async (e) => {
        e.preventDefault();
        const body = {
            email: document.getElementById("loginEmail").value,
            password: document.getElementById("loginPassword").value
        };
        const data = await apiCall("/auth/login", "POST", body);
        if (data) {
            token = data.token;
            currentUser = data.user;
            localStorage.setItem("collabnex_token", token);
            localStorage.setItem("collabnex_user", JSON.stringify(currentUser));
            updateUserUI();
            showNotif("Logged in successfully!");
        }
    });

    // Logout
    document.getElementById("logoutBtn").addEventListener("click", () => {
        token = null;
        currentUser = null;
        localStorage.removeItem("collabnex_token");
        localStorage.removeItem("collabnex_user");
        updateUserUI();
        showNotif("Logged out.");
    });
}

// --- FREELANCER ACTIONS ---
function setupFreelancerListeners() {
    document.getElementById("refreshJobsBtn").addEventListener("click", loadJobsFeed);
}

async function loadJobsFeed() {
    const jobs = await apiCall("/freelancer/jobs");
    const container = document.getElementById("jobFeed");
    container.innerHTML = "";
    
    if (jobs) {
        jobs.forEach(job => {
            const card = document.createElement("div");
            card.className = "item-card";
            card.innerHTML = `
                <div class="item-info">
                    <h4>${job.title} ${job.isPaidClient ? '<span class="badge badge-paid">PAID CLIENT</span>' : ''}</h4>
                    <p>${job.description.substring(0, 100)}...</p>
                    <div class="meta">Posted by ${job.clientName}</div>
                </div>
                <button class="btn btn-secondary" onclick="applyForJob(${job.id})">Apply Now</button>
            `;
            container.appendChild(card);
        });
    }
}

window.applyForJob = async (jobId) => {
    const res = await apiCall(`/freelancer/jobs/${jobId}/apply`, "POST");
    if (res) showNotif("Application sent successfully!");
};

// --- CLIENT ACTIONS ---
function setupClientListeners() {
    document.getElementById("jobForm").addEventListener("submit", async (e) => {
        e.preventDefault();
        const body = {
            title: document.getElementById("jobTitle").value,
            description: document.getElementById("jobDesc").value
        };
        const res = await apiCall("/client/jobs", "POST", body);
        if (res) {
            showNotif("Job posted!");
            document.getElementById("jobForm").reset();
            loadMyJobs();
        }
    });

    // We can auto-load when tab is clicked (simulated)
    document.querySelector('[data-tab="client"]').addEventListener('click', loadMyJobs);
}

async function loadMyJobs() {
    if (!token) return;
    const jobs = await apiCall("/client/jobs");
    const container = document.getElementById("myJobs");
    container.innerHTML = "";
    
    if (jobs) {
        jobs.forEach(job => {
            const card = document.createElement("div");
            card.className = "item-card";
            card.innerHTML = `
                <div class="item-info">
                    <h4>${job.title}</h4>
                    <div class="meta">ID: ${job.id}</div>
                </div>
                <button class="btn btn-secondary" onclick="viewShortlist(${job.id})">View Shortlist</button>
            `;
            container.appendChild(card);
        });
    }
}

window.viewShortlist = async (jobId) => {
    const list = await apiCall(`/client/jobs/${jobId}/shortlisted`);
    if (list) {
        let msg = `Shortlisted Candidates:\n` + list.map(c => `- ${c.name} (${c.email}, ${c.phoneNumber})`).join("\n");
        alert(msg || "No candidates shortlisted yet.");
    }
};

// --- ADMIN ACTIONS ---
let currentJobForAdmin = null;
let selectedCandidates = [];

function setupAdminListeners() {
    document.getElementById("refreshAdminJobsBtn").addEventListener("click", loadAdminJobs);
    document.getElementById("saveShortlistBtn").addEventListener("click", saveShortlist);
}

async function loadAdminJobs() {
    const jobs = await apiCall("/admin/jobs");
    const container = document.getElementById("adminJobs");
    container.innerHTML = "";
    
    if (jobs) {
        jobs.forEach(job => {
            const card = document.createElement("div");
            card.className = "item-card";
            card.innerHTML = `
                <div class="item-info">
                    <h4>${job.title}</h4>
                    <div class="meta">Client: ${job.clientName}</div>
                </div>
                <button class="btn btn-secondary" onclick="viewApplicants(${job.id}, '${job.title}')">View Applicants</button>
            `;
            container.appendChild(card);
        });
    }
}

window.viewApplicants = async (jobId, title) => {
    const apps = await apiCall(`/admin/jobs/${jobId}/applications`);
    currentJobForAdmin = jobId;
    selectedCandidates = [];
    
    document.getElementById("applicantBoard").style.display = "block";
    document.getElementById("currentJobLabel").textContent = `Applicants for ${title}`;
    
    const container = document.getElementById("applicantList");
    container.innerHTML = "";
    
    if (apps) {
        apps.forEach(app => {
            const card = document.createElement("div");
            card.className = "item-card applicant-card";
            card.innerHTML = `
                <div class="applicant-top">
                    <div>
                        <strong>${app.freelancerName}</strong> 
                        ${app.isPaidMember ? '<span class="badge badge-paid">PAID MEMBER</span>' : ''}
                    </div>
                    <label class="cb-label">
                        <input type="checkbox" onchange="toggleSelectCandidate(${app.freelancerId})" /> Shortlist
                    </label>
                </div>
                <div class="meta">Email: ${app.freelancerEmail}</div>
            `;
            container.appendChild(card);
        });
    }
};

window.toggleSelectCandidate = (uid) => {
    if (selectedCandidates.includes(uid)) {
        selectedCandidates = selectedCandidates.filter(id => id !== uid);
    } else {
        selectedCandidates.push(uid);
    }
};

async function saveShortlist() {
    if (selectedCandidates.length < 3 || selectedCandidates.length > 4) {
        return showNotif("Admin Rule: You must select exactly 3 or 4 candidates.");
    }

    const payload = { freelancerIds: selectedCandidates };
    const res = await apiCall(`/admin/jobs/${currentJobForAdmin}/shortlist`, "POST", payload);
    if (res) {
        showNotif("Shortlist saved successfully!");
        document.getElementById("applicantBoard").style.display = "none";
    }
}

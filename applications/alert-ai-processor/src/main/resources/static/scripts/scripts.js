const MOCK_DATA = {
    "activityCount": 4,
    "alertCount": 2,
    "alertRecords": [
        {
            "activities": [
                {
                    "id": "act-101",
                    "account": "user.jdoe@company.com",
                    "icon": "fa-user-shield",
                    "time": "2026-10-19 14:02:11",
                    "activity": "Multiple failed login attempts detected (5 tries in 30s)"
                },
                {
                    "id": "act-102",
                    "account": "user.jdoe@company.com",
                    "icon": "fa-key",
                    "time": "2026-10-19 14:02:45",
                    "activity": "Password reset requested from unrecognized IP (192.168.1.105)"
                }
            ],
            "alerts": [
                {
                    "id": "alt-901",
                    "account": "user.jdoe@company.com",
                    "level": "warning",
                    "time": "2026-10-19 14:03:00",
                    "event": "Suspicious Login Behavior - Possible Brute Force Attempt"
                }
            ]
        },
        {
            "activities": [
                {
                    "id": "act-103",
                    "account": "service-admin@cloud.local",
                    "icon": "fa-database",
                    "time": "2026-10-19 14:05:22",
                    "activity": "Bulk export request issued on DB Table: 'Customer_Credentials'"
                },
                {
                    "id": "act-104",
                    "account": "service-admin@cloud.local",
                    "icon": "fa-network-wired",
                    "time": "2026-10-19 14:05:50",
                    "activity": "Outbound data transfer spike detected (2.4 GB to external host)"
                }
            ],
            "alerts": [
                {
                    "id": "alt-902",
                    "account": "service-admin@cloud.local",
                    "level": "critical",
                    "time": "2026-10-19 14:06:15",
                    "event": "Data Exfiltration Threat Flagged by Anomaly Model"
                }
            ]
        }
    ]
};

let isMockMode = false;
let currentData = null;
let fetchTimer = null;

// Initialization
window.addEventListener('DOMContentLoaded', () => {
    // Initial Fetch
    fetchSummaryData();

    // Read interval selector value and start auto fetch
    const selectedValue = parseInt(document.getElementById('fetchIntervalSelect').value, 10);
    startAutoFetch(selectedValue);

    // Recalculate SVG arrows on window resize
    window.addEventListener('resize', updateSvgConnectors);
});

// Handle interval dropdown selection change
function handleIntervalChange() {
    const selectedValue = parseInt(document.getElementById('fetchIntervalSelect').value, 10);
    startAutoFetch(selectedValue);
}

// Start / Restart Auto Fetch Timer
function startAutoFetch(ms) {
    if (fetchTimer) clearInterval(fetchTimer);

    if (ms > 0) {
        fetchTimer = setInterval(() => {
            fetchSummaryData();
        }, ms);
    }
}

// Manual Fetch wrapper to reset the auto-fetch interval timer on click
function manualFetchData() {
    fetchSummaryData();
    const selectedValue = parseInt(document.getElementById('fetchIntervalSelect').value, 10);
    if (selectedValue > 0) {
        startAutoFetch(selectedValue);
    }
}

// Fetch Function
async function fetchSummaryData() {
    const endpointInput = document.getElementById('endpointUrl').value.trim();
    const refreshIcon = document.getElementById('refreshIcon');

    refreshIcon.classList.add('fa-spin');

    if (isMockMode) {
        setTimeout(() => {
            currentData = MOCK_DATA;
            renderDashboard(currentData);
            refreshIcon.classList.remove('fa-spin');
        }, 300);
        return;
    }

    try {
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), 3500);

        const response = await fetch(endpointInput, {
            method: 'GET',
            headers: { 'Accept': 'application/json' },
            signal: controller.signal
        });

        clearTimeout(timeoutId);

        if (!response.ok) {
            throw new Error(`HTTP Error Status: ${response.status}`);
        }

        const data = await response.json();
        currentData = data;
        updateLiveStatus(true, "Live Server Connected");
        renderDashboard(data);

    } catch (err) {
        console.warn('Live fetch failed/timed out. Falling back to mock data:', err.message);

        currentData = MOCK_DATA;
        updateLiveStatus(false, "Fallback Data (Server Offline)");
        renderDashboard(currentData);
    } finally {
        refreshIcon.classList.remove('fa-spin');
    }
}

function toggleMockMode() {
    isMockMode = !isMockMode;
    const btnText = document.getElementById('mockToggleText');

    if (isMockMode) {
        btnText.textContent = "Using Mock Data";
        updateLiveStatus(false, "Mock Mode Active");
    } else {
        btnText.textContent = "Using Live Mode";
        updateLiveStatus(true, "Live Pipeline Target");
    }
    manualFetchData();
}

function updateLiveStatus(isLive, message) {
    const badge = document.getElementById('liveStatusBadge');
    if (isLive && !isMockMode) {
        badge.className = "text-xs px-2 py-0.5 rounded-full bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 font-medium";
        badge.innerHTML = `<i class="fa-solid fa-circle text-[8px] mr-1 animate-pulse"></i> ${message}`;
    } else {
        badge.className = "text-xs px-2 py-0.5 rounded-full bg-amber-500/10 text-amber-400 border border-amber-500/20 font-medium";
        badge.innerHTML = `<i class="fa-solid fa-triangle-exclamation text-[10px] mr-1"></i> ${message}`;
    }
}

function renderDashboard(data) {
    const activityCount = data?.activityCount ?? 0;
    const alertCount = data?.alertCount ?? 0;
    const records = data?.alertRecords ?? [];

    document.getElementById('metricActivityCount').textContent = activityCount;
    document.getElementById('metricAlertCount').textContent = alertCount;
    document.getElementById('nodeActivityCount').textContent = activityCount;
    document.getElementById('nodeAlertCount').textContent = alertCount;

    setTimeout(updateSvgConnectors, 50);

    renderAlertRecords(records);
}

// Draw and update SVG arrows linking nodes dynamically
function updateSvgConnectors() {
    const nodeSource = document.getElementById('nodeSource');
    const nodeAI = document.getElementById('nodeAI');
    const nodeAlert = document.getElementById('nodeAlert');

    if (window.innerWidth < 768) return;

    const containerRect = document.getElementById('diagramContainer').getBoundingClientRect();

    const r1 = nodeSource.getBoundingClientRect();
    const r2 = nodeAI.getBoundingClientRect();
    const r3 = nodeAlert.getBoundingClientRect();

    const p1Start = { x: r1.right - containerRect.left, y: r1.top + r1.height / 2 - containerRect.top };
    const p1End   = { x: r2.left - containerRect.left, y: r2.top + r2.height / 2 - containerRect.top };

    const p2Start = { x: r2.right - containerRect.left, y: r2.top + r2.height / 2 - containerRect.top };
    const p2End   = { x: r3.left - containerRect.left, y: r3.top + r3.height / 2 - containerRect.top };

    const path1 = document.getElementById('path1');
    const path2 = document.getElementById('path2');

    if (path1 && path2) {
        path1.setAttribute('d', `M ${p1Start.x} ${p1Start.y} L ${p1End.x - 8} ${p1End.y}`);
        path2.setAttribute('d', `M ${p2Start.x} ${p2Start.y} L ${p2End.x - 8} ${p2End.y}`);
    }
}

function renderAlertRecords(records) {
    const container = document.getElementById('recordsListContainer');
    const recordBadge = document.getElementById('recordCountBadge');

    recordBadge.textContent = `${records.length} Record Batch${records.length === 1 ? '' : 'es'}`;
    container.innerHTML = '';

    if (!records || records.length === 0) {
        container.innerHTML = `
                <div class="p-8 text-center bg-slate-900/40 border border-slate-800 rounded-xl text-slate-500">
                    <i class="fa-solid fa-inbox text-3xl mb-2"></i>
                    <p class="text-sm">No alert records available in current payload.</p>
                </div>
            `;
        return;
    }

    records.forEach((record, index) => {
        const card = document.createElement('div');
        card.className = "bg-slate-900/80 border border-slate-800 rounded-xl overflow-hidden shadow-lg transition hover:border-slate-700";

        const activities = record.activities || [];
        const alerts = record.alerts || [];

        card.innerHTML = `
                <div class="bg-slate-800/50 px-5 py-3 border-b border-slate-800 flex items-center justify-between">
                    <div class="flex items-center space-x-2">
                        <span class="w-6 h-6 rounded-full bg-indigo-500/20 text-indigo-400 text-xs flex items-center justify-center font-bold">
                            ${index + 1}
                        </span>
                        <h3 class="text-xs font-semibold text-slate-200">Alert Record Set #${index + 1}</h3>
                    </div>
                    <div class="flex items-center gap-3 text-xs text-slate-400">
                        <span><i class="fa-solid fa-bolt text-blue-400 mr-1"></i>${activities.length} Activities</span>
                        <span><i class="fa-solid fa-triangle-exclamation text-rose-400 mr-1"></i>${alerts.length} Alerts</span>
                    </div>
                </div>

                <div class="p-5 grid grid-cols-1 lg:grid-cols-2 gap-6">
                    <div class="space-y-3">
                        <div class="flex items-center justify-between">
                            <h4 class="text-xs font-bold uppercase tracking-wider text-blue-400 flex items-center gap-1.5">
                                <i class="fa-solid fa-list-ul"></i> Associated Activities
                            </h4>
                        </div>
                        <div class="space-y-2">
                            ${activities.length === 0
            ? '<p class="text-xs text-slate-500 italic">No activity details attached.</p>'
            : activities.map(act => `
                                    <div class="bg-slate-950/60 border border-slate-800/80 rounded-lg p-3 hover:border-blue-500/30 transition text-xs space-y-1">
                                        <div class="flex items-center justify-between">
                                            <span class="font-semibold text-slate-200 flex items-center gap-1.5">
                                                <i class="fa-solid ${getIconClass(act.icon)} text-blue-400"></i>
                                                ${escapeHtml(act.account || 'Unknown Account')}
                                            </span>
                                            <span class="text-[10px] text-slate-500 font-mono">${escapeHtml(act.time || '')}</span>
                                        </div>
                                        <p class="text-slate-300 pl-5">${escapeHtml(act.activity || '')}</p>
                                        <div class="pl-5 text-[10px] text-slate-500 font-mono">ID: ${escapeHtml(act.id || 'N/A')}</div>
                                    </div>
                                `).join('')
        }
                        </div>
                    </div>

                    <div class="space-y-3">
                        <div class="flex items-center justify-between">
                            <h4 class="text-xs font-bold uppercase tracking-wider text-rose-400 flex items-center gap-1.5">
                                <i class="fa-solid fa-bell"></i> Triggered Alerts
                            </h4>
                        </div>
                        <div class="space-y-2">
                            ${alerts.length === 0
            ? '<p class="text-xs text-slate-500 italic">No alerts generated for this record.</p>'
            : alerts.map(alt => {
                const badgeStyle = getAlertBadgeStyle(alt.level);
                return `
                                        <div class="bg-slate-950/60 border border-slate-800/80 rounded-lg p-3 hover:border-rose-500/30 transition text-xs space-y-1">
                                            <div class="flex items-center justify-between">
                                                <span class="font-semibold text-slate-200 flex items-center gap-1.5">
                                                    <i class="fa-solid fa-user text-slate-400"></i>
                                                    ${escapeHtml(alt.account || 'System')}
                                                </span>
                                                <span class="px-2 py-0.5 text-[10px] font-bold uppercase rounded ${badgeStyle}">
                                                    ${escapeHtml(alt.level || 'INFO')}
                                                </span>
                                            </div>
                                            <p class="text-slate-200 font-medium">${escapeHtml(alt.event || '')}</p>
                                            <div class="flex items-center justify-between text-[10px] text-slate-500 font-mono pt-1">
                                                <span>ID: ${escapeHtml(alt.id || 'N/A')}</span>
                                                <span>${escapeHtml(alt.time || '')}</span>
                                            </div>
                                        </div>
                                    `;
            }).join('')
        }
                        </div>
                    </div>
                </div>
            `;
        container.appendChild(card);
    });
}

function getAlertBadgeStyle(level = '') {
    const lvl = level.toLowerCase();
    if (lvl === 'critical' || lvl === 'high') {
        return 'bg-rose-500/20 text-rose-400 border border-rose-500/30';
    } else if (lvl === 'warning' || lvl === 'medium') {
        return 'bg-amber-500/20 text-amber-400 border border-amber-500/30';
    } else {
        return 'bg-blue-500/20 text-blue-400 border border-blue-500/30';
    }
}

function getIconClass(iconStr) {
    if (!iconStr) return 'fa-circle-dot';
    if (iconStr.startsWith('fa-')) return iconStr;
    return `fa-${iconStr}`;
}

function escapeHtml(str) {
    if (typeof str !== 'string') return str;
    return str.replace(/[&<>"']/g, function(m) {
        return {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#039;'
        }[m];
    });
}
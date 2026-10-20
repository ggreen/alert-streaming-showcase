<!DOCTYPE html>
<html lang="en" class="dark">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>AI Agent &amp; Processor</title>
    <!-- Tailwind CSS -->
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="scripts/scripts.js"></script>

    <!-- FontAwesome for icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <!-- Lucide Icons -->
    <script src="https://unpkg.com/lucide@latest"></script>
    <script>
        tailwind.config = {
            darkMode: 'class',
            theme: {
                extend: {
                    colors: {
                        brand: {
                            50: '#f0fdf4',
                            500: '#22c55e',
                            600: '#16a34a',
                            900: '#14532d',
                        },
                        node: {
                            source: '#3b82f6',
                            ai: '#8b5cf6',
                            alert: '#ef4444'
                        }
                    },
                    animation: {
                        'pulse-slow': 'pulse 3s cubic-bezier(0.4, 0, 0.6, 1) infinite',
                        'dash': 'dash 2s linear infinite'
                    },
                    keyframes: {
                        dash: {
                            'to': { 'stroke-dashoffset': '-24' }
                        }
                    }
                }
            }
        }
    </script>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');
        body {
            font-family: 'Inter', sans-serif;
            background-color: #0f172a;
            color: #f8fafc;
        }
        ::-webkit-scrollbar {
            width: 6px;
            height: 6px;
        }
        ::-webkit-scrollbar-track {
            background: #1e293b;
        }
        ::-webkit-scrollbar-thumb {
            background: #475569;
            border-radius: 3px;
        }
        ::-webkit-scrollbar-thumb:hover {
            background: #64748b;
        }
        .flow-path {
            stroke-dasharray: 8, 4;
            animation: dash 1.5s linear infinite;
        }
    </style>
</head>
<body class="min-h-screen bg-slate-950 text-slate-100 flex flex-col font-sans antialiased">

<!-- Header / Controls Bar -->
<header class="border-b border-slate-800 bg-slate-900/80 backdrop-blur-md sticky top-0 z-50 px-4 lg:px-8 py-3">
    <div class="max-w-7xl mx-auto flex flex-col md:flex-row md:items-center justify-between gap-4">
        <!-- Brand Title -->
        <div class="flex items-center space-x-3">
            <div class="p-2 bg-indigo-600/20 rounded-lg border border-indigo-500/30 text-indigo-400">
                <i class="fa-solid font-bold fa-diagram-project text-xl"></i>
            </div>
            <div>
                <h1 class="text-lg font-bold tracking-tight text-white flex items-center gap-2">
                    Data Flow Monitor
                    <span id="liveStatusBadge" class="text-xs px-2 py-0.5 rounded-full bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 font-medium">
                        <i class="fa-solid fa-circle text-[8px] mr-1 animate-pulse"></i> Live Pipeline
                    </span>
                </h1>
                <p class="text-xs text-slate-400">AI Threat & Activity Monitoring Service</p>
            </div>
        </div>

        <!-- Endpoint Configuration & Fetch Actions -->
        <div class="flex flex-wrap items-center gap-2 sm:gap-3">
            <div class="relative flex-1 sm:w-72">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-500 text-xs">
                    <i class="fa-solid fa-link"></i>
                </div>
                <input type="text" id="endpointUrl"
                       value="/alerts/ai/summary"
                       class="w-full pl-8 pr-3 py-1.5 text-xs bg-slate-950 border border-slate-700 rounded-lg text-slate-200 placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition"
                       placeholder="API Endpoint URL">
            </div>

            <!-- Fetch Interval Select -->
            <div class="relative flex items-center">
                <span class="text-xs text-slate-400 mr-1.5 hidden sm:inline"><i class="fa-solid fa-clock text-slate-500 mr-1"></i>Auto:</span>
                <select id="fetchIntervalSelect" onchange="handleIntervalChange()"
                        class="bg-slate-950 border border-slate-700 text-slate-200 text-xs rounded-lg px-2 py-1.5 focus:outline-none focus:ring-2 focus:ring-indigo-500 transition cursor-pointer">
                    <option value="5000">5s</option>
                    <option value="10000" selected>10s</option>
                    <option value="15000">15s</option>
                    <option value="0">Off</option>
                </select>
            </div>

            <button id="fetchBtn" onclick="manualFetchData()"
                    class="px-3 py-1.5 bg-indigo-600 hover:bg-indigo-500 active:bg-indigo-700 text-white text-xs font-semibold rounded-lg shadow-sm transition flex items-center gap-1.5">
                <i class="fa-solid fa-rotate" id="refreshIcon"></i>
                <span>Fetch Data</span>
            </button>
            <div class="h-6 w-[1px] bg-slate-800 hidden sm:block"></div>
            <button id="mockToggleBtn" onclick="toggleMockMode()"
                    class="px-3 py-1.5 bg-slate-800 hover:bg-slate-700 border border-slate-700 text-slate-300 text-xs font-medium rounded-lg transition flex items-center gap-1.5">
                <i class="fa-solid fa-database text-amber-400"></i>
                <span id="mockToggleText">Using Live Mode</span>
            </button>
        </div>
    </div>
</header>

<!-- Main Container -->
<main class="flex-1 max-w-7xl w-full mx-auto p-4 lg:p-8 space-y-8">

    <!-- Top Metrics Cards -->
    <section class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <!-- Activity Summary Card -->
        <div class="bg-slate-900/60 border border-slate-800/80 rounded-xl p-5 flex items-center justify-between backdrop-blur shadow-sm hover:border-blue-500/30 transition">
            <div class="space-y-1">
                <p class="text-xs font-medium text-slate-400 uppercase tracking-wider">Total Activities</p>
                <h3 class="text-3xl font-extrabold text-blue-400" id="metricActivityCount">0</h3>
                <p class="text-[11px] text-slate-500">Ingested events from source</p>
            </div>
            <div class="w-12 h-12 rounded-xl bg-blue-500/10 border border-blue-500/20 flex items-center justify-center text-blue-400 text-xl">
                <i class="fa-solid fa-bolt"></i>
            </div>
        </div>

        <!-- AI Processing Node Status -->
        <div class="bg-slate-900/60 border border-slate-800/80 rounded-xl p-5 flex items-center justify-between backdrop-blur shadow-sm hover:border-purple-500/30 transition">
            <div class="space-y-1">
                <p class="text-xs font-medium text-slate-400 uppercase tracking-wider">AI Processing Status</p>
                <h3 class="text-xl font-bold text-purple-400 flex items-center gap-2">
                    <span>Active Engine</span>
                </h3>
                <p class="text-[11px] text-slate-500" id="aiStatusSubtext">Analyzing events in real-time.</p>
                <span class="text-sm font-bold text-white group-hover:text-blue-300 transition">Filtering ${filterPropName}=${filterValue}</span>
            </div>
            <div class="w-12 h-12 rounded-xl bg-purple-500/10 border border-purple-500/20 flex items-center justify-center text-purple-400 text-xl">
                <i class="fa-solid fa-brain"></i>
            </div>
        </div>

        <!-- Alert Summary Card -->
        <div class="bg-slate-900/60 border border-slate-800/80 rounded-xl p-5 flex items-center justify-between backdrop-blur shadow-sm hover:border-rose-500/30 transition">
            <div class="space-y-1">
                <p class="text-xs font-medium text-slate-400 uppercase tracking-wider">Alerts Generated</p>
                <h3 class="text-3xl font-extrabold text-rose-400" id="metricAlertCount">0</h3>
                <p class="text-[11px] text-slate-500">Threats/Anomalies flagged</p>
            </div>
            <div class="w-12 h-12 rounded-xl bg-rose-500/10 border border-rose-500/20 flex items-center justify-center text-rose-400 text-xl">
                <i class="fa-solid fa-triangle-exclamation"></i>
            </div>
        </div>
    </section>

    <!-- DATA FLOW DIAGRAM SECTION -->
    <section class="bg-slate-900/80 border border-slate-800 rounded-2xl p-6 shadow-xl relative overflow-hidden">
        <div class="flex items-center justify-between mb-6">
            <div>
                <h2 class="text-base font-semibold text-white flex items-center gap-2">
                    <i class="fa-solid fa-route text-indigo-400"></i> Architecture & Data Flow Pipeline
                </h2>
                <p class="text-xs text-slate-400">Visual mapping of stream ingestion through the AI inference layer to alert outputs</p>
            </div>
            <div class="flex items-center gap-2 text-xs text-slate-400">
                <span class="inline-block w-2.5 h-2.5 rounded-full bg-blue-500"></span> Source
                <span class="inline-block w-2.5 h-2.5 rounded-full bg-purple-500 ml-2"></span> AI Layer
                <span class="inline-block w-2.5 h-2.5 rounded-full bg-rose-500 ml-2"></span> Alert
            </div>
        </div>

        <!-- Diagram Container -->
        <div id="diagramContainer" class="relative min-h-[280px] flex flex-col md:flex-row items-center justify-between gap-8 md:gap-4 py-8 px-2 md:px-8">

            <!-- SVG Canvas for Connectors -->
            <svg id="svgConnectors" class="absolute inset-0 w-full h-full pointer-events-none z-0 hidden md:block">
                <defs>
                    <marker id="arrow-blue" viewBox="0 0 10 10" refX="8" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse">
                        <path d="M 0 0 L 10 5 L 0 10 z" fill="#3b82f6"/>
                    </marker>
                    <marker id="arrow-rose" viewBox="0 0 10 10" refX="8" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse">
                        <path d="M 0 0 L 10 5 L 0 10 z" fill="#ef4444"/>
                    </marker>
                    <linearGradient id="grad-source-ai" x1="0%" y1="0%" x2="100%" y2="0%">
                        <stop offset="0%" stop-color="#3b82f6" />
                        <stop offset="100%" stop-color="#8b5cf6" />
                    </linearGradient>
                    <linearGradient id="grad-ai-alert" x1="0%" y1="0%" x2="100%" y2="0%">
                        <stop offset="0%" stop-color="#8b5cf6" />
                        <stop offset="100%" stop-color="#ef4444" />
                    </linearGradient>
                </defs>

                <path id="path1" d="M 0 0 L 0 0" stroke="url(#grad-source-ai)" stroke-width="3" fill="none" class="flow-path" marker-end="url(#arrow-blue)" />
                <path id="path2" d="M 0 0 L 0 0" stroke="url(#grad-ai-alert)" stroke-width="3" fill="none" class="flow-path" marker-end="url(#arrow-rose)" />
            </svg>

            <!-- NODE 1: Activities Source -->
            <div id="nodeSource" class="relative z-10 w-full md:w-72 bg-slate-950/90 border-2 border-blue-500/60 hover:border-blue-400 rounded-xl p-5 shadow-lg transition duration-300 group">
                <div class="flex items-start justify-between">
                    <div class="flex items-center space-x-3">
                        <div class="p-2.5 rounded-lg bg-blue-500/20 text-blue-400 border border-blue-500/30">
                            <i class="fa-solid fa-server text-xl"></i>
                        </div>
                        <div>
                            <h3 class="text-sm font-bold text-white group-hover:text-blue-300 transition">Activities</h3>
                            <p class="text-[11px] text-slate-400">Raw Event Ingestion</p>
                        </div>
                    </div>
                </div>

                <div class="mt-4 pt-3 border-t border-slate-800 flex items-center justify-between">
                    <span class="text-xs text-slate-400">Activity Count</span>
                    <span class="text-sm font-extrabold text-blue-400 bg-blue-500/10 px-2.5 py-0.5 rounded-full border border-blue-500/20" id="nodeActivityCount">0</span>
                </div>

                <div class="absolute -inset-0.5 bg-blue-500/10 rounded-xl blur-sm -z-10 group-hover:bg-blue-500/20 transition"></div>
            </div>

            <!-- Mobile Down Arrow 1 -->
            <div class="block md:hidden text-blue-400 my-[-10px] z-10">
                <i class="fa-solid fa-arrow-down text-xl animate-bounce"></i>
            </div>

            <!-- NODE 2: AI Processor -->
            <div id="nodeAI" class="relative z-10 w-full md:w-72 bg-slate-950/90 border-2 border-purple-500/60 hover:border-purple-400 rounded-xl p-5 shadow-lg transition duration-300 group">
                <div class="flex items-start justify-between">
                    <div class="flex items-center space-x-3">
                        <div class="p-2.5 rounded-lg bg-purple-500/20 text-purple-400 border border-purple-500/30">
                            <i class="fa-solid fa-microchip text-xl animate-pulse"></i>
                        </div>
                        <div>
                            <h3 class="text-sm font-bold text-white group-hover:text-purple-300 transition">AI Agent</h3>
                            <p class="text-[11px] text-slate-400">ML Threat Engine</p>
                        </div>
                    </div>
                </div>

                <div class="mt-4 pt-3 border-t border-slate-800 flex items-center justify-between">
                    <span class="text-xs text-slate-400">Processing Mode</span>
                    <span class="text-xs font-semibold text-purple-300 bg-purple-500/10 px-2 py-0.5 rounded border border-purple-500/20">Real-time LLM</span>
                </div>

                <div class="absolute -inset-0.5 bg-purple-500/10 rounded-xl blur-sm -z-10 group-hover:bg-purple-500/20 transition"></div>
            </div>

            <!-- Mobile Down Arrow 2 -->
            <div class="block md:hidden text-rose-400 my-[-10px] z-10">
                <i class="fa-solid fa-arrow-down text-xl animate-bounce"></i>
            </div>

            <!-- NODE 3: Alert Box -->
            <div id="nodeAlert" class="relative z-10 w-full md:w-72 bg-slate-950/90 border-2 border-rose-500/60 hover:border-rose-400 rounded-xl p-5 shadow-lg transition duration-300 group">
                <div class="flex items-start justify-between">
                    <div class="flex items-center space-x-3">
                        <div class="p-2.5 rounded-lg bg-rose-500/20 text-rose-400 border border-rose-500/30">
                            <i class="fa-solid fa-bell text-xl"></i>
                        </div>
                        <div>
                            <h3 class="text-sm font-bold text-white group-hover:text-rose-300 transition">Alerts</h3>
                            <p class="text-[11px] text-slate-400">Security Notifications</p>
                        </div>
                    </div>
                </div>

                <div class="mt-4 pt-3 border-t border-slate-800 flex items-center justify-between">
                    <span class="text-xs text-slate-400">Alert Count</span>
                    <span class="text-sm font-extrabold text-rose-400 bg-rose-500/10 px-2.5 py-0.5 rounded-full border border-rose-500/20" id="nodeAlertCount">0</span>
                </div>

                <div class="absolute -inset-0.5 bg-rose-500/10 rounded-xl blur-sm -z-10 group-hover:bg-rose-500/20 transition"></div>
            </div>

        </div>
    </section>

    <!-- DETAILED ALERT RECORDS CONTAINER -->
    <section class="space-y-6">
        <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-slate-800 pb-4">
            <div>
                <h2 class="text-lg font-bold text-white flex items-center gap-2">
                    <i class="fa-solid fa-list-check text-indigo-400"></i> Detailed Alert Records & Ingested Activities
                </h2>
                <p class="text-xs text-slate-400">Inspect nested payload payloads processed by the AI pipeline</p>
            </div>
            <div class="flex items-center gap-2">
                <span id="recordCountBadge" class="text-xs font-semibold px-2.5 py-1 rounded-md bg-slate-800 text-slate-300 border border-slate-700">
                    0 Records Loaded
                </span>
            </div>
        </div>

        <div id="recordsListContainer" class="space-y-6">
            <!-- Dynamic Content Injected Here -->
        </div>
    </section>
</main>

<footer class="border-t border-slate-800 bg-slate-900/50 py-6 mt-12 text-center text-xs text-slate-500">
    <p>AI Data Flow & Alert Summary Visualizer &bull; Interconnected Event Pipeline</p>
</footer>

<!-- JAVASCRIPT LOGIC -->
<script>
</script>
</body>
</html>
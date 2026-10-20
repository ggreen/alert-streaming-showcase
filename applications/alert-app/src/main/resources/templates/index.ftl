<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Security Dashboard</title>
    <link rel="stylesheet" href="style.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
    <style>
        /* Optional basic styling for the clear button */
        .btn-clear {
            background-color: #dc3545;
            color: #ffffff;
            border: none;
            padding: 8px 14px;
            font-family: 'Poppins', sans-serif;
            font-size: 0.9rem;
            font-weight: 500;
            border-radius: 6px;
            cursor: pointer;
            display: inline-flex;
            align-items: center;
            gap: 6px;
            transition: background-color 0.2s ease;
        }
        .btn-clear:hover {
            background-color: #bd2130;
        }
        .card-header-actions {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
        }
    </style>
</head>
<body>
<div class="container">
    <header class="header">
        <div class="header-branding">
            <div class="header-icon" aria-hidden="true">
                <i class="fas fa-shield-alt"></i>
            </div>
            <div class="header-title-group">
                <h1>Security & Activity Monitor</h1>
                <p class="header-subtitle">Real-time threat monitoring and event logs</p>
            </div>
        </div>

        <div class="header-actions">
            <!-- Live System Status Badge -->
            <div class="status-badge" role="status" aria-label="System operational">
                <span class="status-dot"></span>
                <span class="status-text">Live</span>
            </div>

            <!-- User Profile Pill -->
            <div class="user-profile" role="button" tabindex="0" aria-label="User alertFilterValue: ${alertFilterValue}, activityFilterValue: ${activityFilterValue}">
                <i class="fas fa-user-circle profile-avatar" aria-hidden="true"></i>
                Filter values &nbsp; <span class="username"> alert = (${alertFilterValue})</span>
                <span class="username">, activities = (${activityFilterValue})</span>
                <i class="fas fa-chevron-down dropdown-arrow" aria-hidden="true"></i>
            </div>
        </div>
    </header>
    <div class="main-dashboard-grid">

        <div class="table-card alert-table-card">
            <div class="card-header-actions">
                <h2 style="margin: 0;"><i class="fas fa-exclamation-triangle"></i> Security Alerts</h2>
                <!-- Clear Button -->
                <button id="clear-btn" class="btn-clear">
                    <i class="fas fa-trash-alt"></i> Clear Logs
                </button>
            </div>

            <!-- SQL Filter Section -->
            <div id="sql-filter-container" class="sql-filter-wrapper" style="display: none;">
                <div class="sql-filter-header">
                    <i class="fas fa-filter"></i>
                    <span>Active Filter</span>
                </div>
                <pre class="sql-code-block"><code id="sql-filter-text"></code></pre>
            </div>

            <div class="table-container">
                <table id="alerts-table">
                    <thead>
                    <tr>
                        <th>Level</th>
                        <th>Time</th>
                        <th>User</th>
                        <th>Event</th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>
        </div>

        <div class="table-card activity-table-card">
            <h2><i class="fas fa-stream"></i> System Activity Log</h2>
            <div class="table-container">
                <table id="activity-table">
                    <thead>
                    <tr>
                        <th>Icon</th>
                        <th>Time</th>
                        <th>Activity</th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>
        </div>

    </div>
</div>

<script>
    $(document).ready(function () {
        const rawSqlFilter = `${alertWhereSqlFilter}`.trim();
        if (rawSqlFilter) {
            $('#sql-filter-text').text(rawSqlFilter);
            $('#sql-filter-container').show();
        }

        // Handle HTTP DELETE request on Clear button click
        $('#clear-btn').on('click', function () {
            if (confirm('Are you sure you want to clear the logs?')) {
                $.ajax({
                    url: '/clear',
                    type: 'DELETE',
                    success: function (response) {
                        alert('Logs cleared successfully.');
                        // Optionally refresh tables or clear rows dynamically:
                        $('#alerts-table tbody').empty();
                        $('#activity-table tbody').empty();
                    },
                    error: function (xhr, status, error) {
                        alert('Failed to clear logs: ' + error);
                    }
                });
            }
        });
    });
</script>
<script src="script.js"></script>
</body>
</html>
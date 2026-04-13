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
</head>
<body>
<div class="container">
    <header class="header">
        <h1>Security & Activity Monitor</h1>
        <div class="user-profile">
            <i class="fas fa-user-circle"></i>
            <span>${account}</span>
        </div>
    </header>

    <div class="main-dashboard-grid">

        <div class="table-card alert-table-card">
            <h2><i class="fas fa-exclamation-triangle"></i> Security Alerts</h2>
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

<script src="script.js"></script>
</body>
</html>
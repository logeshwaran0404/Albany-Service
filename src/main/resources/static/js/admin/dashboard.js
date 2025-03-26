
    // Initialize charts
    document.addEventListener('DOMContentLoaded', function() {
    // Line chart for service trends
    const serviceChartCtx = document.getElementById('serviceChart').getContext('2d');
    const serviceChart = new Chart(serviceChartCtx, {
    type: 'line',
    data: {
    labels: ['Week 1', 'Week 2', 'Week 3', 'Week 4'],
    datasets: [
{
    label: 'Vehicles Due',
    data: [8, 10, 12, 15],
    borderColor: '#722F37',
    backgroundColor: 'rgba(114, 47, 55, 0.1)',
    tension: 0.4,
    fill: true
},
{
    label: 'Completed Services',
    data: [5, 8, 7, 10],
    borderColor: '#8a3943',
    backgroundColor: 'rgba(138, 57, 67, 0.1)',
    tension: 0.4,
    fill: true
}
    ]
},
    options: {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
    legend: {
    position: 'top',
}
},
    scales: {
    y: {
    beginAtZero: true,
    grid: {
    drawBorder: false,
    color: 'rgba(0, 0, 0, 0.05)'
}
},
    x: {
    grid: {
    display: false
}
}
}
}
});

    // Doughnut chart for service distribution
    const distributionChartCtx = document.getElementById('distributionChart').getContext('2d');
    const distributionChart = new Chart(distributionChartCtx, {
    type: 'doughnut',
    data: {
    labels: ['Pending', 'In Progress', 'Completed'],
    datasets: [{
    data: [12, 8, 5],
    backgroundColor: [
    'rgba(234, 179, 8, 0.7)',
    'rgba(59, 130, 246, 0.7)',
    'rgba(16, 185, 129, 0.7)'
    ],
    borderColor: [
    'rgba(234, 179, 8, 1)',
    'rgba(59, 130, 246, 1)',
    'rgba(16, 185, 129, 1)'
    ],
    borderWidth: 1,
    hoverOffset: 4
}]
},
    options: {
    responsive: true,
    maintainAspectRatio: false,
    cutout: '65%',
    plugins: {
    legend: {
    position: 'bottom',
    labels: {
    padding: 15,
    usePointStyle: true,
    pointStyle: 'circle'
}
}
}
}
});

    // Chart filter buttons
    document.querySelectorAll('.chart-filter').forEach(button => {
    button.addEventListener('click', function() {
    const parent = this.closest('.chart-options');
    parent.querySelectorAll('.chart-filter').forEach(btn => {
    btn.classList.remove('active');
});
    this.classList.add('active');
});
});
});

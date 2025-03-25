function loadVehicle(vehicleId) {
    document.getElementById('vehicleId').value = vehicleId;
    // Simulate loading data for the vehicle (e.g., checklist, status) from backend
    console.log(`Loading data for vehicle: ${vehicleId}`);
}

function addServiceItem() {
    const tableBody = document.querySelector('#serviceItemsTable tbody');
    const newRow = `
        <tr>
            <td>
                <select class="form-select" onchange="updateCost(this)">
                    <option value="oil_change">Oil Change</option>
                    <option value="wheel_alignment">Wheel Alignment</option>
                    <option value="fuel_filter">Fuel Filter Replacement</option>
                </select>
            </td>
            <td><input type="number" class="form-control" min="1" value="1"></td>
            <td><input type="text" class="form-control" value="$50" readonly></td>
            <td><button type="button" class="btn btn-danger btn-sm" onclick="removeRow(this)"><i class="bi bi-trash"></i></button></td>
        </tr>
    `;
    tableBody.insertAdjacentHTML('beforeend', newRow);
}

function removeRow(button) {
    button.closest('tr').remove();
}

function updateCost(selectElement) {
    const costInput = selectElement.closest('tr').querySelector('input[type="text"]');
    const item = selectElement.value;
    // Simulate fixed costs (this would come from backend in real app)
    const costs = {
        'oil_change': '$50',
        'wheel_alignment': '$80',
        'fuel_filter': '$30'
    };
    costInput.value = costs[item] || '$0';
}

function saveServiceRecord() {
    const vehicleId = document.getElementById('vehicleId').value;
    const status = document.getElementById('status').value;
    const serviceItems = [];
    document.querySelectorAll('#serviceItemsTable tbody tr').forEach(row => {
        const item = row.querySelector('select').value;
        const quantity = row.querySelector('input[type="number"]').value;
        serviceItems.push({ item, quantity });
    });
    console.log('Saving service record:', { vehicleId, serviceItems, status });
    // Here you would send this data to the backend via an API call
    alert('Service record updated successfully!');
    bootstrap.Modal.getInstance(document.getElementById('updateModal')).hide();
}
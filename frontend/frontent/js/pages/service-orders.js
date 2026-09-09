if (!localStorage.getItem('mc_token')) {
    window.location.href = 'index.html';
    throw new Error('Not authenticated');
}

UI.renderShell('service-orders.html', 'Service Orders');

const role = localStorage.getItem('mc_role');
const isAdmin = role === 'ADMIN';
const isEmployee = role === 'ADMIN' || role === 'USER';

if (!isEmployee) {
    $('#add-btn').remove();
}

const STATUSES = [
    'OPEN',
    'IN_PROGRESS',
    'COMPLETED',
    'CANCELLED'
];

let allOrders = [];
let allCustomers = [];
let allMotorcycles = [];
let allServices = [];
let allSpareParts = [];

const columns = [
    { key: 'id', label: 'ID' },
    { key: 'orderDate', label: 'Date' },
    { key: 'customerName', label: 'Customer' },
    {
        key: 'motorcycleRegistrationNumber',
        label: 'Motorcycle',
        mono: true
    },
    {
        key: 'totalAmount',
        label: 'Total',
        render: r => UI.money(r.totalAmount)
    },
    {
        key: 'status',
        label: 'Status',
        render: r => UI.badge(r.status)
    }
];
function rowActions(row) {

    let html = `
<button
class="btn btn-small"
data-view="${row.id}"
    >
    Items
    </button>
<button
class="btn btn-small"
data-ai-report="${row.id}"
    >
    AI Report
    </button>
        `;

    if (isEmployee) {

        html += `
    <button
class="btn btn-small"
data-status="${row.id}"
    >
    Status
    </button>

<button
    class="btn btn-small btn-danger"
    data-delete="${row.id}"
>
    Delete
</button>
    `;
    }

    return html;
}

function renderList(rows) {

    UI.renderTable(
        '#table-container',
        columns,
        rows,
        rowActions
    );

    $('[data-view]').on('click', function () {
        viewItems(
            Number($(this).data('view'))
        );
    });

    $('[data-ai-report]').on('click', function () {
        generateAiReport(
            Number($(this).data('ai-report'))
        );
    });

    $('[data-status]').on('click', function () {
        openStatusForm(
            Number($(this).data('status'))
        );
    });

    $('[data-delete]').on('click', function () {
        handleDelete(
            Number($(this).data('delete'))
        );
    });
}

function generateAiReport(serviceOrderId) {
    AiApi.generateServiceReport(serviceOrderId)
        .then(report => {
            const html = `
                <div style="line-height:1.55;">
                  <p><strong>${UI.escapeHtml(report.summaryTitle || 'AI Service Report')}</strong></p>
                  <p style="margin-top:12px;">${UI.escapeHtml(report.generatedSummary || '')}</p>
                  <p style="margin-top:12px;"><strong>Recommendation:</strong> ${UI.escapeHtml(report.recommendation || '')}</p>
                  <p style="margin-top:8px;"><strong>Risk Level:</strong> ${UI.escapeHtml(report.riskLevel || '')}</p>
                </div>
            `;
            UI.openDrawer('AI Service Report Summary', html, function () {
                return Promise.resolve();
            }, 'Close');
        })
        .catch(err => UI.toast(err.message || 'Failed to generate AI report', 'error'));
}

function load() {

    ServiceOrdersApi.getAll()
        .then(rows => {

            allOrders = rows;

            renderList(rows);
        })
        .catch(err => {

            UI.toast(
                err.message,
                'error'
            );
        });
}

function viewItems(id) {

    const o = allOrders.find(
        x => x.id === id
    );

    if (!o) return;

    const rowsHtml = (o.items || [])
        .map(it => `
<tr>
<td>
${UI.escapeHtml(it.serviceName)}
</td>

<td>
    ${it.quantity}
</td>

<td>
    ${UI.money(it.unitPrice)}
</td>

<td>
    ${UI.money(it.subtotal)}
</td>
</tr>
`)
        .join('');

    const html = `
<p
style="
color:var(--muted);
font-size:13.5px;
margin-top:0;
"
>
${UI.escapeHtml(o.customerName)}
&middot;
${UI.escapeHtml(o.motorcycleRegistrationNumber)}
&middot;
${UI.badge(o.status)}
</p>

${
        o.remarks
            ? `
                    <p style="font-size:13.5px;">
                        ${UI.escapeHtml(o.remarks)}
                    </p>
                `
            : ''
    }

<table class="data-table">
    <thead>
    <tr>
        <th>Service</th>
        <th>Qty</th>
        <th>Unit price</th>
        <th>Subtotal</th>
    </tr>
    </thead>

    <tbody>
    ${
        rowsHtml ||
        `
                        <tr>
                            <td
                                colspan="4"
                                class="dim"
                            >
                                No items
                            </td>
                        </tr>
                    `
    }
    </tbody>
</table>
    `;

    const sparePartRowsHtml = (o.spareParts || [])
        .map(sp => `
<tr>
<td>
${UI.escapeHtml(sp.sparePartName)}
</td>

<td>
    ${sp.quantity}
</td>

<td>
    ${UI.money(sp.unitPrice)}
</td>

<td>
    ${UI.money(sp.subtotal)}
</td>
</tr>
`)
        .join('');

    const sparePartsHtml = `
<table class="data-table" style="margin-top:16px;">
    <thead>
    <tr>
        <th>Spare part</th>
        <th>Qty</th>
        <th>Unit price</th>
        <th>Subtotal</th>
    </tr>
    </thead>

    <tbody>
    ${
        sparePartRowsHtml ||
        `
                        <tr>
                            <td
                                colspan="4"
                                class="dim"
                            >
                                No spare parts
                            </td>
                        </tr>
                    `
    }
    </tbody>
</table>

<p
    style="
                text-align:right;
                font-weight:700;
                margin-top:12px;
            "
>
    Total: ${UI.money(o.totalAmount)}
</p>
    `;

    UI.openDrawer(
        `Service Order #${o.id}`,
        html + sparePartsHtml,
        () => Promise.resolve(),
        'Close'
    );
}

function customerOptions() {

    return allCustomers
        .map(c => `
<option value="${c.id}">
    ${UI.escapeHtml(c.name)}
(#${c.id})
</option>
    `)
        .join('');
}

function motorcycleOptions() {

    return allMotorcycles
        .map(m => `
<option value="${m.id}">
    ${UI.escapeHtml(m.registrationNumber)}
</option>
`)
        .join('');
}

function serviceOptionsHtml(selectedId) {

    return allServices
        .filter(s => s.active !== false)
        .map(s => `
<option
value="${s.id}"
${
            String(selectedId) === String(s.id)
                ? 'selected'
                : ''
        }
>
${UI.escapeHtml(s.name)}
(${UI.money(s.price).replace(/<[^>]+>/g, '')})
</option>
    `)
        .join('');
}

function sparePartOptionsHtml(selectedId) {

    return allSpareParts
        .map(sp => `
<option
value="${sp.id}"
${
            String(selectedId) === String(sp.id)
                ? 'selected'
                : ''
        }
>
${UI.escapeHtml(sp.name)}
(${UI.money(sp.sellingPrice).replace(/<[^>]+>/g, '')})
</option>
    `)
        .join('');
}

let sparePartRowCounter = 0;

function sparePartRowHtml() {

    sparePartRowCounter++;

    return `
<div
class="item-row"
data-sparepart-row="${sparePartRowCounter}"
    >

    <div
class="form-field"
style="margin-bottom:0;"
    >
    <label>Spare part</label>

<select data-sparepart-select>
    <option value="">
        Select a spare part
    </option>

    ${sparePartOptionsHtml()}
</select>
</div>

<div
    class="form-field"
    style="margin-bottom:0;"
>
    <label>Qty</label>

    <input
        type="number"
        min="1"
        value="1"
        data-sparepart-qty
    >
</div>

<button
    type="button"
    class="remove-item-btn"
    data-remove-sparepart-row="${sparePartRowCounter}"
>
    &times;
</button>

</div>
`;
}

let itemRowCounter = 0;

function itemRowHtml() {

    itemRowCounter++;

    return `
<div
class="item-row"
data-item-row="${itemRowCounter}"
    >

    <div
class="form-field"
style="margin-bottom:0;"
    >
    <label>Service</label>

<select data-item-service>
    <option value="">
        Select a service
    </option>

    ${serviceOptionsHtml()}
</select>
</div>

<div
    class="form-field"
    style="margin-bottom:0;"
>
    <label>Qty</label>

    <input
        type="number"
        min="1"
        value="1"
        data-item-qty
    >
</div>

<button
    type="button"
    class="remove-item-btn"
    data-remove-row="${itemRowCounter}"
>
    &times;
</button>

</div>
`;
}

function openCreateForm() {

    const html = `
<div class="form-field">
    <label>Customer</label>

<select
    data-field="customerId"
    required
>
    <option value="">
        Select a customer
    </option>

    ${customerOptions()}
</select>
</div>

<div class="form-field">
    <label>Motorcycle</label>

    <select
        data-field="motorcycleId"
        required
    >
        <option value="">
            Select a motorcycle
        </option>

        ${motorcycleOptions()}
    </select>
</div>

<div class="form-field">
    <label>Remarks</label>

    <textarea
        data-field="remarks"
    ></textarea>
</div>

<div class="form-field">
    <label>Service items</label>

    <div id="item-rows">
        ${itemRowHtml()}
    </div>

    <button
        type="button"
        class="btn btn-small"
        id="add-item-row"
    >
        + Add another service
    </button>
</div>

<div class="form-field">
    <label>Spare parts (optional)</label>

    <div id="sparepart-rows"></div>

    <button
        type="button"
        class="btn btn-small"
        id="add-sparepart-row"
    >
        + Add spare part
    </button>
</div>
    `;

    UI.openDrawer(
        'New Service Order',
        html,
        values => {

            const items = [];

            $('#item-rows [data-item-row]')
                .each(function () {

                    const serviceId = $(this)
                        .find('[data-item-service]')
                        .val();

                    const quantity = $(this)
                        .find('[data-item-qty]')
                        .val();

                    if (serviceId) {

                        items.push({
                            serviceId: Number(serviceId),
                            quantity: Number(quantity) || 1
                        });
                    }
                });

            if (items.length === 0) {

                return Promise.reject({
                    message: 'Add at least one service item.'
                });
            }

            const spareParts = [];

            $('#sparepart-rows [data-sparepart-row]')
                .each(function () {

                    const sparePartId = $(this)
                        .find('[data-sparepart-select]')
                        .val();

                    const quantity = $(this)
                        .find('[data-sparepart-qty]')
                        .val();

                    if (sparePartId) {

                        spareParts.push({
                            sparePartId: Number(sparePartId),
                            quantity: Number(quantity) || 1
                        });
                    }
                });

            const payload = {
                customerId: Number(values.customerId),
                motorcycleId: Number(values.motorcycleId),
                remarks: values.remarks,
                items: items,
                spareParts: spareParts
            };

            return ServiceOrdersApi
                .create(payload)
                .then(() => {

                    UI.toast(
                        'Service order created.',
                        'success'
                    );

                    load();
                });
        },
        'Create'
    );

    $('#add-item-row').on(
        'click',
        () => {
            $('#item-rows')
                .append(itemRowHtml());
        }
    );

    $('#item-rows').on(
        'click',
        '[data-remove-row]',
        function () {

            if (
                $('#item-rows [data-item-row]').length > 1
            ) {
                $(this)
                    .closest('[data-item-row]')
                    .remove();
            }
        }
    );

    $('#add-sparepart-row').on(
        'click',
        () => {
            $('#sparepart-rows')
                .append(sparePartRowHtml());
        }
    );

    $('#sparepart-rows').on(
        'click',
        '[data-remove-sparepart-row]',
        function () {

            $(this)
                .closest('[data-sparepart-row]')
                .remove();
        }
    );
}

function autoGenerateInvoiceAndPayment(order) {

    return InvoicesApi
        .create({
            customerId: order.customerId,
            serviceOrderId: order.id,
            discount: 0
        })
        .then(invoice => {

            return PaymentsApi
                .create({
                    invoiceId: invoice.id,
                    amount: invoice.totalAmount,
                    paymentMethod: 'CASH',
                    reference: 'Auto-generated on order completion'
                })
                .then(() => {

                    UI.toast(
                        'Invoice ' + invoice.invoiceNumber + ' created and marked as paid.',
                        'success'
                    );
                });
        })
        .catch(err => {

            const msg = (err && err.message) || '';

            if (msg.toLowerCase().includes('invoice already exists')) {
                return;
            }

            UI.toast(
                'Order marked complete, but invoice/payment could not be created: ' + msg,
                'error'
            );
        });
}

function openStatusForm(id) {

    const o = allOrders.find(
        x => x.id === id
    );

    if (!o) return;

    const options = STATUSES
        .map(s => `
<option
value="${s}"
${s === o.status ? 'selected' : ''}
>
${s}
</option>
`)
        .join('');

    const html = `
<div class="form-field">
    <label>New status</label>

<select data-field="status">
    ${options}
</select>
</div>
`;

    UI.openDrawer(
        'Update Order Status',
        html,
        values => {

            return ServiceOrdersApi
                .updateStatus(
                    id,
                    {
                        status: values.status
                    }
                )
                .then(() => {

                    UI.toast(
                        'Status updated.',
                        'success'
                    );

                    if (values.status === 'COMPLETED') {
                        return autoGenerateInvoiceAndPayment(o);
                    }
                })
                .then(() => {
                    load();
                });
        },
        'Update'
    );
}

function handleDelete(id) {

    UI.confirmAction(
        'Delete this service order?'
    )
        .then(ok => {

            if (!ok) return;

            ServiceOrdersApi.delete(id)
                .then(() => {

                    UI.toast(
                        'Service order deleted.',
                        'success'
                    );

                    load();
                })
                .catch(err => {

                    UI.toast(
                        err.message,
                        'error'
                    );
                });
        });
}

$('#add-btn').on(
    'click',
    openCreateForm
);

$('#search-input').on(
    'input',
    function () {

        const q = $(this)
            .val()
            .toLowerCase();

        renderList(
            allOrders.filter(o =>
                (o.customerName || '')
                    .toLowerCase()
                    .includes(q)

                ||

                (o.motorcycleRegistrationNumber || '')
                    .toLowerCase()
                    .includes(q)

                ||

                (o.status || '')
                    .toLowerCase()
                    .includes(q)
            )
        );
    }
);

Promise.all([
    CustomersApi.getAll(),
    MotorcyclesApi.getAll(),
    ServicesApi.getAll(),
    SparePartsApi.getAll()
])
    .then(([customers, motorcycles, services, spareParts]) => {

        allCustomers = customers;
        allMotorcycles = motorcycles;
        allServices = services;
        allSpareParts = spareParts;

        load();
    })
    .catch(err => {

        UI.toast(
            err.message,
            'error'
        );
    });

$('#completed-btn').on('click', function () {
    ServiceOrdersApi.getCompleted()
        .then(function (res) {
            var rows = (res && res.body) ? res.body : res;
            allOrders = Array.isArray(rows) ? rows : (typeof allOrders !== 'undefined' ? allOrders : []);
            if (typeof renderList === 'function') renderList(rows);
            UI.toast('Showing completed service orders', 'success');
        })
        .catch(function (err) {
            UI.toast(err.message || 'Failed to load completed orders', 'error');
        });
});

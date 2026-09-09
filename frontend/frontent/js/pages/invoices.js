if (!localStorage.getItem('mc_token')) {
    window.location.href = 'index.html';
    throw new Error('Not authenticated');
}

UI.renderShell('invoices.html', 'Invoices');

const role = localStorage.getItem('mc_role');
const isAdmin = role === 'ADMIN';

if (!isAdmin) {
    $('#add-btn').remove();
}

let allInvoices = [];
let allCustomers = [];
let allServiceOrders = [];

const columns = [
    { key: 'id', label: 'ID' },
    { key: 'invoiceNumber', label: 'Invoice No.', mono: true },
    { key: 'invoiceDate', label: 'Date' },
    { key: 'customerName', label: 'Customer' },
    {
        key: 'subtotal',
        label: 'Subtotal',
        render: r => UI.money(r.subtotal)
    },
    {
        key: 'discount',
        label: 'Discount',
        render: r => UI.money(r.discount)
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
    if (!isAdmin) return '';

    return `
<button
class="btn btn-small btn-danger"
data-delete="${row.id}"
    >
    Delete
    </button>
        `;
}

function renderList(rows) {
    UI.renderTable(
        '#table-container',
        columns,
        rows,
        rowActions
    );

    $('[data-delete]').on('click', function () {
        handleDelete(
            Number($(this).data('delete'))
        );
    });
}

function load() {
    InvoicesApi.getAll()
        .then(rows => {
            allInvoices = rows;
            renderList(rows);
        })
        .catch(err => {
            UI.toast(err.message, 'error');
        });
}

function customerOptions() {
    return allCustomers
        .map(c => `
    <option value="${c.id}">
    ${UI.escapeHtml(c.name)} (#${c.id})
</option>
    `)
        .join('');
}

function serviceOrderOptions() {
    return allServiceOrders
        .map(o => `
<option value="${o.id}">
    Order #${o.id} —
                ${UI.escapeHtml(o.customerName)}
</option>
`)
        .join('');
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
    <label>Service order</label>

    <select
        data-field="serviceOrderId"
        required
    >
        <option value="">
            Select a service order
        </option>

        ${serviceOrderOptions()}
    </select>
</div>

<div class="form-field">
    <label>Discount</label>

    <input
        type="number"
        step="0.01"
        data-field="discount"
        value="0"
    >
</div>

<p
    id="invoice-total-preview"
    class="hint"
></p>
`;

UI.openDrawer(
'New Invoice',
html,
values => {

return InvoicesApi.create({
    customerId: Number(values.customerId),
    serviceOrderId: Number(values.serviceOrderId),
    discount: values.discount
        ? Number(values.discount)
        : 0
})
    .then(() => {

        UI.toast(
            'Invoice created.',
            'success'
        );

        load();
    });
},
'Create'
);

function updateTotalPreview() {

const orderId = Number(
    $('[data-field="serviceOrderId"]').val()
);

const discount =
    Number(
        $('[data-field="discount"]').val()
    ) || 0;

const order = allServiceOrders.find(
    o => o.id === orderId
);

if (!order) {
    $('#invoice-total-preview').text('');
    return;
}

const total =
    Number(order.totalAmount) - discount;

$('#invoice-total-preview').text(
    `Auto-calculated total: ${
        'Rs. ' +
        total.toLocaleString(
            undefined,
            {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            }
        )
    }`
);
}

$('[data-field="serviceOrderId"]').on(
'change',
function () {

const orderId = Number(
    $(this).val()
);

const order = allServiceOrders.find(
    o => o.id === orderId
);

if (order) {
    $('[data-field="customerId"]')
        .val(String(order.customerId));
}

updateTotalPreview();
}
);

$('[data-field="discount"]').on(
'input',
updateTotalPreview
);
}

function handleDelete(id) {

UI.confirmAction(
    'Delete this invoice?'
)
    .then(ok => {

        if (!ok) return;

        InvoicesApi.delete(id)
            .then(() => {

                UI.toast(
                    'Invoice deleted.',
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
    allInvoices.filter(i =>
        (i.invoiceNumber || '')
            .toLowerCase()
            .includes(q)

        ||

        (i.customerName || '')
            .toLowerCase()
            .includes(q)
    )
);
}
);

Promise.all([
CustomersApi.getAll(),
ServiceOrdersApi.getAll()
])
.then(([customers, orders]) => {

allCustomers = customers;
allServiceOrders = orders;

load();
})
.catch(err => {

UI.toast(
    err.message,
    'error'
);
});


$('#unpaid-btn').on('click', function () {
    InvoicesApi.getUnpaid()
        .then(function (res) {
            var rows = (res && res.body) ? res.body : res;
            allInvoices = Array.isArray(rows) ? rows : [];
            renderList(allInvoices);
            UI.toast('Showing unpaid / partially paid invoices', 'success');
        })
        .catch(function (err) {
            UI.toast(err.message || 'Failed to load unpaid invoices', 'error');
        });
});

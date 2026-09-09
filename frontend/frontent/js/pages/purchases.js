if (!localStorage.getItem('mc_token')) {
    window.location.href = 'index.html';
    throw new Error('Not authenticated');
}

const role = localStorage.getItem('mc_role');

if (role !== 'ADMIN') {
    window.location.href = 'dashboard.html';
    throw new Error('Admin access required');
}

UI.renderShell('purchases.html', 'Purchases');

const STATUSES = ['PENDING', 'RECEIVED', 'CANCELLED'];

let allPurchases = [];
let allSuppliers = [];

const columns = [
    { key: 'id', label: 'ID' },
    { key: 'purchaseDate', label: 'Date' },
    { key: 'supplierName', label: 'Supplier' },
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
    return `
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

function renderList(rows) {

    UI.renderTable(
        '#table-container',
        columns,
        rows,
        rowActions
    );

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

function load() {

    PurchasesApi.getAll()
        .then(rows => {

            allPurchases = rows;

            renderList(rows);
        })
        .catch(err => {

            UI.toast(
                err.message,
                'error'
            );
        });
}

function supplierOptions() {

    return allSuppliers
        .map(s => `
<option value="${s.id}">
    ${UI.escapeHtml(s.name)}
</option>
`)
        .join('');
}

function openCreateForm() {

    const html = `
<div class="form-field">
    <label>Supplier</label>

<select
    data-field="supplierId"
    required
>
    <option value="">
        Select a supplier
    </option>

    ${supplierOptions()}
</select>
</div>

<div class="form-field">
    <label>Total amount</label>

    <input
        type="number"
        step="0.01"
        data-field="totalAmount"
        required
    >
</div>
`;

UI.openDrawer(
'New Purchase',
html,
values => {

return PurchasesApi
    .create({
        supplierId: Number(values.supplierId),
        totalAmount: Number(values.totalAmount)
    })
    .then(() => {

        UI.toast(
            'Purchase recorded.',
            'success'
        );

        load();
    });
},
'Create'
);
}

function openStatusForm(id) {

const p = allPurchases.find(
    x => x.id === id
);

if (!p) return;

const options = STATUSES
    .map(s => `
            <option
                value="${s}"
                ${s === p.status ? 'selected' : ''}
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
    'Update Purchase Status',
    html,
    values => {

        return PurchasesApi
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

                load();
            });
    },
    'Update'
);
}

function handleDelete(id) {

UI.confirmAction(
    'Delete this purchase?'
)
    .then(ok => {

        if (!ok) return;

        PurchasesApi.delete(id)
            .then(() => {

                UI.toast(
                    'Purchase deleted.',
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
    allPurchases.filter(p =>
        (p.supplierName || '')
            .toLowerCase()
            .includes(q)
    )
);
}
);

Promise.all([
SuppliersApi.getAll()
])
.then(([suppliers]) => {

allSuppliers = suppliers;

load();
})
.catch(err => {

UI.toast(
    err.message,
    'error'
);
});

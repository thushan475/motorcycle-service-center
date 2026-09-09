if (!localStorage.getItem('mc_token')) {
    window.location.href = 'index.html';
    throw new Error('Not authenticated');
}

const role = localStorage.getItem('mc_role');

if (role !== 'ADMIN') {
    window.location.href = 'dashboard.html';
    throw new Error('Admin access required');
}

UI.renderShell('inventory.html', 'Inventory');

let allInventory = [];
let allParts = [];

const columns = [
    { key: 'id', label: 'ID' },
    { key: 'partNumber', label: 'Part No.', mono: true },
    { key: 'sparePartName', label: 'Spare Part' },
    { key: 'quantity', label: 'Quantity' },
    {
        key: 'lastUpdated',
        label: 'Last Updated',
        render: r =>
            r.lastUpdated
                ? String(r.lastUpdated).replace('T', ' ').slice(0, 16)
                : ''
    }
];

function rowActions(row) {
    return `
<button class="btn btn-small" data-adjust="${row.id}">
    Adjust Stock
</button>
<button class="btn btn-small btn-danger" data-delete="${row.id}">
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

    $('[data-adjust]').on('click', function () {
        openAdjustForm(Number($(this).data('adjust')));
    });

    $('[data-delete]').on('click', function () {
        handleDelete(Number($(this).data('delete')));
    });
}

function load() {
    InventoryApi.getAll()
        .then(rows => {
            allInventory = rows;
            renderList(rows);
        })
        .catch(err => {
            UI.toast(err.message, 'error');
        });
}

function partOptions() {
    return allParts
        .map(p => `
<option value="${p.id}">
    ${UI.escapeHtml(p.name)}
(${UI.escapeHtml(p.partNumber)})
</option>
    `)
        .join('');
}

function openCreateForm() {

    const html = `
<div class="form-field">
    <label>Spare part</label>

<select data-field="sparePartId" required>
    <option value="">
        Select a spare part
    </option>

    ${partOptions()}
</select>
</div>

<div class="form-field">
    <label>Opening quantity</label>

    <input
        type="number"
        min="0"
        data-field="quantity"
        value="0"
        required
    >
</div>
`;

UI.openDrawer(
'New Stock Line',
html,
values => {

return InventoryApi.create({
    sparePartId: Number(values.sparePartId),
    quantity: Number(values.quantity)
})
    .then(() => {

        UI.toast(
            'Stock line created.',
            'success'
        );

        load();
    });
},
'Create'
);
}

function openAdjustForm(id) {

const inv = allInventory.find(x => x.id === id);

if (!inv) return;

const html = `
        <p style="margin-top:0; color:var(--muted); font-size:13.5px;">
            Current quantity for
            <strong>
                ${UI.escapeHtml(inv.sparePartName)}
            </strong>:
            ${inv.quantity}
        </p>

        <div class="form-field">
            <label>Change</label>

            <input
                type="number"
                data-field="change"
                value="0"
                required
            >

            <span class="hint">
                Use a positive number for stock in
                (e.g. 10), a negative number for stock out
                (e.g. -5).
                Stock can never go below zero.
            </span>
        </div>
    `;

UI.openDrawer(
    'Adjust Stock',
    html,
    values => {

        return InventoryApi.adjust(
            id,
            {
                change: Number(values.change)
            }
        )
            .then(() => {

                UI.toast(
                    'Stock adjusted.',
                    'success'
                );

                load();
            });
    },
    'Apply'
);
}

function handleDelete(id) {

UI.confirmAction(
    'Delete this stock line?'
)
    .then(ok => {

        if (!ok) return;

        InventoryApi.delete(id)
            .then(() => {

                UI.toast(
                    'Stock line deleted.',
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
    allInventory.filter(i =>
        (i.sparePartName || '')
            .toLowerCase()
            .includes(q)

        ||

        (i.partNumber || '')
            .toLowerCase()
            .includes(q)
    )
);
}
);

Promise.all([
SparePartsApi.getAll()
])
.then(([parts]) => {

allParts = parts;

load();
})
.catch(err => {

UI.toast(
    err.message,
    'error'
);
});


$('#low-stock-btn').on('click', function () {
    InventoryApi.getLowStock(10)
        .then(function (res) {
            var rows = (res && res.body) ? res.body : res;
            allInventory = Array.isArray(rows) ? rows : [];
            renderList(allInventory);
            UI.toast('Showing low stock items (qty <= 10)', 'success');
        })
        .catch(function (err) {
            UI.toast(err.message || 'Failed to load low stock', 'error');
        });
});

$('#value-btn').on('click', function () {
    InventoryApi.getValue()
        .then(function (res) {
            var body = (res && res.body) ? res.body : res;
            var val = body && body.totalInventoryValue != null ? body.totalInventoryValue : 0;
            UI.toast('Total inventory value: ' + val, 'success');
        })
        .catch(function (err) {
            UI.toast(err.message || 'Failed to calculate value', 'error');
        });
});


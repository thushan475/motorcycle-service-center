if (!localStorage.getItem('mc_token')) {
    window.location.href = 'index.html';
    throw new Error('Not authenticated');
}

UI.renderShell('motorcycles.html', 'Motorcycles');

const role = localStorage.getItem('mc_role');
const isAdmin = role === 'ADMIN';

let allMotorcycles = [];
let allCustomers = [];
let allBrands = [];

const columns = [
    { key: 'id', label: 'ID' },
    { key: 'registrationNumber', label: 'Reg. No.', mono: true },
    { key: 'motorcycleBrandName', label: 'Brand' },
    { key: 'chassisNumber', label: 'Chassis No.', mono: true },
    { key: 'engineNumber', label: 'Engine No.', mono: true },
    { key: 'year', label: 'Year' },
    { key: 'color', label: 'Color' },
    { key: 'customerName', label: 'Owner' }
];

function rowActions(row) {
    return `
<button
class="btn btn-small"
data-edit="${row.id}"
    >
    Edit
    </button>

<button
    class="btn btn-small btn-danger"
    data-delete="${row.id}"
    data-admin-only
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

    if (!isAdmin) {
        $('[data-admin-only]').remove();
    }

    $('[data-edit]').on('click', function () {
        openEditForm(
            Number($(this).data('edit'))
        );
    });

    $('[data-delete]').on('click', function () {
        handleDelete(
            Number($(this).data('delete'))
        );
    });
}

function load() {

    MotorcyclesApi.getAll()
        .then(rows => {

            allMotorcycles = rows;

            renderList(rows);
        })
        .catch(err => {

            UI.toast(
                err.message,
                'error'
            );
        });
}

function customerOptions(selectedId) {

    return allCustomers
        .map(c => `
<option
value="${c.id}"
${selectedId === c.id ? 'selected' : ''}
>
${UI.escapeHtml(c.name)}
(#${c.id})
</option>
    `)
        .join('');
}

function brandOptions(selectedId) {

    return allBrands
        .map(b => `
<option
value="${b.id}"
${selectedId === b.id ? 'selected' : ''}
>
${UI.escapeHtml(b.name)}
</option>
`)
        .join('');
}

function formHtml(m) {

    m = m || {};

    return `
<div class="form-field">
    <label>Registration number</label>

<input
    type="text"
    data-field="registrationNumber"
    value="${UI.escapeHtml(m.registrationNumber || '')}"
    required
>
</div>

<div class="form-row">

    <div class="form-field">
        <label>Chassis number</label>

        <input
            type="text"
            data-field="chassisNumber"
            value="${UI.escapeHtml(m.chassisNumber || '')}"
            required
        >
    </div>

    <div class="form-field">
        <label>Engine number</label>

        <input
            type="text"
            data-field="engineNumber"
            value="${UI.escapeHtml(m.engineNumber || '')}"
            required
        >
    </div>

</div>

<div class="form-row">

    <div class="form-field">
        <label>Year</label>

        <input
            type="number"
            data-field="year"
            value="${m.year || ''}"
            required
        >
    </div>

    <div class="form-field">
        <label>Color</label>

        <input
            type="text"
            data-field="color"
            value="${UI.escapeHtml(m.color || '')}"
        >
    </div>

</div>

<div class="form-field">
    <label>Owner (customer)</label>

    <select
        data-field="customerId"
        required
    >
        <option value="">
            Select a customer
        </option>

        ${customerOptions(m.customerId)}
    </select>
</div>

<div class="form-field">
    <label>Brand</label>

    <select
        data-field="motorcycleBrandId"
        required
    >
        <option value="">
            Select a brand
        </option>

        ${brandOptions(m.motorcycleBrandId)}
    </select>
</div>
`;
}

function toPayload(values) {

return {
    registrationNumber: values.registrationNumber,
    chassisNumber: values.chassisNumber,
    engineNumber: values.engineNumber,
    year: Number(values.year),
    color: values.color,
    customerId: Number(values.customerId),
    motorcycleBrandId: Number(values.motorcycleBrandId)
};
}

function openCreateForm() {

UI.openDrawer(
    'New Motorcycle',
    formHtml(),
    values => {

        return MotorcyclesApi
            .create(toPayload(values))
            .then(() => {

                UI.toast(
                    'Motorcycle added.',
                    'success'
                );

                load();
            });
    },
    'Create'
);
}

function openEditForm(id) {

const m = allMotorcycles.find(
    x => x.id === id
);

if (!m) return;

UI.openDrawer(
    'Edit Motorcycle',
    formHtml(m),
    values => {

        return MotorcyclesApi
            .update(
                id,
                toPayload(values)
            )
            .then(() => {

                UI.toast(
                    'Motorcycle updated.',
                    'success'
                );

                load();
            });
    },
    'Save changes'
);
}

function handleDelete(id) {

const m = allMotorcycles.find(
    x => x.id === id
);

UI.confirmAction(
    `Delete motorcycle "${m ? m.registrationNumber : id}"?`
)
    .then(ok => {

        if (!ok) return;

        MotorcyclesApi.delete(id)
            .then(() => {

                UI.toast(
                    'Motorcycle deleted.',
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
    allMotorcycles.filter(m =>
        (m.registrationNumber || '')
            .toLowerCase()
            .includes(q)

        ||

        (m.customerName || '')
            .toLowerCase()
            .includes(q)

        ||

        (m.motorcycleBrandName || '')
            .toLowerCase()
            .includes(q)
    )
);
}
);

Promise.all([
CustomersApi.getAll(),
BrandsApi.getAll()
])
.then(([customers, brands]) => {

allCustomers = customers;
allBrands = brands;

load();
})
.catch(err => {

UI.toast(
    err.message,
    'error'
);
});

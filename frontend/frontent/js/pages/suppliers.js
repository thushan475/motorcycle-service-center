if (!localStorage.getItem('mc_token')) {
    window.location.href = 'index.html';
    throw new Error('Not authenticated');
}

const role = localStorage.getItem('mc_role');

if (role !== 'ADMIN') {
    window.location.href = 'dashboard.html';
    throw new Error('Admin access required');
}

UI.renderShell('suppliers.html', 'Suppliers');

let allSuppliers = [];

const columns = [
    { key: 'id', label: 'ID' },
    { key: 'name', label: 'Name' },
    { key: 'email', label: 'Email' },
    { key: 'phone', label: 'Phone' },
    { key: 'address', label: 'Address' }
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

    $('[data-edit]').on('click', function () {
        openEditForm(Number($(this).data('edit')));
    });

    $('[data-delete]').on('click', function () {
        handleDelete(Number($(this).data('delete')));
    });
}

function load() {
    SuppliersApi.getAll()
        .then(rows => {
            allSuppliers = rows;
            renderList(rows);
        })
        .catch(err => {
            UI.toast(err.message, 'error');
        });
}

function formHtml(s) {
    s = s || {};

    return `
<div class="form-field">
    <label>Name</label>
<input
    type="text"
    data-field="name"
    value="${UI.escapeHtml(s.name || '')}"
    required
>
</div>

<div class="form-field">
    <label>Email</label>
    <input
        type="email"
        data-field="email"
        value="${UI.escapeHtml(s.email || '')}"
    >
</div>

<div class="form-field">
    <label>Phone</label>
    <input
        type="text"
        data-field="phone"
        value="${UI.escapeHtml(s.phone || '')}"
        required
    >
</div>

<div class="form-field">
    <label>Address</label>
    <textarea data-field="address">${UI.escapeHtml(
        s.address || ''
    )}</textarea>
</div>
`;
}

function openCreateForm() {
UI.openDrawer(
    'New Supplier',
    formHtml(),
    values => {
        return SuppliersApi
            .create(values)
            .then(() => {
                UI.toast(
                    'Supplier created.',
                    'success'
                );

                load();
            });
    },
    'Create'
);
}

function openEditForm(id) {
const s = allSuppliers.find(x => x.id === id);

if (!s) return;

UI.openDrawer(
    'Edit Supplier',
    formHtml(s),
    values => {
        return SuppliersApi
            .update(id, values)
            .then(() => {
                UI.toast(
                    'Supplier updated.',
                    'success'
                );

                load();
            });
    },
    'Save changes'
);
}

function handleDelete(id) {
const s = allSuppliers.find(x => x.id === id);

UI.confirmAction(
    `Delete supplier "${s ? s.name : id}"?`
)
    .then(ok => {
        if (!ok) return;

        SuppliersApi
            .delete(id)
            .then(() => {
                UI.toast(
                    'Supplier deleted.',
                    'success'
                );

                load();
            })
            .catch(err => {
                UI.toast(err.message, 'error');
            });
    });
}

$('#add-btn').on('click', openCreateForm);

$('#search-input').on('input', function () {
const q = $(this)
    .val()
    .toLowerCase();

renderList(
    allSuppliers.filter(s =>
        (s.name || '')
            .toLowerCase()
            .includes(q)
    )
);
});

load();

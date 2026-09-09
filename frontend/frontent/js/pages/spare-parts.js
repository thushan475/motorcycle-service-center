if (!localStorage.getItem('mc_token')) {
    window.location.href = 'index.html';
    throw new Error('Not authenticated');
}

UI.renderShell('spare-parts.html', 'Spare Parts');

const role = localStorage.getItem('mc_role');
const isAdmin = role === 'ADMIN';

if (!isAdmin) {
    $('#add-btn').remove();
}

let allParts = [];

const columns = [
    { key: 'id', label: 'ID' },
    { key: 'partNumber', label: 'Part No.', mono: true },
    { key: 'name', label: 'Name' },
    { key: 'description', label: 'Description' },
    {
        key: 'sellingPrice',
        label: 'Selling Price',
        render: r => UI.money(r.sellingPrice)
    },
    {
        key: 'active',
        label: 'Active',
        render: r => UI.badge(String(r.active))
    }
];

function rowActions(row) {
    if (!isAdmin) return '';

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
    SparePartsApi.getAll()
        .then(rows => {
            allParts = rows;
            renderList(rows);
        })
        .catch(err => {
            UI.toast(err.message, 'error');
        });
}

function formHtml(p) {
    p = p || { active: true };

    return `
<div class="form-field">
    <label>Part number</label>
<input
    type="text"
    data-field="partNumber"
    value="${UI.escapeHtml(p.partNumber || '')}"
    required
>
</div>

<div class="form-field">
    <label>Name</label>
    <input
        type="text"
        data-field="name"
        value="${UI.escapeHtml(p.name || '')}"
        required
    >
</div>

<div class="form-field">
    <label>Description</label>
    <textarea data-field="description">${UI.escapeHtml(
        p.description || ''
    )}</textarea>
</div>

<div class="form-field">
    <label>Selling price</label>
    <input
        type="number"
        step="0.01"
        data-field="sellingPrice"
        value="${p.sellingPrice || ''}"
        required
    >
</div>

<div class="form-field checkbox-row">
    <input
        type="checkbox"
        id="active-check"
        data-field="active"
        ${p.active ? 'checked' : ''}
    >

        <label for="active-check" style="margin:0;">
            Active
        </label>
</div>
`;
}

function toPayload(values) {
    return {
        partNumber: values.partNumber,
        name: values.name,
        description: values.description,
        sellingPrice: Number(values.sellingPrice),
        active: !!values.active
    };
}

function openCreateForm() {
    UI.openDrawer(
        'New Spare Part',
        formHtml(),
        values => {
            return SparePartsApi
                .create(toPayload(values))
                .then(() => {
                    UI.toast(
                        'Spare part created.',
                        'success'
                    );

                    load();
                });
        },
        'Create'
    );
}

function openEditForm(id) {
    const p = allParts.find(x => x.id === id);

    if (!p) return;

    UI.openDrawer(
        'Edit Spare Part',
        formHtml(p),
        values => {
            return SparePartsApi
                .update(id, toPayload(values))
                .then(() => {
                    UI.toast(
                        'Spare part updated.',
                        'success'
                    );

                    load();
                });
        },
        'Save changes'
    );
}

function handleDelete(id) {
    const p = allParts.find(x => x.id === id);

    UI.confirmAction(
        `Delete spare part "${p ? p.name : id}"?`
    )
        .then(ok => {
            if (!ok) return;

            SparePartsApi
                .delete(id)
                .then(() => {
                    UI.toast(
                        'Spare part deleted.',
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
        allParts.filter(p =>
            (p.name || '')
                .toLowerCase()
                .includes(q) ||

            (p.partNumber || '')
                .toLowerCase()
                .includes(q)
        )
    );
});

load();
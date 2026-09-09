if (!localStorage.getItem('mc_token')) {
    window.location.href = 'index.html';
    throw new Error('Not authenticated');
}

UI.renderShell('services.html', 'Services');

const role = localStorage.getItem('mc_role');
const isAdmin = role === 'ADMIN';

if (!isAdmin) {
    $('#add-btn').remove();
}

const COMMON_SERVICES = [
    'Oil Change',
    'Full Service',
    'General Inspection',
    'Brake Service',
    'Chain & Sprocket Replacement',
    'Tyre Replacement',
    'Battery Replacement',
    'Engine Tuning',
    'Wheel Alignment',
    'Clutch Service'
];

let allServices = [];

const columns = [
    { key: 'id', label: 'ID' },
    { key: 'name', label: 'Name' },
    { key: 'description', label: 'Description' },
    {
        key: 'price',
        label: 'Price',
        render: r => UI.money(r.price)
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

    ServicesApi.getAll()
        .then(rows => {

            allServices = rows;

            renderList(rows);
        })
        .catch(err => {

            UI.toast(
                err.message,
                'error'
            );
        });
}

function formHtml(s) {

    s = s || {
        active: true
    };

    return `
<div class="form-field">
    <label>Service name</label>

<input
    type="text"
    data-field="name"
    value="${UI.escapeHtml(s.name || '')}"
    list="common-services-list"
    placeholder="Pick a common service or type your own"
    required
>

    <datalist id="common-services-list">
        ${COMMON_SERVICES
        .map(name =>
            `<option value="${UI.escapeHtml(name)}"></option>`
        )
        .join('')}
    </datalist>
</div>

<div class="form-field">
    <label>Description</label>

    <textarea
        data-field="description"
    >${UI.escapeHtml(s.description || '')}</textarea>
</div>

<div class="form-field">
    <label>Price</label>

    <input
        type="number"
        step="0.01"
        data-field="price"
        value="${s.price || ''}"
        required
    >
</div>

<div class="form-field checkbox-row">

    <input
        type="checkbox"
        id="active-check"
        data-field="active"
        ${s.active ? 'checked' : ''}
    >

        <label
            for="active-check"
            style="margin:0;"
        >
            Active (bookable by customers)
        </label>

</div>
`;
}

function toPayload(values) {

return {
    name: values.name,
    description: values.description,
    price: Number(values.price),
    active: !!values.active
};
}

function openCreateForm() {

UI.openDrawer(
    'New Service',
    formHtml(),
    values => {

        return ServicesApi
            .create(toPayload(values))
            .then(() => {

                UI.toast(
                    'Service created.',
                    'success'
                );

                load();
            });
    },
    'Create'
);
}

function openEditForm(id) {

const s = allServices.find(
    x => x.id === id
);

if (!s) return;

UI.openDrawer(
    'Edit Service',
    formHtml(s),
    values => {

        return ServicesApi
            .update(
                id,
                toPayload(values)
            )
            .then(() => {

                UI.toast(
                    'Service updated.',
                    'success'
                );

                load();
            });
    },
    'Save changes'
);
}

function handleDelete(id) {

const s = allServices.find(
    x => x.id === id
);

UI.confirmAction(
    `Delete service "${s ? s.name : id}"?`
)
    .then(ok => {

        if (!ok) return;

        ServicesApi.delete(id)
            .then(() => {

                UI.toast(
                    'Service deleted.',
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
    allServices.filter(s =>
        (s.name || '')
            .toLowerCase()
            .includes(q)
    )
);
}
);

load();

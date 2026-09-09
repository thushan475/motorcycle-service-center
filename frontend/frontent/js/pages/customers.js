if (!localStorage.getItem('mc_token')) {
    window.location.href = 'index.html';
}

UI.renderShell('customers.html', 'Customers');

const isAdmin = localStorage.getItem('mc_role') === 'ADMIN';

if (!isAdmin) {
    $('#add-btn').remove();
}

let allCustomers = [];

const columns = [
    {
        key: 'id',
        label: 'ID'
    },
    {
        key: 'name',
        label: 'Name'
    },
    {
        key: 'email',
        label: 'Email'
    },
    {
        key: 'phone',
        label: 'Phone'
    },
    {
        key: 'address',
        label: 'Address'
    }
];

function rowActions(row) {

    if (!isAdmin) {
        return '';
    }

    return `
<button
class="btn btn-small"
data-edit="${row.id}">
    Edit
    </button>

<button
    class="btn btn-small btn-danger"
    data-delete="${row.id}">
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

        const id = Number(
            $(this).data('edit')
        );

        openEditForm(id);
    });

    $('[data-delete]').on('click', function () {

        const id = Number(
            $(this).data('delete')
        );

        handleDelete(id);
    });
}

function load() {

    CustomersApi.getAll()

        .then(rows => {

            allCustomers = rows;

            renderList(rows);
        })

        .catch(err => {

            console.error(
                'Failed to load customers:',
                err
            );

            UI.toast(
                err.message || 'Failed to load customers.',
                'error'
            );
        });
}

function formHtml(customer) {

    customer = customer || {};

    return `
<div class="form-field">

    <label>Name</label>

<input
    type="text"
    data-field="name"
    value="${UI.escapeHtml(customer.name || '')}"
    required
>

</div>

<div class="form-field">

    <label>Email</label>

    <input
        type="email"
        data-field="email"
        value="${UI.escapeHtml(customer.email || '')}"
        required
    >

</div>

<div class="form-field">

    <label>Phone</label>

    <input
        type="tel"
        data-field="phone"
        value="${UI.escapeHtml(customer.phone || '')}"
        pattern="^(0\\d{9}|\\+94\\d{9})$"
        placeholder="e.g. 0771234567"
        title="Enter a valid Sri Lankan phone number, e.g. 0771234567 or +94771234567"
        required
    >

            <span class="hint">
                Format: 0771234567 or +94771234567
            </span>

</div>

<div class="form-field">

    <label>Address</label>

    <textarea
        data-field="address"
    >${UI.escapeHtml(customer.address || '')}</textarea>

</div>
`;
}

function openCreateForm() {

UI.openDrawer(

    'New Customer',

    formHtml(),

    function (values) {

        return CustomersApi.create(values)

            .then(() => {

                UI.toast(
                    'Customer created.',
                    'success'
                );

                load();
            })

            .catch(err => {

                throw new Error(
                    err.message ||
                    'Failed to create customer.'
                );
            });
    },

    'Create'
);
}

function openEditForm(id) {

const customer = allCustomers.find(
    customer => customer.id === id
);

if (!customer) {

    UI.toast(
        'Customer not found.',
        'error'
    );

    return;
}

UI.openDrawer(

    'Edit Customer',

    formHtml(customer),

    function (values) {

        return CustomersApi.update(
            id,
            values
        )

            .then(() => {

                UI.toast(
                    'Customer updated.',
                    'success'
                );

                load();
            })

            .catch(err => {

                throw new Error(
                    err.message ||
                    'Failed to update customer.'
                );
            });
    },

    'Save changes'
);
}

function handleDelete(id) {

const customer = allCustomers.find(
    customer => customer.id === id
);

const customerName = customer
    ? customer.name
    : id;

UI.confirmAction(
    `Delete customer "${customerName}"? This cannot be undone.`
)

    .then(ok => {

        if (!ok) {
            return;
        }

        CustomersApi.delete(id)

            .then(() => {

                UI.toast(
                    'Customer deleted.',
                    'success'
                );

                load();
            })

            .catch(err => {

                console.error(
                    'Failed to delete customer:',
                    err
                );

                UI.toast(
                    err.message ||
                    'Failed to delete customer.',
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
    .toLowerCase()
    .trim();

const filtered = allCustomers.filter(
    customer =>

        (customer.name || '')
            .toLowerCase()
            .includes(q)

        ||

        (customer.email || '')
            .toLowerCase()
            .includes(q)

        ||

        (customer.phone || '')
            .toLowerCase()
            .includes(q)

        ||

        (customer.address || '')
            .toLowerCase()
            .includes(q)
);

renderList(filtered);
}
);

load();


$('#history-btn').on('click', function () {
    var id = prompt('Enter Customer ID to view service history:');
    if (!id) return;
    CustomersApi.getServiceHistory(id)
        .then(function (res) {
            var rows = (res && res.body) ? res.body : res;
            UI.toast('Loaded ' + (Array.isArray(rows) ? rows.length : 0) + ' service order(s) for customer ' + id, 'success');
            console.log('Service history', rows);
        })
        .catch(function (err) {
            UI.toast(err.message || 'Failed to load service history', 'error');
        });
});

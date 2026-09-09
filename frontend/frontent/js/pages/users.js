if (!localStorage.getItem('mc_token')) {
    window.location.href = 'index.html';
    throw new Error('Not authenticated');
}

const role = localStorage.getItem('mc_role');

if (role !== 'ADMIN') {
    window.location.href = 'dashboard.html';
    throw new Error('Admin access required');
}

UI.renderShell('users.html', 'Users');

const currentUsername = localStorage.getItem('mc_username');

let allUsers = [];

const columns = [
    { key: 'id', label: 'ID' },

    { key: 'username', label: 'Username' },

    { key: 'email', label: 'Email' },

    {
        key: 'role',
        label: 'Role',
        render: r =>
            `<span class="role-pill role-${r.role}">
    ${r.role}
</span>`
    },

    {
        key: 'enabled',
        label: 'Enabled',
        render: r => UI.badge(String(r.enabled))
    },

    {
        key: 'createdAt',
        label: 'Created',
        render: r =>
            r.createdAt
                ? String(r.createdAt)
                    .replace('T', ' ')
                    .slice(0, 16)
                : ''
    }
];

function rowActions(row) {

    if (row.username === currentUsername) {
        return '<span class="dim">You</span>';
    }

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

    UsersApi.getAll()
        .then(rows => {

            allUsers = rows;

            renderList(rows);

        })
        .catch(err => {

            UI.toast(
                err.message,
                'error'
            );

        });
}

function formHtml() {

    return `
    <div class="form-field">
    <label>Username</label>

<input
    type="text"
    data-field="username"
    required
>
</div>

<div class="form-field">
    <label>Email</label>

    <input
        type="email"
        data-field="email"
        required
    >
</div>

<div class="form-field">
    <label>Password</label>

    <input
        type="password"
        data-field="password"
        minlength="6"
        required
    >
</div>

<div class="form-field">
    <label>Role</label>

    <select
        data-field="role"
        required
    >
        <option value="USER">
            USER (workshop staff)
        </option>

        <option value="ADMIN">
            ADMIN
        </option>
    </select>
</div>
`;
}

function openCreateForm() {

UI.openDrawer(
    'Register Employee',
    formHtml(),

    values => {

        return UsersApi
            .create({
                username: values.username,
                email: values.email,
                password: values.password,
                role: values.role
            })
            .then(() => {

                UI.toast(
                    'Employee registered.',
                    'success'
                );

                load();

            });
    },

    'Register'
);
}

$('#add-btn').on(
'click',
openCreateForm
);

function handleDelete(id) {

const u = allUsers.find(
    x => x.id === id
);

UI.confirmAction(
    `Delete user "${u ? u.username : id}"? They will lose access immediately.`
)
    .then(ok => {

        if (!ok) return;

        UsersApi
            .delete(id)
            .then(() => {

                UI.toast(
                    'User deleted.',
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

$('#search-input').on(
'input',
function () {

const q = $(this)
    .val()
    .toLowerCase();

renderList(
    allUsers.filter(u =>
        (u.username || '')
            .toLowerCase()
            .includes(q) ||

        (u.email || '')
            .toLowerCase()
            .includes(q)
    )
);
}
);

load();

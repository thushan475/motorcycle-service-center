if (!localStorage.getItem('mc_token')) {
    window.location.href = 'index.html';
}

UI.renderShell('appointments.html', 'Appointments');

const isAdmin = localStorage.getItem('mc_role') === 'ADMIN';

const STATUSES = [
    'PENDING',
    'CONFIRMED',
    'COMPLETED',
    'CANCELLED'
];

let allAppointments = [];
let allCustomers = [];
let allMotorcycles = [];

const columns = [
    {
        key: 'id',
        label: 'ID'
    },
    {
        key: 'appointmentDate',
        label: 'Date'
    },
    {
        key: 'appointmentTime',
        label: 'Time'
    },
    {
        key: 'customerName',
        label: 'Customer'
    },
    {
        key: 'motorcycleRegistrationNumber',
        label: 'Motorcycle',
        mono: true
    },
    {
        key: 'status',
        label: 'Status',
        render: r => UI.badge(r.status)
    },
    {
        key: 'remarks',
        label: 'Remarks'
    }
];

function rowActions(row) {

    let html = `
<button
class="btn btn-small"
data-edit="${row.id}">
    Edit
    </button>
        `;

    if (isAdmin) {

        html += `
    <button
class="btn btn-small"
data-status="${row.id}">
    Status
    </button>

<button
    class="btn btn-small btn-danger"
    data-delete="${row.id}">
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

    $('[data-edit]').on('click', function () {

        const id = Number(
            $(this).data('edit')
        );

        openEditForm(id);
    });

    $('[data-status]').on('click', function () {

        const id = Number(
            $(this).data('status')
        );

        openStatusForm(id);
    });

    $('[data-delete]').on('click', function () {

        const id = Number(
            $(this).data('delete')
        );

        handleDelete(id);
    });
}

function load() {

    AppointmentsApi.getAll()

        .then(rows => {

            allAppointments = rows;

            renderList(rows);
        })

        .catch(err => {

            console.error(
                'Failed to load appointments:',
                err
            );

            UI.toast(
                err.message ||
                'Failed to load appointments.',
                'error'
            );
        });
}

function customerOptions(selectedId) {

    return allCustomers
        .map(customer => {

            return `
<option
value="${customer.id}"
${selectedId === customer.id ? 'selected' : ''}>
${UI.escapeHtml(customer.name)}
(#${customer.id})
</option>
    `;
        })
        .join('');
}

function motorcycleOptions(selectedId) {

    return allMotorcycles
        .map(motorcycle => {

            return `
<option
value="${motorcycle.id}"
${selectedId === motorcycle.id ? 'selected' : ''}>
${UI.escapeHtml(
    motorcycle.registrationNumber
)}
</option>
`;
        })
        .join('');
}

function todayDateStr() {

    const d = new Date();

    const mm = String(
        d.getMonth() + 1
    ).padStart(2, '0');

    const dd = String(
        d.getDate()
    ).padStart(2, '0');

    return `${d.getFullYear()}-${mm}-${dd}`;
}

function nowTimeStr() {

    const d = new Date();

    const hh = String(
        d.getHours()
    ).padStart(2, '0');

    const mi = String(
        d.getMinutes()
    ).padStart(2, '0');

    return `${hh}:${mi}`;
}

function formHtml(a) {

    a = a || {};

    const isNew = !a.id;

    const dateValue =
        a.appointmentDate ||
        (isNew ? todayDateStr() : '');

    const timeValue =
        (a.appointmentTime || '').slice(0, 5) ||
        (isNew ? nowTimeStr() : '');

    return `
<div class="form-row">

    <div class="form-field">

    <label>Date</label>

<input
    type="date"
    data-field="appointmentDate"
    value="${dateValue}"
    required
>

</div>

<div class="form-field">

    <label>Time</label>

    <input
        type="time"
        data-field="appointmentTime"
        value="${timeValue}"
        required
    >

</div>

</div>

<div class="form-field">

    <label>Customer</label>

    <select
        data-field="customerId"
        required
    >

        <option value="">
            Select a customer
        </option>

        ${customerOptions(a.customerId)}

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

        ${motorcycleOptions(a.motorcycleId)}

    </select>

</div>

<div class="form-field">

    <label>Remarks</label>

    <textarea
        data-field="remarks"
    >${UI.escapeHtml(a.remarks || '')}</textarea>

</div>
    `;
}

function toPayload(values) {

    return {

        appointmentDate:
            values.appointmentDate,

        appointmentTime:
            values.appointmentTime.length === 5
                ? values.appointmentTime + ':00'
                : values.appointmentTime,

        remarks:
            values.remarks,

        customerId:
            Number(values.customerId),

        motorcycleId:
            Number(values.motorcycleId)
    };
}

function openCreateForm() {

    UI.openDrawer(

        'New Appointment',

        formHtml(),

        function (values) {

            return AppointmentsApi
                .create(
                    toPayload(values)
                )

                .then(() => {

                    UI.toast(
                        'Appointment booked.',
                        'success'
                    );

                    load();
                })

                .catch(err => {

                    throw new Error(
                        err.message ||
                        'Failed to create appointment.'
                    );
                });
        },

        'Create'
    );
}

function openEditForm(id) {

    const appointment =
        allAppointments.find(
            x => x.id === id
        );

    if (!appointment) {

        UI.toast(
            'Appointment not found.',
            'error'
        );

        return;
    }

    UI.openDrawer(

        'Edit Appointment',

        formHtml(appointment),

        function (values) {

            return AppointmentsApi
                .update(
                    id,
                    toPayload(values)
                )

                .then(() => {

                    UI.toast(
                        'Appointment updated.',
                        'success'
                    );

                    load();
                })

                .catch(err => {

                    throw new Error(
                        err.message ||
                        'Failed to update appointment.'
                    );
                });
        },

        'Save changes'
    );
}

function openStatusForm(id) {

    const appointment =
        allAppointments.find(
            x => x.id === id
        );

    if (!appointment) {

        UI.toast(
            'Appointment not found.',
            'error'
        );

        return;
    }

    const options = STATUSES
        .map(status => {

            return `
<option
value="${status}"
${status === appointment.status
    ? 'selected'
    : ''}>
${status}
</option>
`;
        })
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

        'Update Appointment Status',

        html,

        function (values) {

            return AppointmentsApi
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
                })

                .catch(err => {

                    throw new Error(
                        err.message ||
                        'Failed to update status.'
                    );
                });
        },

        'Update'
    );
}

function handleDelete(id) {

    UI.confirmAction(
        'Delete this appointment?'
    )

    .then(ok => {

        if (!ok) {
            return;
        }

        AppointmentsApi
            .delete(id)

            .then(() => {

                UI.toast(
                    'Appointment deleted.',
                    'success'
                );

                load();
            })

            .catch(err => {

                UI.toast(
                    err.message ||
                    'Failed to delete appointment.',
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

        const filtered =
            allAppointments.filter(
                appointment =>

                    (appointment.customerName || '')
                        .toLowerCase()
                        .includes(q)

                    ||

                    (appointment.motorcycleRegistrationNumber || '')
                        .toLowerCase()
                        .includes(q)

                    ||

                    (appointment.status || '')
                        .toLowerCase()
                        .includes(q)
            );

        renderList(filtered);
    }
);

Promise.all([
    CustomersApi.getAll(),
    MotorcyclesApi.getAll()
])

.then(([customers, motorcycles]) => {

    allCustomers = customers;

    allMotorcycles = motorcycles;

    load();
})

.catch(err => {

    console.error(
        'Failed to load customer/motorcycle data:',
        err
    );

    UI.toast(
        err.message ||
        'Failed to load required data.',
        'error'
    );
});

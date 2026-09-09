$.ajaxSetup({
    dataFilter: function (rawData, type) {

        if (type !== 'json' || !rawData) {
            return rawData;
        }

        try {
            const parsed = JSON.parse(rawData);

            if (
                parsed &&
                typeof parsed === 'object' &&
                Object.prototype.hasOwnProperty.call(parsed, 'body') &&
                Object.prototype.hasOwnProperty.call(parsed, 'status') &&
                Object.prototype.hasOwnProperty.call(parsed, 'message')
            ) {
                return JSON.stringify(parsed.body);
            }
        } catch (e) {

        }

        return rawData;
    }
});

const NAV_SECTIONS = [
    {
        title: 'Front Desk',
        items: [
            { label: 'Dashboard', href: 'dashboard.html', roles: ['ADMIN', 'USER'] },
            { label: 'Customers', href: 'customers.html', roles: ['ADMIN', 'USER'] },
            { label: 'Motorcycles', href: 'motorcycles.html', roles: ['ADMIN', 'USER'] },
            { label: 'Brands', href: 'brands.html', roles: ['ADMIN', 'USER'] },
            { label: 'Appointments', href: 'appointments.html', roles: ['ADMIN', 'USER'] }
        ]
    },
    {
        title: 'Workshop Floor',
        items: [
            { label: 'Services', href: 'services.html', roles: ['ADMIN', 'USER'] },
            { label: 'Service Orders', href: 'service-orders.html', roles: ['ADMIN', 'USER'] }
        ]
    },
    {
        title: 'Parts &amp; Stock',
        items: [
            { label: 'Spare Parts', href: 'spare-parts.html', roles: ['ADMIN', 'USER'] },
            { label: 'Inventory', href: 'inventory.html', roles: ['ADMIN'] },
            { label: 'Suppliers', href: 'suppliers.html', roles: ['ADMIN'] },
            { label: 'Purchases', href: 'purchases.html', roles: ['ADMIN'] }
        ]
    },
    {
        title: 'Billing',
        items: [
            { label: 'Invoices', href: 'invoices.html', roles: ['ADMIN', 'USER'] },
            { label: 'Payments', href: 'payments.html', roles: ['ADMIN', 'USER'] }
        ]
    },
    {
        title: 'Admin',
        items: [
            { label: 'Users', href: 'users.html', roles: ['ADMIN'] }
        ]
    }
];

const UI = {

    renderShell(activeHref, pageTitle) {

        const role = localStorage.getItem('mc_role');
        const username = localStorage.getItem('mc_username');

        let navHtml = '';

        NAV_SECTIONS.forEach(section => {

            const visibleItems = section.items.filter(item =>
                item.roles.includes(role)
            );

            if (visibleItems.length === 0) return;

            navHtml += `
<div class="nav-section">
    <p class="nav-section-title">${section.title}</p>
<ul class="nav-list">
    `;

    visibleItems.forEach(item => {

    const active = item.href === activeHref
    ? ' active'
    : '';

    navHtml += `
                    <li>
                        <a class="nav-link${active}" href="${item.href}">
                            ${item.label}
                        </a>
                    </li>
                `;
});

    navHtml += `
</ul>
</div>
`;
        });

        $('#sidebar').html(`
<div class="brand">
    <span class="brand-mark">MC</span>
<span class="brand-text">Workshop&nbsp;Ops</span>
</div>

<nav class="nav">
    ${navHtml}
</nav>
    `);

        $('#topbar').html(`
<h1 class="page-title">${pageTitle}</h1>

<div class="topbar-right">
    <div class="user-chip">
        <span class="user-name">${username || ''}</span>
        <span class="role-pill role-${role || ''}">
                        ${role || ''}
                    </span>
    </div>

    <button id="logout-btn" class="btn btn-ghost">
        Log out
    </button>
</div>
    `);

        $('#logout-btn').on('click', function () {

            localStorage.removeItem('mc_token');
            localStorage.removeItem('mc_username');
            localStorage.removeItem('mc_role');

            window.location.href = 'index.html';
        });
    },

    toast(message, type) {

        type = type || 'info';

        const $t = $(
            `<div class="toast toast-${type}">${message}</div>`
        );

        $('#toast-stack').append($t);

        requestAnimationFrame(() => {
            $t.addClass('show');
        });

        setTimeout(() => {

            $t.removeClass('show');

            setTimeout(() => {
                $t.remove();
            }, 250);

        }, 3600);
    },

    openDrawer(title, bodyHtml, onSubmit, submitLabel) {

        $('#drawer-title').text(title);
        $('#drawer-body').html(bodyHtml);
        $('#drawer-submit').text(submitLabel || 'Save');

        $('#drawer-overlay').addClass('open');
        $('#drawer').addClass('open');

        const $form = $('#drawer-form');

        $form.off('submit').on('submit', function (e) {

            e.preventDefault();

            const values = {};

            $(this).find('[data-field]').each(function () {

                const $el = $(this);
                const key = $el.data('field');

                if ($el.attr('type') === 'checkbox') {
                    values[key] = $el.is(':checked');
                } else {
                    values[key] = $el.val();
                }
            });

            $('#drawer-error')
                .hide()
                .text('');

            $('#drawer-submit')
                .prop('disabled', true)
                .text('Saving...');

            Promise.resolve(onSubmit(values))

                .then(() => {
                    UI.closeDrawer();
                })

                .catch(err => {

                    $('#drawer-error')
                        .text(
                            err && err.message
                                ? err.message
                                : 'Something went wrong.'
                        )
                        .show();
                })

                .finally(() => {

                    $('#drawer-submit')
                        .prop('disabled', false)
                        .text(submitLabel || 'Save');
                });
        });
    },

    closeDrawer() {

        $('#drawer-overlay').removeClass('open');
        $('#drawer').removeClass('open');
        $('#drawer-body').empty();

        $('#drawer-error')
            .hide()
            .text('');
    },

    confirmAction(message) {

        return new Promise((resolve) => {

            $('#confirm-message').text(message);

            $('#confirm-overlay').addClass('open');

            $('#confirm-yes')
                .off('click')
                .on('click', function () {

                    $('#confirm-overlay').removeClass('open');

                    resolve(true);
                });

            $('#confirm-no')
                .off('click')
                .on('click', function () {

                    $('#confirm-overlay').removeClass('open');

                    resolve(false);
                });
        });
    },

    renderTable(containerSelector, columns, rows, rowActions) {

        const $container = $(containerSelector);

        if (!rows || rows.length === 0) {

            $container.html(`
<div class="empty-state">
    Nothing here yet. Use "+ New" to add the first record.
</div>
`);

            return;
        }

        let html = `
<table class="data-table">
    <thead>
    <tr>
    `;

        columns.forEach(column => {

            html += `<th>${column.label}</th>`;
        });

        if (rowActions) {
            html += `
<th class="col-actions">
    Actions
    </th>
        `;
        }

        html += `
    </tr>
</thead>
<tbody>
`;

rows.forEach(row => {

    html += '<tr>';

    columns.forEach(column => {

    const raw = column.render
    ? column.render(row)
    : (row[column.key] ?? '');

    html += `
                    <td class="${column.mono ? 'mono' : ''}">
                        ${
    raw === null ||
    raw === undefined ||
    raw === ''
    ? '<span class="dim">—</span>'
    : raw
}
                    </td>
                `;
});

    if (rowActions) {

    html += `
                    <td class="col-actions">
                        ${rowActions(row)}
                    </td>
                `;
}

    html += '</tr>';
});

html += `
</tbody>
</table>
`;

        $container.html(html);
    },

    badge(status) {

        if (!status) {
            return '<span class="dim">—</span>';
        }

        return `
<span class="badge badge-${status}">
    ${status.replace(/_/g, ' ')}
</span>
`;
    },

    money(value) {

        if (
            value === null ||
            value === undefined ||
            value === ''
        ) {
            return '<span class="dim">—</span>';
        }

        const num = Number(value);

        return 'Rs. ' + num.toLocaleString(undefined, {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        });
    },

    escapeHtml(str) {

        if (str === null || str === undefined) {
            return '';
        }

        return String(str)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;');
    }
};

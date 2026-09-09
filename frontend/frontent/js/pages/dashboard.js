if (!localStorage.getItem('mc_token')) {
    window.location.href = 'index.html';
    throw new Error('Not authenticated');
}

UI.renderShell('dashboard.html', 'Dashboard');

const username = localStorage.getItem('mc_username');
const role = localStorage.getItem('mc_role');

$('#welcome-line').text(
    `Signed in as ${username} (${role}). Here's a quick snapshot of the workshop.`
);

const isAdmin = role === 'ADMIN';

const cards = [
    { label: 'Customers', path: '/customers', roles: ['ADMIN', 'USER'] },
    { label: 'Motorcycles', path: '/motorcycles', roles: ['ADMIN', 'USER'] },
    { label: 'Appointments', path: '/appointments', roles: ['ADMIN', 'USER'] },
    { label: 'Service Orders', path: '/service-orders', roles: ['ADMIN', 'USER'] },
    { label: 'Spare Parts', path: '/spare-parts', roles: ['ADMIN', 'USER'] },
    { label: 'Invoices', path: '/invoices', roles: ['ADMIN', 'USER'] },
    { label: 'Suppliers', path: '/suppliers', roles: ['ADMIN'] },
    { label: 'Purchases', path: '/purchases', roles: ['ADMIN'] }
].filter(c => c.roles.includes(role));

Promise.allSettled(
    cards.map(c => {
        const map = {
            '/customers': CustomersApi.getAll,
            '/motorcycles': MotorcyclesApi.getAll,
            '/appointments': AppointmentsApi.getAll,
            '/service-orders': ServiceOrdersApi.getAll,
            '/spare-parts': SparePartsApi.getAll,
            '/invoices': InvoicesApi.getAll,
            '/suppliers': SuppliersApi.getAll,
            '/purchases': PurchasesApi.getAll
        };

        const apiMethod = map[c.path];

        if (!apiMethod) {
            return Promise.reject({
                message: 'Unknown path'
            });
        }

        return apiMethod();
    })
).then(results => {

    let html = '';

    results.forEach((res, i) => {

        const count =
            res.status === 'fulfilled' && Array.isArray(res.value)
                ? res.value.length
                : '—';

        html += `
            <div class="stat-card">
                <div class="stat-value">${count}</div>
                <div class="stat-label">${cards[i].label}</div>
            </div>
        `;
    });

    $('#stat-grid').html(html);
});

const quickLinks = [
    {
        label: 'Book an appointment',
        href: 'appointments.html'
    },
    {
        label: 'Open a service order',
        href: 'service-orders.html'
    },
    {
        label: 'Check stock',
        href: 'inventory.html',
        adminOnly: true
    },
    {
        label: 'Raise an invoice',
        href: 'invoices.html'
    }
].filter(l => !l.adminOnly || isAdmin);

$('#quick-links').html(
    quickLinks
        .map(l => `<a class="btn" href="${l.href}">${l.label}</a>`)
        .join('')
);

if (!localStorage.getItem('mc_token')) {
    window.location.href = 'index.html';
}

UI.renderShell('brands.html', 'Motorcycle Brands');

const isAdmin = localStorage.getItem('mc_role') === 'ADMIN';

if (!isAdmin) {
    $('#add-btn').remove();
}

const COMMON_BRANDS = [
    'Honda',
    'Yamaha',
    'Suzuki',
    'Bajaj',
    'TVS',
    'Hero',
    'Kawasaki',
    'KTM',
    'Royal Enfield',
    'Vespa / Piaggio',
    'Demak',
    'DSI'
];

let allBrands = [];

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
        key: 'description',
        label: 'Description'
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

    BrandsApi.getAll()

        .then(rows => {

            allBrands = rows;

            renderList(rows);
        })

        .catch(err => {

            console.error(
                'Failed to load brands:',
                err
            );

            UI.toast(
                err.message ||
                'Failed to load brands.',
                'error'
            );
        });
}

function formHtml(brand) {

    brand = brand || {};

    return `
<div class="form-field">

    <label>Brand name</label>

<input
    type="text"
    data-field="name"
    value="${UI.escapeHtml(brand.name || '')}"
    list="common-brands-list"
    placeholder="Pick a common brand or type your own"
    required
>

    <datalist id="common-brands-list">

        ${COMMON_BRANDS
        .map(name =>
            `<option value="${UI.escapeHtml(name)}"></option>`
        )
        .join('')
    }

    </datalist>

</div>

<div class="form-field">

    <label>Description</label>

    <textarea
        data-field="description"
    >${UI.escapeHtml(brand.description || '')}</textarea>

</div>
    `;
}

function openCreateForm() {

    UI.openDrawer(

        'New Brand',

        formHtml(),

        function (values) {

            return BrandsApi.create(values)

                .then(() => {

                    UI.toast(
                        'Brand created.',
                        'success'
                    );

                    load();
                })

                .catch(err => {

                    throw new Error(
                        err.message ||
                        'Failed to create brand.'
                    );
                });
        },

        'Create'
    );
}

function openEditForm(id) {

    const brand = allBrands.find(
        brand => brand.id === id
    );

    if (!brand) {

        UI.toast(
            'Brand not found.',
            'error'
        );

        return;
    }

    UI.openDrawer(

        'Edit Brand',

        formHtml(brand),

        function (values) {

            return BrandsApi.update(
                id,
                values
            )

            .then(() => {

                UI.toast(
                    'Brand updated.',
                    'success'
                );

                load();
            })

            .catch(err => {

                throw new Error(
                    err.message ||
                    'Failed to update brand.'
                );
            });
        },

        'Save changes'
    );
}

function handleDelete(id) {

    const brand = allBrands.find(
        brand => brand.id === id
    );

    UI.confirmAction(
        `Delete brand "${brand ? brand.name : id}"?`
    )

    .then(ok => {

        if (!ok) {
            return;
        }

        BrandsApi.delete(id)

            .then(() => {

                UI.toast(
                    'Brand deleted.',
                    'success'
                );

                load();
            })

            .catch(err => {

                console.error(
                    'Failed to delete brand:',
                    err
                );

                UI.toast(
                    err.message ||
                    'Failed to delete brand.',
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

        const filtered = allBrands.filter(
            brand =>
                (brand.name || '')
                    .toLowerCase()
                    .includes(q)
        );

        renderList(filtered);
    }
);

load();

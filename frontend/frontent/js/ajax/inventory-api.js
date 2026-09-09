const InventoryApi = {

    getAll: function () {
        return $.ajax({
            url: 'http://localhost:8080/api/inventory',
            type: 'GET',
            dataType: 'json',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    },

    getById: function (id) {
        return $.ajax({
            url: 'http://localhost:8080/api/inventory/' + id,
            type: 'GET',
            dataType: 'json',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    },

    create: function (data) {
        return $.ajax({
            url: 'http://localhost:8080/api/inventory',
            type: 'POST',
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify(data),
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    },

    adjust: function (id, data) {
        return $.ajax({
            url: 'http://localhost:8080/api/inventory/' + id + '/adjust',
            type: 'PATCH',
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify(data),
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    },

    delete: function (id) {
        return $.ajax({
            url: 'http://localhost:8080/api/inventory/' + id,
            type: 'DELETE',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    },

    getLowStock: function (threshold) {
        var url = 'http://localhost:8080/api/inventory/low-stock';
        if (threshold !== undefined && threshold !== null) {
            url += '?threshold=' + threshold;
        }
        return $.ajax({
            url: url,
            type: 'GET',
            dataType: 'json',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    },

    getValue: function () {
        return $.ajax({
            url: 'http://localhost:8080/api/inventory/value',
            type: 'GET',
            dataType: 'json',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    },

    setQuantity: function (id, quantity) {
        return $.ajax({
            url: 'http://localhost:8080/api/inventory/' + id + '/quantity',
            type: 'PUT',
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify({ quantity: quantity }),
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    },

    deleteZeroQuantity: function () {
        return $.ajax({
            url: 'http://localhost:8080/api/inventory/zero-quantity',
            type: 'DELETE',
            dataType: 'json',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    }
};

const ServiceOrdersApi = {

    getAll: function () {
        return $.ajax({
            url: 'http://localhost:8080/api/service-orders',
            type: 'GET',
            dataType: 'json',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    },

    getById: function (id) {
        return $.ajax({
            url: 'http://localhost:8080/api/service-orders/' + id,
            type: 'GET',
            dataType: 'json',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    },

    create: function (data) {
        return $.ajax({
            url: 'http://localhost:8080/api/service-orders',
            type: 'POST',
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify(data),
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    },

    updateStatus: function (id, data) {
        return $.ajax({
            url: 'http://localhost:8080/api/service-orders/' + id + '/status',
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
            url: 'http://localhost:8080/api/service-orders/' + id,
            type: 'DELETE',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    }
,

    getCompleted: function () {
        return $.ajax({
            url: 'http://localhost:8080/api/service-orders/completed',
            type: 'GET',
            dataType: 'json',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    },

    getServiceHistory: function (customerId) {
        return $.ajax({
            url: 'http://localhost:8080/api/service-orders/customer/' + customerId + '/service-history',
            type: 'GET',
            dataType: 'json',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    }
};

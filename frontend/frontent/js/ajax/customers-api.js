const CustomersApi = {

    getAll: function () {
        return $.ajax({
            url: 'http://localhost:8080/api/customers',
            type: 'GET',
            dataType: 'json',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    },

    getById: function (id) {
        return $.ajax({
            url: 'http://localhost:8080/api/customers/' + id,
            type: 'GET',
            dataType: 'json',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    },

    create: function (data) {
        return $.ajax({
            url: 'http://localhost:8080/api/customers',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    },

    update: function (id, data) {
        return $.ajax({
            url: 'http://localhost:8080/api/customers/' + id,
            type: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(data),
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    },

    delete: function (id) {
        return $.ajax({
            url: 'http://localhost:8080/api/customers/' + id,
            type: 'DELETE',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    }
,

    getServiceHistory: function (customerId) {
        return $.ajax({
            url: 'http://localhost:8080/api/customers/' + customerId + '/service-history',
            type: 'GET',
            dataType: 'json',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    }
};

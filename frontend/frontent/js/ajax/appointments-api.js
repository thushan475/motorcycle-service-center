const AppointmentsApi = {

    getAll: function () {
        return $.ajax({
            url: 'http://localhost:8080/api/appointments',
            type: 'GET',
            dataType: 'json',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    },

    getById: function (id) {
        return $.ajax({
            url: 'http://localhost:8080/api/appointments/' + id,
            type: 'GET',
            dataType: 'json',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    },

    getByCustomer: function (customerId) {
        return $.ajax({
            url: 'http://localhost:8080/api/appointments/customer/' + customerId,
            type: 'GET',
            dataType: 'json',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    },

    create: function (data) {
        return $.ajax({
            url: 'http://localhost:8080/api/appointments',
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
            url: 'http://localhost:8080/api/appointments/' + id,
            type: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(data),
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    },

    updateStatus: function (id, data) {
        return $.ajax({
            url: 'http://localhost:8080/api/appointments/' + id + '/status',
            type: 'PATCH',
            contentType: 'application/json',
            data: JSON.stringify(data),
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    },

    delete: function (id) {
        return $.ajax({
            url: 'http://localhost:8080/api/appointments/' + id,
            type: 'DELETE',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    }
};

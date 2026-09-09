const UsersApi = {

    getAll: function () {
        return $.ajax({
            url: 'http://localhost:8080/api/users',
            type: 'GET',
            dataType: 'json',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    },

    getById: function (id) {
        return $.ajax({
            url: 'http://localhost:8080/api/users/' + id,
            type: 'GET',
            dataType: 'json',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    },

    getMe: function () {
        return $.ajax({
            url: 'http://localhost:8080/api/users/me',
            type: 'GET',
            dataType: 'json',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    },

    create: function (data) {
        return $.ajax({
            url: 'http://localhost:8080/api/users',
            type: 'POST',
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
            url: 'http://localhost:8080/api/users/' + id,
            type: 'DELETE',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    }
};

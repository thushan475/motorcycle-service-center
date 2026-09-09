const AuthApi = {

    login: function (credentials, opts) {
        return $.ajax({
            url: 'http://localhost:8080/api/auth/login',
            type: 'POST',
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify(credentials),
            ...opts
        });
    },

    register: function (data) {
        return $.ajax({
            url: 'http://localhost:8080/api/auth/register',
            type: 'POST',
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify(data)
        });
    }
};

const AiApi = {
    generateServiceReport: function (serviceOrderId) {
        return $.ajax({
            url: 'http://localhost:8080/api/ai/service-report/' + serviceOrderId,
            type: 'GET',
            dataType: 'json',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('mc_token')
            }
        });
    }
};

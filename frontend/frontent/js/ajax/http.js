const Http = (function () {
  function request(method, path, data, opts) {
    opts = opts || {};
    return new Promise(function (resolve, reject) {
      var xhr = new XMLHttpRequest();
      var url = CONFIG.API_BASE_URL + path;

      xhr.open(method, url, true);
      xhr.setRequestHeader('Content-Type', 'application/json');
      xhr.setRequestHeader('Accept', 'application/json');

      var token = Auth.getToken();
      if (token) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + token);
      }

      xhr.onload = function () {
        var status = xhr.status;
        var responseBody = null;
        var message = null;

        try {
          if (xhr.responseText) {
            responseBody = JSON.parse(xhr.responseText);
          }
        } catch (e) {
          responseBody = null;
        }

        if (status >= 200 && status < 300) {

          if (responseBody && responseBody.body !== undefined) {
            resolve(responseBody.body);
          } else {
            resolve(responseBody);
          }
          return;
        }

        if (responseBody && responseBody.message) {
          message = responseBody.message;
        } else if (status === 401) {
          message = 'Your session has expired. Please log in again.';
        } else if (status === 403) {
          message = 'You do not have permission to do that.';
        } else if (status === 404) {
          message = 'Resource not found.';
        } else if (status === 409) {
          message = 'Conflict: the resource already exists or cannot be modified.';
        } else if (status === 400) {
          message = (responseBody && responseBody.message) || 'Invalid request.';
        } else if (status === 500) {
          message = 'Server error. Please try again later.';
        } else if (status) {
          message = 'Request failed (HTTP ' + status + ').';
        } else {
          message = 'Could not reach the server. Is the backend running on ' + CONFIG.API_BASE_URL + '?';
        }

        if (status === 401 && !opts.silent401) {
          Auth.logout();
        }

        reject({ status: status, message: message });
      };

      xhr.onerror = function () {
        reject({
          status: 0,
          message: 'Could not reach the server. Is the backend running on ' + CONFIG.API_BASE_URL + '?'
        });
      };

      xhr.ontimeout = function () {
        reject({ status: 0, message: 'Request timed out.' });
      };

      if (data !== undefined && data !== null) {
        xhr.send(JSON.stringify(data));
      } else {
        xhr.send();
      }
    });
  }

  return {
    get: function (path, opts) {
      return request('GET', path, undefined, opts);
    },
    post: function (path, data, opts) {
      return request('POST', path, data, opts);
    },
    put: function (path, data, opts) {
      return request('PUT', path, data, opts);
    },
    patch: function (path, data, opts) {
      return request('PATCH', path, data, opts);
    },
    del: function (path, opts) {
      return request('DELETE', path, undefined, opts);
    },
    request: request
  };
})();

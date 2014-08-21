angular.module('kiApp.AlertService', ['ngResource']).

service('AlertService', [function() {
    var alerts = {};
    return {
        setAlert: function(id) {
            alerts[id] = true;
        },

        getAlert: function(id) {
            return alerts[id];
        }
    }
}]);
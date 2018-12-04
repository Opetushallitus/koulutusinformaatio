angular.module('kiApp.HostResolver', ['ngResource']).

service('HostResolver', function() {
    var hosts = {
        demo: 'demo',
        koulutus: 'koulutus',
        qa: 'qa',
        localhost: 'dev'
    };

    return {
        mapHostToConf: function(host) {
            if (host) {
                if (host.indexOf('koulutus') == 0) return hosts.koulutus;
                else if (host.indexOf('testi') == 0) return hosts.qa;
                else if (host.indexOf('localhost') == 0) return hosts.localhost;
                else if (host.indexOf('demo.') == 0) return hosts.demo;
            }
            return 'prod'; // return prod config by default
        }
    };
});

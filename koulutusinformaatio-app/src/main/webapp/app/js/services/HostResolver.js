angular.module('kiApp.HostResolver', ['ngResource']).

service('HostResolver', function() {
    var hosts = {
        koulutus: 'koulutus',
        qa: 'qa',
        kielistudio: 'kielistudio',
        reppu: 'reppu',
        luokka: 'luokka',
        localhost: 'localhost'
    };

    return {
        resolve: function(host) {
            if (host) {
                if (host.indexOf('koulutus') == 0) return hosts.koulutus;
                else if (host.indexOf('testi') == 0) return hosts.qa;
                else if (host.indexOf('xtest-') == 0) return hosts.kielistudio;
                else if (host.indexOf('test-') == 0) return hosts.reppu;
                else if (host.indexOf('itest-') == 0) return hosts.luokka;
                else if (host.indexOf('localhost') == 0 || host.indexOf('10.0.2.2') == 0) return hosts.localhost;
            }
        },

        mapHostToConf: function(host) {
            if (host) {
                if (host == hosts.kielistudio ||
                    host == hosts.reppu ||
                    host == hosts.luokka ||
                    host == hosts.localhost) {
                    return 'dev';
                } else if (host == hosts.qa) {
                    return 'qa';
                } else if (host == hosts.koulutus) {
                    return 'koulutus';
                }
            }

            return 'prod'; // return prod config by default
        }
    };
});
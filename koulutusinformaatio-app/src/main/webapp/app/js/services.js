'use strict';

/* Services */

angular.module('kiApp.services', ['ngResource']).
    factory('LearningOpportunity', function($resource){
        return $resource('../lo/search/:q', {}, {
            query: {method:'GET', isArray:true}
        });
    });
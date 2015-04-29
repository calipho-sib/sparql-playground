(function (angular, undefined) {'use strict';

//Define the application global configuration
angular.module('snorql.config', []).factory('config', [
    function () {

    	var BASE_URL = window.location.origin + "/";
        // global application configuration
        var defaultConfig = {
            apiUrl : BASE_URL,
            home:'/',
            sparql : {
              endpoint: BASE_URL + 'sparql',
              examples: BASE_URL + 'queries',
              faqsURL: BASE_URL + 'faqs',
              prefixes: BASE_URL + 'prefixes',
              dataURL: BASE_URL + 'ttl-data'
            }
        }


        return defaultConfig;
    }
]);

})(angular);

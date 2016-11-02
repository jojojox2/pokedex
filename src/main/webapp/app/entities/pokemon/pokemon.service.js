(function() {
    'use strict';
    angular
        .module('pokedexApp')
        .factory('Pokemon', Pokemon);

    Pokemon.$inject = ['$resource'];

    function Pokemon ($resource) {
        var resourceUrl =  'api/pokemons/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();

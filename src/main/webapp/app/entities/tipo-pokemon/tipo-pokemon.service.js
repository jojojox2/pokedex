(function() {
    'use strict';
    angular
        .module('pokedexApp')
        .factory('TipoPokemon', TipoPokemon);

    TipoPokemon.$inject = ['$resource'];

    function TipoPokemon ($resource) {
        var resourceUrl =  'api/tipo-pokemons/:id';

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

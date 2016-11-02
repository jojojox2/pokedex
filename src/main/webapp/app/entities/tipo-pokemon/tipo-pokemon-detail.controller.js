(function() {
    'use strict';

    angular
        .module('pokedexApp')
        .controller('TipoPokemonDetailController', TipoPokemonDetailController);

    TipoPokemonDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'TipoPokemon'];

    function TipoPokemonDetailController($scope, $rootScope, $stateParams, previousState, entity, TipoPokemon) {
        var vm = this;

        vm.tipoPokemon = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('pokedexApp:tipoPokemonUpdate', function(event, result) {
            vm.tipoPokemon = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

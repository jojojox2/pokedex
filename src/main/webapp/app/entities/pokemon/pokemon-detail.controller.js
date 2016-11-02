(function() {
    'use strict';

    angular
        .module('pokedexApp')
        .controller('PokemonDetailController', PokemonDetailController);

    PokemonDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Pokemon', 'TipoPokemon'];

    function PokemonDetailController($scope, $rootScope, $stateParams, previousState, entity, Pokemon, TipoPokemon) {
        var vm = this;

        vm.pokemon = entity;
        vm.previousState = previousState.name;
        vm.previousStateParams = previousState.params;

        var unsubscribe = $rootScope.$on('pokedexApp:pokemonUpdate', function(event, result) {
            vm.pokemon = result;
        });
        $scope.$on('$destroy', unsubscribe);
        
        vm.updateFavorito = function (valor) {
        	vm.pokemon.favorito = valor;
            vm.isSaving = true;
        	Pokemon.update(vm.pokemon, onSaveSuccess, onSaveError);
        }
        
        function onSaveSuccess (result) {
            $scope.$emit('pokedexApp:pokemonUpdate', result);
            vm.isSaving = false;
        }

        function onSaveError () {
        	vm.pokemon.favorito = !vm.pokemon.favorito;
            vm.isSaving = false;
        }
    }
})();

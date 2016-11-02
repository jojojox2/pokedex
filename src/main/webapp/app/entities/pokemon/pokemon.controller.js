(function() {
    'use strict';

    angular
        .module('pokedexApp')
        .controller('PokemonController', PokemonController);

    PokemonController.$inject = ['$scope', '$state', 'Pokemon', 'ParseLinks', 'AlertService', 'pagingParams', 'paginationConstants'];

    function PokemonController ($scope, $state, Pokemon, ParseLinks, AlertService, pagingParams, paginationConstants) {
        var vm = this;

        vm.filtro = {
    		nombre: '',
    		favorito: false
        };
        
        vm.filtradorFavoritos = function (actual, expected) {
        	if (expected === false) {
        		return true;
        	} else {
        		return actual === expected;
        	}
        };
        
        loadAll();

        function loadAll () {
        	Pokemon.query(function(result) {
                vm.pokemons = result;
            });
        }
        
        vm.updateFavorito = function (pokemon, valor) {
        	pokemon.favorito = valor;
            vm.savingPokemon = pokemon;
        	Pokemon.update(pokemon, onSaveSuccess, onSaveError);
        }
        
        function onSaveSuccess (result) {
            $scope.$emit('pokedexApp:pokemonUpdate', result);
            vm.savingPokemon = null;
        }

        function onSaveError () {
        	vm.savingPokemon.favorito = !vm.savingPokemon.favorito;
            vm.savingPokemon = null;
        }


    }
})();

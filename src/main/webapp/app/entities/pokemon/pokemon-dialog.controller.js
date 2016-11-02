(function() {
    'use strict';

    angular
        .module('pokedexApp')
        .controller('PokemonDialogController', PokemonDialogController);

    PokemonDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Pokemon', 'TipoPokemon'];

    function PokemonDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Pokemon, TipoPokemon) {
        var vm = this;

        vm.pokemon = entity;
        vm.clear = clear;
        vm.save = save;
        vm.tipopokemons = TipoPokemon.query();
        vm.pokemons = Pokemon.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.pokemon.id !== null) {
                Pokemon.update(vm.pokemon, onSaveSuccess, onSaveError);
            } else {
                Pokemon.save(vm.pokemon, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('pokedexApp:pokemonUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();

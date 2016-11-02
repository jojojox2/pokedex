(function() {
    'use strict';

    angular
        .module('pokedexApp')
        .controller('TipoPokemonDialogController', TipoPokemonDialogController);

    TipoPokemonDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'TipoPokemon'];

    function TipoPokemonDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, TipoPokemon) {
        var vm = this;

        vm.tipoPokemon = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.tipoPokemon.id !== null) {
                TipoPokemon.update(vm.tipoPokemon, onSaveSuccess, onSaveError);
            } else {
                TipoPokemon.save(vm.tipoPokemon, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('pokedexApp:tipoPokemonUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();

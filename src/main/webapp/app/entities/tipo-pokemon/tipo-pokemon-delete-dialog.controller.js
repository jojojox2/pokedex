(function() {
    'use strict';

    angular
        .module('pokedexApp')
        .controller('TipoPokemonDeleteController',TipoPokemonDeleteController);

    TipoPokemonDeleteController.$inject = ['$uibModalInstance', 'entity', 'TipoPokemon'];

    function TipoPokemonDeleteController($uibModalInstance, entity, TipoPokemon) {
        var vm = this;

        vm.tipoPokemon = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            TipoPokemon.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();

(function() {
    'use strict';

    angular
        .module('pokedexApp')
        .controller('PokemonDeleteController',PokemonDeleteController);

    PokemonDeleteController.$inject = ['$uibModalInstance', 'entity', 'Pokemon'];

    function PokemonDeleteController($uibModalInstance, entity, Pokemon) {
        var vm = this;

        vm.pokemon = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Pokemon.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();

(function() {
    'use strict';

    var jhiItemCount = {
        template: '<div class="info">' +
                    'Mostrando {{(($ctrl.page - 1) * $ctrl.itemsPerPage) == 0 ? 1 : (($ctrl.page - 1) * $ctrl.itemsPerPage + 1)}} - ' +
                    '{{($ctrl.page * $ctrl.itemsPerPage) < $ctrl.queryCount ? ($ctrl.page * $ctrl.itemsPerPage) : $ctrl.queryCount}} ' +
                    'de {{$ctrl.queryCount}} elementos.' +
                '</div>',
        bindings: {
            page: '<',
            queryCount: '<total',
            itemsPerPage: '<'
        }
    };

    angular
        .module('pokedexApp')
        .component('jhiItemCount', jhiItemCount);
})();

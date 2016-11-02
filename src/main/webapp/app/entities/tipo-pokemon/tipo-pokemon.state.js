(function() {
    'use strict';

    angular
        .module('pokedexApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('tipo-pokemon', {
            parent: 'entity',
            url: '/tipo-pokemon?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'TipoPokemons'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/tipo-pokemon/tipo-pokemons.html',
                    controller: 'TipoPokemonController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }]
            }
        })
        .state('tipo-pokemon-detail', {
            parent: 'entity',
            url: '/tipo-pokemon/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'TipoPokemon'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/tipo-pokemon/tipo-pokemon-detail.html',
                    controller: 'TipoPokemonDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'TipoPokemon', function($stateParams, TipoPokemon) {
                    return TipoPokemon.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'tipo-pokemon',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('tipo-pokemon-detail.edit', {
            parent: 'tipo-pokemon-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tipo-pokemon/tipo-pokemon-dialog.html',
                    controller: 'TipoPokemonDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['TipoPokemon', function(TipoPokemon) {
                            return TipoPokemon.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('tipo-pokemon.new', {
            parent: 'tipo-pokemon',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tipo-pokemon/tipo-pokemon-dialog.html',
                    controller: 'TipoPokemonDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                tipo: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('tipo-pokemon', null, { reload: 'tipo-pokemon' });
                }, function() {
                    $state.go('tipo-pokemon');
                });
            }]
        })
        .state('tipo-pokemon.edit', {
            parent: 'tipo-pokemon',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tipo-pokemon/tipo-pokemon-dialog.html',
                    controller: 'TipoPokemonDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['TipoPokemon', function(TipoPokemon) {
                            return TipoPokemon.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('tipo-pokemon', null, { reload: 'tipo-pokemon' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('tipo-pokemon.delete', {
            parent: 'tipo-pokemon',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tipo-pokemon/tipo-pokemon-delete-dialog.html',
                    controller: 'TipoPokemonDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['TipoPokemon', function(TipoPokemon) {
                            return TipoPokemon.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('tipo-pokemon', null, { reload: 'tipo-pokemon' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

(function() {
    'use strict';

    angular
        .module('pokedexApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('pokemon', {
            parent: 'entity',
            url: '/pokemon?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Pokemons'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/pokemon/pokemons.html',
                    controller: 'PokemonController',
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
        .state('pokemon-detail', {
            parent: 'entity',
            url: '/pokemon/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Pokemon'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/pokemon/pokemon-detail.html',
                    controller: 'PokemonDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Pokemon', function($stateParams, Pokemon) {
                    return Pokemon.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'pokemon',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('pokemon-detail.edit', {
            parent: 'pokemon-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/pokemon/pokemon-dialog.html',
                    controller: 'PokemonDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Pokemon', function(Pokemon) {
                            return Pokemon.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('pokemon.new', {
            parent: 'pokemon',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/pokemon/pokemon-dialog.html',
                    controller: 'PokemonDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                nombre: null,
                                descripcion: null,
                                favorito: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('pokemon', null, { reload: 'pokemon' });
                }, function() {
                    $state.go('pokemon');
                });
            }]
        })
        .state('pokemon.edit', {
            parent: 'pokemon',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/pokemon/pokemon-dialog.html',
                    controller: 'PokemonDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Pokemon', function(Pokemon) {
                            return Pokemon.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('pokemon', null, { reload: 'pokemon' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('pokemon.delete', {
            parent: 'pokemon',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/pokemon/pokemon-delete-dialog.html',
                    controller: 'PokemonDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Pokemon', function(Pokemon) {
                            return Pokemon.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('pokemon', null, { reload: 'pokemon' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

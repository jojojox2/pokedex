package pokedex.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import pokedex.domain.Pokemon;
import pokedex.repository.PokemonRepository;
import pokedex.web.rest.util.HeaderUtil;
import pokedex.web.rest.util.PaginationUtil;

/**
 * REST controller for managing Pokemon.
 */
@RestController
@RequestMapping("/api")
public class PokemonResource {

    private final Logger log = LoggerFactory.getLogger(PokemonResource.class);
    private final int MAX_FAVORITES = 2;
        
    @Inject
    private PokemonRepository pokemonRepository;

    /**
     * POST  /pokemons : Create a new pokemon.
     *
     * @param pokemon the pokemon to create
     * @return the ResponseEntity with status 201 (Created) and with body the new pokemon, or with status 400 (Bad Request) if the pokemon has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/pokemons")
    @Timed
    public ResponseEntity<Pokemon> createPokemon(@Valid @RequestBody Pokemon pokemon) throws URISyntaxException {
        log.debug("REST request to save Pokemon : {}", pokemon);
        if (pokemon.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("pokemon", "idexists", "Un nuevo Pokémon no puede ya tener un ID")).body(null);
        }
        if (pokemon.isFavorito() && isMaxFavorites(pokemon)) {
            return getMaxFavoritesResponse();        	
        }
        Pokemon result = pokemonRepository.save(pokemon);
        return ResponseEntity.created(new URI("/api/pokemons/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("Pokémon", result.getNombre()))
            .body(result);
    }

    /**
     * PUT  /pokemons : Updates an existing pokemon.
     *
     * @param pokemon the pokemon to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated pokemon,
     * or with status 400 (Bad Request) if the pokemon is not valid,
     * or with status 500 (Internal Server Error) if the pokemon couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/pokemons")
    @Timed
    public ResponseEntity<Pokemon> updatePokemon(@Valid @RequestBody Pokemon pokemon) throws URISyntaxException {
        log.debug("REST request to update Pokemon : {}", pokemon);
        if (pokemon.getId() == null) {
            return createPokemon(pokemon);
        }
        if (pokemon.isFavorito() && isMaxFavorites(pokemon)) {
            return getMaxFavoritesResponse();        	
        }
        Pokemon result = pokemonRepository.save(pokemon);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("Pokémon", pokemon.getNombre()))
            .body(result);
    }

    private ResponseEntity<Pokemon> getMaxFavoritesResponse() {
		return  ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("pokemon", "maxfav", "Se ha alcanzado el máximo de favoritos")).body(null);
	}

	private boolean isMaxFavorites(Pokemon newPokemon) {
    	List<Pokemon> pokemons = pokemonRepository.findAll();
    	int count = 1;
    	for (Pokemon pokemon : pokemons) {
    		if (newPokemon.getId() != pokemon.getId() && pokemon.isFavorito()) {
    			count++;
    			if (count > MAX_FAVORITES) {
    				return true;
    			}
    		}
    	}
		return false;
	}

	/**
     * GET  /pokemons : get all the pokemons.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of pokemons in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/pokemons")
    @Timed
    public ResponseEntity<List<Pokemon>> getAllPokemons(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Pokemons");
        Page<Pokemon> page = pokemonRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/pokemons");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /pokemons/:id : get the "id" pokemon.
     *
     * @param id the id of the pokemon to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the pokemon, or with status 404 (Not Found)
     */
    @GetMapping("/pokemons/{id}")
    @Timed
    public ResponseEntity<Pokemon> getPokemon(@PathVariable Long id) {
        log.debug("REST request to get Pokemon : {}", id);
        Pokemon pokemon = pokemonRepository.findOne(id);
        return Optional.ofNullable(pokemon)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /pokemons/:id : delete the "id" pokemon.
     *
     * @param id the id of the pokemon to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/pokemons/{id}")
    @Timed
    public ResponseEntity<Void> deletePokemon(@PathVariable Long id) {
        log.debug("REST request to delete Pokemon : {}", id);
        Pokemon pokemon = pokemonRepository.findOne(id);
        pokemonRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("Pokémon", pokemon.getNombre())).build();
    }

}

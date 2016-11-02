package pokedex.web.rest;

import com.codahale.metrics.annotation.Timed;
import pokedex.domain.TipoPokemon;

import pokedex.repository.TipoPokemonRepository;
import pokedex.web.rest.util.HeaderUtil;
import pokedex.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing TipoPokemon.
 */
@RestController
@RequestMapping("/api")
public class TipoPokemonResource {

    private final Logger log = LoggerFactory.getLogger(TipoPokemonResource.class);
        
    @Inject
    private TipoPokemonRepository tipoPokemonRepository;

    /**
     * POST  /tipo-pokemons : Create a new tipoPokemon.
     *
     * @param tipoPokemon the tipoPokemon to create
     * @return the ResponseEntity with status 201 (Created) and with body the new tipoPokemon, or with status 400 (Bad Request) if the tipoPokemon has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/tipo-pokemons")
    @Timed
    public ResponseEntity<TipoPokemon> createTipoPokemon(@Valid @RequestBody TipoPokemon tipoPokemon) throws URISyntaxException {
        log.debug("REST request to save TipoPokemon : {}", tipoPokemon);
        if (tipoPokemon.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("tipoPokemon", "idexists", "A new tipoPokemon cannot already have an ID")).body(null);
        }
        TipoPokemon result = tipoPokemonRepository.save(tipoPokemon);
        return ResponseEntity.created(new URI("/api/tipo-pokemons/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("tipoPokemon", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /tipo-pokemons : Updates an existing tipoPokemon.
     *
     * @param tipoPokemon the tipoPokemon to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated tipoPokemon,
     * or with status 400 (Bad Request) if the tipoPokemon is not valid,
     * or with status 500 (Internal Server Error) if the tipoPokemon couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/tipo-pokemons")
    @Timed
    public ResponseEntity<TipoPokemon> updateTipoPokemon(@Valid @RequestBody TipoPokemon tipoPokemon) throws URISyntaxException {
        log.debug("REST request to update TipoPokemon : {}", tipoPokemon);
        if (tipoPokemon.getId() == null) {
            return createTipoPokemon(tipoPokemon);
        }
        TipoPokemon result = tipoPokemonRepository.save(tipoPokemon);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("tipoPokemon", tipoPokemon.getId().toString()))
            .body(result);
    }

    /**
     * GET  /tipo-pokemons : get all the tipoPokemons.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of tipoPokemons in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/tipo-pokemons")
    @Timed
    public ResponseEntity<List<TipoPokemon>> getAllTipoPokemons(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of TipoPokemons");
        Page<TipoPokemon> page = tipoPokemonRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tipo-pokemons");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /tipo-pokemons/:id : get the "id" tipoPokemon.
     *
     * @param id the id of the tipoPokemon to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the tipoPokemon, or with status 404 (Not Found)
     */
    @GetMapping("/tipo-pokemons/{id}")
    @Timed
    public ResponseEntity<TipoPokemon> getTipoPokemon(@PathVariable Long id) {
        log.debug("REST request to get TipoPokemon : {}", id);
        TipoPokemon tipoPokemon = tipoPokemonRepository.findOne(id);
        return Optional.ofNullable(tipoPokemon)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /tipo-pokemons/:id : delete the "id" tipoPokemon.
     *
     * @param id the id of the tipoPokemon to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/tipo-pokemons/{id}")
    @Timed
    public ResponseEntity<Void> deleteTipoPokemon(@PathVariable Long id) {
        log.debug("REST request to delete TipoPokemon : {}", id);
        tipoPokemonRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("tipoPokemon", id.toString())).build();
    }

}

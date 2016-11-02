package pokedex.repository;

import pokedex.domain.TipoPokemon;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the TipoPokemon entity.
 */
@SuppressWarnings("unused")
public interface TipoPokemonRepository extends JpaRepository<TipoPokemon,Long> {

}

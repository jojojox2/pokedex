package pokedex.repository;

import pokedex.domain.Pokemon;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Pokemon entity.
 */
@SuppressWarnings("unused")
public interface PokemonRepository extends JpaRepository<Pokemon,Long> {

}

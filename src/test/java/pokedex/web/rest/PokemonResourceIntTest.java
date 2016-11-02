package pokedex.web.rest;

import pokedex.PokedexApp;

import pokedex.domain.Pokemon;
import pokedex.domain.TipoPokemon;
import pokedex.repository.PokemonRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the PokemonResource REST controller.
 *
 * @see PokemonResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PokedexApp.class)
public class PokemonResourceIntTest {

    private static final String DEFAULT_NOMBRE = "AAAA";
    private static final String UPDATED_NOMBRE = "BBBB";

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    private static final Boolean DEFAULT_FAVORITO = false;
    private static final Boolean UPDATED_FAVORITO = true;

    @Inject
    private PokemonRepository pokemonRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restPokemonMockMvc;

    private Pokemon pokemon;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PokemonResource pokemonResource = new PokemonResource();
        ReflectionTestUtils.setField(pokemonResource, "pokemonRepository", pokemonRepository);
        this.restPokemonMockMvc = MockMvcBuilders.standaloneSetup(pokemonResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pokemon createEntity(EntityManager em) {
        Pokemon pokemon = new Pokemon()
                .nombre(DEFAULT_NOMBRE)
                .descripcion(DEFAULT_DESCRIPCION)
                .favorito(DEFAULT_FAVORITO);
        // Add required entity
        TipoPokemon tipo1 = TipoPokemonResourceIntTest.createEntity(em);
        em.persist(tipo1);
        em.flush();
        pokemon.setTipo1(tipo1);
        return pokemon;
    }

    @Before
    public void initTest() {
        pokemon = createEntity(em);
    }

    @Test
    @Transactional
    public void createPokemon() throws Exception {
        int databaseSizeBeforeCreate = pokemonRepository.findAll().size();

        // Create the Pokemon

        restPokemonMockMvc.perform(post("/api/pokemons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pokemon)))
                .andExpect(status().isCreated());

        // Validate the Pokemon in the database
        List<Pokemon> pokemons = pokemonRepository.findAll();
        assertThat(pokemons).hasSize(databaseSizeBeforeCreate + 1);
        Pokemon testPokemon = pokemons.get(pokemons.size() - 1);
        assertThat(testPokemon.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testPokemon.getDescripcion()).isEqualTo(DEFAULT_DESCRIPCION);
        assertThat(testPokemon.isFavorito()).isEqualTo(DEFAULT_FAVORITO);
    }

    @Test
    @Transactional
    public void checkNombreIsRequired() throws Exception {
        int databaseSizeBeforeTest = pokemonRepository.findAll().size();
        // set the field null
        pokemon.setNombre(null);

        // Create the Pokemon, which fails.

        restPokemonMockMvc.perform(post("/api/pokemons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pokemon)))
                .andExpect(status().isBadRequest());

        List<Pokemon> pokemons = pokemonRepository.findAll();
        assertThat(pokemons).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDescripcionIsRequired() throws Exception {
        int databaseSizeBeforeTest = pokemonRepository.findAll().size();
        // set the field null
        pokemon.setDescripcion(null);

        // Create the Pokemon, which fails.

        restPokemonMockMvc.perform(post("/api/pokemons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pokemon)))
                .andExpect(status().isBadRequest());

        List<Pokemon> pokemons = pokemonRepository.findAll();
        assertThat(pokemons).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPokemons() throws Exception {
        // Initialize the database
        pokemonRepository.saveAndFlush(pokemon);

        // Get all the pokemons
        restPokemonMockMvc.perform(get("/api/pokemons?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(pokemon.getId().intValue())))
                .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE.toString())))
                .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION.toString())))
                .andExpect(jsonPath("$.[*].favorito").value(hasItem(DEFAULT_FAVORITO.booleanValue())));
    }

    @Test
    @Transactional
    public void getPokemon() throws Exception {
        // Initialize the database
        pokemonRepository.saveAndFlush(pokemon);

        // Get the pokemon
        restPokemonMockMvc.perform(get("/api/pokemons/{id}", pokemon.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(pokemon.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE.toString()))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION.toString()))
            .andExpect(jsonPath("$.favorito").value(DEFAULT_FAVORITO.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingPokemon() throws Exception {
        // Get the pokemon
        restPokemonMockMvc.perform(get("/api/pokemons/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePokemon() throws Exception {
        // Initialize the database
        pokemonRepository.saveAndFlush(pokemon);
        int databaseSizeBeforeUpdate = pokemonRepository.findAll().size();

        // Update the pokemon
        Pokemon updatedPokemon = pokemonRepository.findOne(pokemon.getId());
        updatedPokemon
                .nombre(UPDATED_NOMBRE)
                .descripcion(UPDATED_DESCRIPCION)
                .favorito(UPDATED_FAVORITO);

        restPokemonMockMvc.perform(put("/api/pokemons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedPokemon)))
                .andExpect(status().isOk());

        // Validate the Pokemon in the database
        List<Pokemon> pokemons = pokemonRepository.findAll();
        assertThat(pokemons).hasSize(databaseSizeBeforeUpdate);
        Pokemon testPokemon = pokemons.get(pokemons.size() - 1);
        assertThat(testPokemon.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testPokemon.getDescripcion()).isEqualTo(UPDATED_DESCRIPCION);
        assertThat(testPokemon.isFavorito()).isEqualTo(UPDATED_FAVORITO);
    }

    @Test
    @Transactional
    public void deletePokemon() throws Exception {
        // Initialize the database
        pokemonRepository.saveAndFlush(pokemon);
        int databaseSizeBeforeDelete = pokemonRepository.findAll().size();

        // Get the pokemon
        restPokemonMockMvc.perform(delete("/api/pokemons/{id}", pokemon.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Pokemon> pokemons = pokemonRepository.findAll();
        assertThat(pokemons).hasSize(databaseSizeBeforeDelete - 1);
    }
}

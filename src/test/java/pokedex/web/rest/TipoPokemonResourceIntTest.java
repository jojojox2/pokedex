package pokedex.web.rest;

import pokedex.PokedexApp;

import pokedex.domain.TipoPokemon;
import pokedex.repository.TipoPokemonRepository;

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
 * Test class for the TipoPokemonResource REST controller.
 *
 * @see TipoPokemonResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PokedexApp.class)
public class TipoPokemonResourceIntTest {

    private static final String DEFAULT_TIPO = "AAAAA";
    private static final String UPDATED_TIPO = "BBBBB";

    @Inject
    private TipoPokemonRepository tipoPokemonRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restTipoPokemonMockMvc;

    private TipoPokemon tipoPokemon;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TipoPokemonResource tipoPokemonResource = new TipoPokemonResource();
        ReflectionTestUtils.setField(tipoPokemonResource, "tipoPokemonRepository", tipoPokemonRepository);
        this.restTipoPokemonMockMvc = MockMvcBuilders.standaloneSetup(tipoPokemonResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TipoPokemon createEntity(EntityManager em) {
        TipoPokemon tipoPokemon = new TipoPokemon()
                .tipo(DEFAULT_TIPO);
        return tipoPokemon;
    }

    @Before
    public void initTest() {
        tipoPokemon = createEntity(em);
    }

    @Test
    @Transactional
    public void createTipoPokemon() throws Exception {
        int databaseSizeBeforeCreate = tipoPokemonRepository.findAll().size();

        // Create the TipoPokemon

        restTipoPokemonMockMvc.perform(post("/api/tipo-pokemons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tipoPokemon)))
                .andExpect(status().isCreated());

        // Validate the TipoPokemon in the database
        List<TipoPokemon> tipoPokemons = tipoPokemonRepository.findAll();
        assertThat(tipoPokemons).hasSize(databaseSizeBeforeCreate + 1);
        TipoPokemon testTipoPokemon = tipoPokemons.get(tipoPokemons.size() - 1);
        assertThat(testTipoPokemon.getTipo()).isEqualTo(DEFAULT_TIPO);
    }

    @Test
    @Transactional
    public void checkTipoIsRequired() throws Exception {
        int databaseSizeBeforeTest = tipoPokemonRepository.findAll().size();
        // set the field null
        tipoPokemon.setTipo(null);

        // Create the TipoPokemon, which fails.

        restTipoPokemonMockMvc.perform(post("/api/tipo-pokemons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tipoPokemon)))
                .andExpect(status().isBadRequest());

        List<TipoPokemon> tipoPokemons = tipoPokemonRepository.findAll();
        assertThat(tipoPokemons).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTipoPokemons() throws Exception {
        // Initialize the database
        tipoPokemonRepository.saveAndFlush(tipoPokemon);

        // Get all the tipoPokemons
        restTipoPokemonMockMvc.perform(get("/api/tipo-pokemons?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(tipoPokemon.getId().intValue())))
                .andExpect(jsonPath("$.[*].tipo").value(hasItem(DEFAULT_TIPO.toString())));
    }

    @Test
    @Transactional
    public void getTipoPokemon() throws Exception {
        // Initialize the database
        tipoPokemonRepository.saveAndFlush(tipoPokemon);

        // Get the tipoPokemon
        restTipoPokemonMockMvc.perform(get("/api/tipo-pokemons/{id}", tipoPokemon.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(tipoPokemon.getId().intValue()))
            .andExpect(jsonPath("$.tipo").value(DEFAULT_TIPO.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTipoPokemon() throws Exception {
        // Get the tipoPokemon
        restTipoPokemonMockMvc.perform(get("/api/tipo-pokemons/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTipoPokemon() throws Exception {
        // Initialize the database
        tipoPokemonRepository.saveAndFlush(tipoPokemon);
        int databaseSizeBeforeUpdate = tipoPokemonRepository.findAll().size();

        // Update the tipoPokemon
        TipoPokemon updatedTipoPokemon = tipoPokemonRepository.findOne(tipoPokemon.getId());
        updatedTipoPokemon
                .tipo(UPDATED_TIPO);

        restTipoPokemonMockMvc.perform(put("/api/tipo-pokemons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedTipoPokemon)))
                .andExpect(status().isOk());

        // Validate the TipoPokemon in the database
        List<TipoPokemon> tipoPokemons = tipoPokemonRepository.findAll();
        assertThat(tipoPokemons).hasSize(databaseSizeBeforeUpdate);
        TipoPokemon testTipoPokemon = tipoPokemons.get(tipoPokemons.size() - 1);
        assertThat(testTipoPokemon.getTipo()).isEqualTo(UPDATED_TIPO);
    }

    @Test
    @Transactional
    public void deleteTipoPokemon() throws Exception {
        // Initialize the database
        tipoPokemonRepository.saveAndFlush(tipoPokemon);
        int databaseSizeBeforeDelete = tipoPokemonRepository.findAll().size();

        // Get the tipoPokemon
        restTipoPokemonMockMvc.perform(delete("/api/tipo-pokemons/{id}", tipoPokemon.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<TipoPokemon> tipoPokemons = tipoPokemonRepository.findAll();
        assertThat(tipoPokemons).hasSize(databaseSizeBeforeDelete - 1);
    }
}

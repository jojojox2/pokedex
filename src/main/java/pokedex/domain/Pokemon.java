package pokedex.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Pokemon.
 */
@Entity
@Table(name = "pokemon")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Pokemon implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min = 4, max = 24)
    @Column(name = "nombre", length = 24, nullable = false)
    private String nombre;

    @NotNull
    @Size(min = 30)
    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @Column(name = "favorito")
    private Boolean favorito;

    @ManyToOne
    @NotNull
    private TipoPokemon tipo1;

    @ManyToOne
    private TipoPokemon tipo2;

    @ManyToOne
    private Pokemon evolucion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public Pokemon nombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Pokemon descripcion(String descripcion) {
        this.descripcion = descripcion;
        return this;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean isFavorito() {
        return favorito;
    }

    public Pokemon favorito(Boolean favorito) {
        this.favorito = favorito;
        return this;
    }

    public void setFavorito(Boolean favorito) {
        this.favorito = favorito;
    }

    public TipoPokemon getTipo1() {
        return tipo1;
    }

    public Pokemon tipo1(TipoPokemon tipoPokemon) {
        this.tipo1 = tipoPokemon;
        return this;
    }

    public void setTipo1(TipoPokemon tipoPokemon) {
        this.tipo1 = tipoPokemon;
    }

    public TipoPokemon getTipo2() {
        return tipo2;
    }

    public Pokemon tipo2(TipoPokemon tipoPokemon) {
        this.tipo2 = tipoPokemon;
        return this;
    }

    public void setTipo2(TipoPokemon tipoPokemon) {
        this.tipo2 = tipoPokemon;
    }

    public Pokemon getEvolucion() {
        return evolucion;
    }

    public Pokemon evolucion(Pokemon pokemon) {
        this.evolucion = pokemon;
        return this;
    }

    public void setEvolucion(Pokemon pokemon) {
        this.evolucion = pokemon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pokemon pokemon = (Pokemon) o;
        if(pokemon.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, pokemon.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Pokemon{" +
            "id=" + id +
            ", nombre='" + nombre + "'" +
            ", descripcion='" + descripcion + "'" +
            ", favorito='" + favorito + "'" +
            '}';
    }
}

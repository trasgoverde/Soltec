package com.blocknitive.com.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Terrain.
 */
@Document(collection = "terrain")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "terrain")
public class Terrain implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("energy_for_community")
    private Integer energyForCommunity;

    @Field("re_inversion")
    private Integer reInversion;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Terrain id(String id) {
        this.id = id;
        return this;
    }

    public Integer getEnergyForCommunity() {
        return this.energyForCommunity;
    }

    public Terrain energyForCommunity(Integer energyForCommunity) {
        this.energyForCommunity = energyForCommunity;
        return this;
    }

    public void setEnergyForCommunity(Integer energyForCommunity) {
        this.energyForCommunity = energyForCommunity;
    }

    public Integer getReInversion() {
        return this.reInversion;
    }

    public Terrain reInversion(Integer reInversion) {
        this.reInversion = reInversion;
        return this;
    }

    public void setReInversion(Integer reInversion) {
        this.reInversion = reInversion;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Terrain)) {
            return false;
        }
        return id != null && id.equals(((Terrain) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Terrain{" +
            "id=" + getId() +
            ", energyForCommunity=" + getEnergyForCommunity() +
            ", reInversion=" + getReInversion() +
            "}";
    }
}

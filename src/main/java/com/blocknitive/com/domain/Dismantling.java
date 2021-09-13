package com.blocknitive.com.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Dismantling.
 */
@Document(collection = "dismantling")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "dismantling")
public class Dismantling implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("guarantee_dismantling")
    private Integer guaranteeDismantling;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Dismantling id(String id) {
        this.id = id;
        return this;
    }

    public Integer getGuaranteeDismantling() {
        return this.guaranteeDismantling;
    }

    public Dismantling guaranteeDismantling(Integer guaranteeDismantling) {
        this.guaranteeDismantling = guaranteeDismantling;
        return this;
    }

    public void setGuaranteeDismantling(Integer guaranteeDismantling) {
        this.guaranteeDismantling = guaranteeDismantling;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Dismantling)) {
            return false;
        }
        return id != null && id.equals(((Dismantling) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Dismantling{" +
            "id=" + getId() +
            ", guaranteeDismantling=" + getGuaranteeDismantling() +
            "}";
    }
}

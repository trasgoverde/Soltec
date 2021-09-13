package com.blocknitive.com.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Logistics.
 */
@Document(collection = "logistics")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "logistics")
public class Logistics implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("co_2_emitions")
    private Integer co2Emitions;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Logistics id(String id) {
        this.id = id;
        return this;
    }

    public Integer getCo2Emitions() {
        return this.co2Emitions;
    }

    public Logistics co2Emitions(Integer co2Emitions) {
        this.co2Emitions = co2Emitions;
        return this;
    }

    public void setCo2Emitions(Integer co2Emitions) {
        this.co2Emitions = co2Emitions;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Logistics)) {
            return false;
        }
        return id != null && id.equals(((Logistics) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Logistics{" +
            "id=" + getId() +
            ", co2Emitions=" + getCo2Emitions() +
            "}";
    }
}

package com.blocknitive.com.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A HumanResources.
 */
@Document(collection = "human_resources")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "humanresources")
public class HumanResources implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("investments_locally")
    private Integer investmentsLocally;

    @Field("labor_accidentsindex")
    private Integer laborAccidentsindex;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HumanResources id(String id) {
        this.id = id;
        return this;
    }

    public Integer getInvestmentsLocally() {
        return this.investmentsLocally;
    }

    public HumanResources investmentsLocally(Integer investmentsLocally) {
        this.investmentsLocally = investmentsLocally;
        return this;
    }

    public void setInvestmentsLocally(Integer investmentsLocally) {
        this.investmentsLocally = investmentsLocally;
    }

    public Integer getLaborAccidentsindex() {
        return this.laborAccidentsindex;
    }

    public HumanResources laborAccidentsindex(Integer laborAccidentsindex) {
        this.laborAccidentsindex = laborAccidentsindex;
        return this;
    }

    public void setLaborAccidentsindex(Integer laborAccidentsindex) {
        this.laborAccidentsindex = laborAccidentsindex;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HumanResources)) {
            return false;
        }
        return id != null && id.equals(((HumanResources) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HumanResources{" +
            "id=" + getId() +
            ", investmentsLocally=" + getInvestmentsLocally() +
            ", laborAccidentsindex=" + getLaborAccidentsindex() +
            "}";
    }
}

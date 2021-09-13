package com.blocknitive.com.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Resources.
 */
@Document(collection = "resources")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "resources")
public class Resources implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("water_consumtion")
    private Integer waterConsumtion;

    @Field("reforestry_index")
    private Integer reforestryIndex;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Resources id(String id) {
        this.id = id;
        return this;
    }

    public Integer getWaterConsumtion() {
        return this.waterConsumtion;
    }

    public Resources waterConsumtion(Integer waterConsumtion) {
        this.waterConsumtion = waterConsumtion;
        return this;
    }

    public void setWaterConsumtion(Integer waterConsumtion) {
        this.waterConsumtion = waterConsumtion;
    }

    public Integer getReforestryIndex() {
        return this.reforestryIndex;
    }

    public Resources reforestryIndex(Integer reforestryIndex) {
        this.reforestryIndex = reforestryIndex;
        return this;
    }

    public void setReforestryIndex(Integer reforestryIndex) {
        this.reforestryIndex = reforestryIndex;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Resources)) {
            return false;
        }
        return id != null && id.equals(((Resources) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Resources{" +
            "id=" + getId() +
            ", waterConsumtion=" + getWaterConsumtion() +
            ", reforestryIndex=" + getReforestryIndex() +
            "}";
    }
}

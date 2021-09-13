package com.blocknitive.com.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Teams.
 */
@Document(collection = "teams")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "teams")
public class Teams implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("origin_materials")
    private Integer originMaterials;

    @Field("origin_steal")
    private Integer originSteal;

    @Field("origin_aluminium")
    private Integer originAluminium;

    @Field("sustainable_providers")
    private Boolean sustainableProviders;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Teams id(String id) {
        this.id = id;
        return this;
    }

    public Integer getOriginMaterials() {
        return this.originMaterials;
    }

    public Teams originMaterials(Integer originMaterials) {
        this.originMaterials = originMaterials;
        return this;
    }

    public void setOriginMaterials(Integer originMaterials) {
        this.originMaterials = originMaterials;
    }

    public Integer getOriginSteal() {
        return this.originSteal;
    }

    public Teams originSteal(Integer originSteal) {
        this.originSteal = originSteal;
        return this;
    }

    public void setOriginSteal(Integer originSteal) {
        this.originSteal = originSteal;
    }

    public Integer getOriginAluminium() {
        return this.originAluminium;
    }

    public Teams originAluminium(Integer originAluminium) {
        this.originAluminium = originAluminium;
        return this;
    }

    public void setOriginAluminium(Integer originAluminium) {
        this.originAluminium = originAluminium;
    }

    public Boolean getSustainableProviders() {
        return this.sustainableProviders;
    }

    public Teams sustainableProviders(Boolean sustainableProviders) {
        this.sustainableProviders = sustainableProviders;
        return this;
    }

    public void setSustainableProviders(Boolean sustainableProviders) {
        this.sustainableProviders = sustainableProviders;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Teams)) {
            return false;
        }
        return id != null && id.equals(((Teams) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Teams{" +
            "id=" + getId() +
            ", originMaterials=" + getOriginMaterials() +
            ", originSteal=" + getOriginSteal() +
            ", originAluminium=" + getOriginAluminium() +
            ", sustainableProviders='" + getSustainableProviders() + "'" +
            "}";
    }
}

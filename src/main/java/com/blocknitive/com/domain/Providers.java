package com.blocknitive.com.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Providers.
 */
@Document(collection = "providers")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "providers")
public class Providers implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("agreement_paris")
    private Integer agreementParis;

    @Field("certified_sustianable")
    private Boolean certifiedSustianable;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Providers id(String id) {
        this.id = id;
        return this;
    }

    public Integer getAgreementParis() {
        return this.agreementParis;
    }

    public Providers agreementParis(Integer agreementParis) {
        this.agreementParis = agreementParis;
        return this;
    }

    public void setAgreementParis(Integer agreementParis) {
        this.agreementParis = agreementParis;
    }

    public Boolean getCertifiedSustianable() {
        return this.certifiedSustianable;
    }

    public Providers certifiedSustianable(Boolean certifiedSustianable) {
        this.certifiedSustianable = certifiedSustianable;
        return this;
    }

    public void setCertifiedSustianable(Boolean certifiedSustianable) {
        this.certifiedSustianable = certifiedSustianable;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Providers)) {
            return false;
        }
        return id != null && id.equals(((Providers) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Providers{" +
            "id=" + getId() +
            ", agreementParis=" + getAgreementParis() +
            ", certifiedSustianable='" + getCertifiedSustianable() + "'" +
            "}";
    }
}

package com.blocknitive.com.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Machinery.
 */
@Document(collection = "machinery")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "machinery")
public class Machinery implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("payment_cycle")
    private Integer paymentCycle;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Machinery id(String id) {
        this.id = id;
        return this;
    }

    public Integer getPaymentCycle() {
        return this.paymentCycle;
    }

    public Machinery paymentCycle(Integer paymentCycle) {
        this.paymentCycle = paymentCycle;
        return this;
    }

    public void setPaymentCycle(Integer paymentCycle) {
        this.paymentCycle = paymentCycle;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Machinery)) {
            return false;
        }
        return id != null && id.equals(((Machinery) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Machinery{" +
            "id=" + getId() +
            ", paymentCycle=" + getPaymentCycle() +
            "}";
    }
}

package mk.ukim.finki.wp.commonmodel.product;

import jakarta.persistence.*;
import lombok.Getter;
import mk.ukim.finki.wp.commonmodel.base.AbstractEntity;
import mk.ukim.finki.wp.commonmodel.valueobjects.*;
import java.time.ZonedDateTime;
import static org.apache.commons.lang3.Validate.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import static org.apache.commons.lang3.Validate.*;

@Getter
@Entity
public class Product extends AbstractEntity<ProductId> {

    @Embedded
    @AttributeOverride(name = "name", column = @Column(name = "product_name"))
    private Name name;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "price_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "price_currency"))
    })
    private Money price;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "quantity"))
    private Quantity quantity;

    private ZonedDateTime dateOfProduction;
    private ZonedDateTime dateOfExpiry;

    public Product(Name name, Money price, Quantity quantity) {
        super(new ProductId());
        this.name = notNull(name, "name must not be null");
        this.price = notNull(price, "price must not be null");
        this.quantity = notNull(quantity, "quantity must not be null");
    }

    protected Product() {}

    public void updateProductDateOfProduction(ZonedDateTime dateOfProduction) {
        notNull(dateOfProduction, "dateOfProduction must not be null");
        isTrue(dateOfProduction.isBefore(ZonedDateTime.now()), "dateOfProduction must be in the past");
        this.dateOfProduction = dateOfProduction;
        this.checkInvariants();
    }
    public void clearDateOfProduction() {
        this.dateOfProduction = null;
        this.dateOfExpiry = null;
        this.checkInvariants();
    }
    public void updateProductName(Name name) {
        this.name = notNull(name, "name must not be null");
    }
    public Product withDateOfProduction(ZonedDateTime dateOfProduction) {
        notNull(dateOfProduction, "dateOfProduction must not be null");
        isTrue(dateOfProduction.isBefore(ZonedDateTime.now()),
                "dateOfProduction must be in the past");
        this.dateOfProduction = dateOfProduction;
        this.checkInvariants();
        return this;
    }

    public Product withDateOfExpiry(ZonedDateTime dateOfExpiry) {
        notNull(dateOfExpiry, "dateOfExpiry must not be null");
        isTrue(dateOfExpiry.isAfter(ZonedDateTime.now()),
                "dateOfExpiry must be in the future");
        this.dateOfExpiry = dateOfExpiry;
        this.checkInvariants();
        return this;
    }
    private void checkInvariants() {
        validState((dateOfProduction == null && dateOfExpiry == null)
                        || (dateOfProduction != null && dateOfExpiry == null)
                        || (dateOfProduction != null && dateOfProduction.isBefore(dateOfExpiry)),
                "dateOfProduction must be before dateOfExpiry");
    }
    public void updateProductDateOfExpiry(ZonedDateTime dateOfExpiry) {
        notNull(dateOfExpiry, "dateOfExpiry must not be null");
        isTrue(dateOfExpiry.isAfter(ZonedDateTime.now()), "dateOfExpiry must be in the future");
        this.dateOfExpiry = dateOfExpiry;
        this.checkInvariants();
    }

    public void clearDateOfExpiry() {
        this.dateOfExpiry = null;
        this.checkInvariants();
    }
    public static class Builder {
        private Product product;

        public Builder(Name name, Money price, Quantity quantity) {
            this.product = new Product(name, price, quantity);
        }

        public Builder withDateOfProduction(ZonedDateTime dateOfProduction) {
            this.product.dateOfProduction = dateOfProduction;
            return this;
        }

        public Builder withDateOfExpiry(ZonedDateTime dateOfExpiry) {
            this.product.dateOfExpiry = dateOfExpiry;
            return this;
        }

        public Product build() {
            validState(product != null, "product must not be null");
            product.checkInvariants();
            Product result = product;
            product = null;
            return result;
        }
    }
}
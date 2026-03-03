package mk.ukim.finki.wp.commonmodel.product;

import jakarta.persistence.*;
import lombok.Getter;
import mk.ukim.finki.wp.commonmodel.base.AbstractEntity;
import mk.ukim.finki.wp.commonmodel.valueobjects.*;
import java.time.ZonedDateTime;
import static org.apache.commons.lang3.Validate.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
    }
    public void clearDateOfProduction() {
        this.dateOfProduction = null;
    }
    public void updateProductName(Name name) {
        this.name = notNull(name, "name must not be null");
    }
}
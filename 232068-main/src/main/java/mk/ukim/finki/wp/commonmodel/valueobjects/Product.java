package mk.ukim.finki.wp.commonmodel.product;

import jakarta.persistence.*;
import lombok.Getter;
import mk.ukim.finki.wp.commonmodel.base.AbstractEntity;
import mk.ukim.finki.wp.commonmodel.valueobjects.*;
import java.time.ZonedDateTime;
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

    public Product(Name name, Money price, Quantity quantity) {
        super(new ProductId());
        this.name = notNull(name, "name must not be null");
        this.price = notNull(price, "price must not be null");
        this.quantity = notNull(quantity, "quantity must not be null");
    }

    protected Product() {}

    public void updateProductDateOfProduction(ZonedDateTime dateOfProduction) {
        isTrue(dateOfProduction == null ||
                        dateOfProduction.isBefore(ZonedDateTime.now()),
                "Date of production must not be in the future");
        this.dateOfProduction = dateOfProduction;
    }
}
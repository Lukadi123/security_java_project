package mk.ukim.finki.wp.commonmodel.order;

import jakarta.persistence.*;
import lombok.Getter;
import mk.ukim.finki.wp.commonmodel.base.AbstractEntity;
import mk.ukim.finki.wp.commonmodel.valueobjects.ProductId;
import mk.ukim.finki.wp.commonmodel.valueobjects.ShoppingCartId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

@Getter
@Entity
public class ShoppingCart extends AbstractEntity<ShoppingCartId> {

    @Enumerated(EnumType.STRING)
    private ShoppingCartStatus status;

    @ElementCollection
    private List<ProductId> products;

    public ShoppingCart() {
        super(new ShoppingCartId());
        this.status = ShoppingCartStatus.CREATED;
        this.products = new ArrayList<>();
    }

    protected ShoppingCart(boolean jpa) {
        super();
    }

    public void addProduct(ProductId productId) {
        notNull(productId, "productId must not be null");
        isTrue(status == ShoppingCartStatus.CREATED,
                "Cannot add products: cart must be in CREATED status, but was " + status);
        products.add(productId);
    }

    public void removeProduct(ProductId productId) {
        notNull(productId, "productId must not be null");
        isTrue(status == ShoppingCartStatus.CREATED,
                "Cannot remove products: cart must be in CREATED status, but was " + status);
        products.remove(productId);
    }

    public void clearProducts() {
        isTrue(status == ShoppingCartStatus.CREATED,
                "Cannot clear products: cart must be in CREATED status, but was " + status);
        products.clear();
    }

    public void cancel() {
        isTrue(status == ShoppingCartStatus.CREATED,
                "Cannot cancel: cart must be in CREATED status, but was " + status);
        this.status = ShoppingCartStatus.CANCELED;
    }

    public Order submit() {
        isTrue(status == ShoppingCartStatus.CREATED,
                "Cannot submit: cart must be in CREATED status, but was " + status);
        isTrue(!products.isEmpty(),
                "Cannot submit: cart must have at least one product");
        this.status = ShoppingCartStatus.SUBMITTED;
        return new Order(List.copyOf(products));
    }

    public List<ProductId> products() {
        return Collections.unmodifiableList(products);
    }
}
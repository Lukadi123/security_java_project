package mk.ukim.finki.wp.commonmodel.order;

import jakarta.persistence.*;
import lombok.Getter;
import mk.ukim.finki.wp.commonmodel.base.AbstractEntity;
import mk.ukim.finki.wp.commonmodel.valueobjects.OrderId;
import mk.ukim.finki.wp.commonmodel.valueobjects.ProductId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

@Getter
@Entity
@Table(name = "orders")
public class Order extends AbstractEntity<OrderId> {

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private boolean isPaid;

    @ElementCollection
    private List<ProductId> products;

    public Order() {
        super(new OrderId());
        this.status = OrderStatus.CREATED;
        this.isPaid = false;
        this.products = new ArrayList<>();
    }

    protected Order(boolean jpa) {
        super();
    }

    // --- Product management (only in CREATED) ---

    public void addProduct(ProductId productId) {
        notNull(productId, "productId must not be null");
        isTrue(status == OrderStatus.CREATED,
                "Cannot add products: order must be in CREATED status, but was " + status);
        products.add(productId);
    }

    public void removeProduct(ProductId productId) {
        notNull(productId, "productId must not be null");
        isTrue(status == OrderStatus.CREATED,
                "Cannot remove products: order must be in CREATED status, but was " + status);
        products.remove(productId);
    }

    public void clearProducts() {
        isTrue(status == OrderStatus.CREATED,
                "Cannot clear products: order must be in CREATED status, but was " + status);
        products.clear();
    }

    // --- Lifecycle transitions ---

    public void cancel() {
        isTrue(status == OrderStatus.CREATED,
                "Cannot cancel: order must be in CREATED status, but was " + status);
        this.status = OrderStatus.CANCELED;
    }

    public void submit() {
        isTrue(status == OrderStatus.CREATED,
                "Cannot submit: order must be in CREATED status, but was " + status);
        isTrue(!products.isEmpty(),
                "Cannot submit: order must have at least one product");
        this.status = OrderStatus.SUBMITTED;
    }

    public void process() {
        isTrue(status == OrderStatus.SUBMITTED,
                "Cannot process: order must be in SUBMITTED status, but was " + status);
        this.status = OrderStatus.PROCESSED;
    }

    public void pay() {
        isTrue(status == OrderStatus.PROCESSED || status == OrderStatus.IN_TRANSPORT,
                "Cannot pay: order must be in PROCESSED or IN_TRANSPORT status, but was " + status);
        this.isPaid = true;
    }

    public void beginTransport() {
        isTrue(status == OrderStatus.PROCESSED,
                "Cannot begin transport: order must be in PROCESSED status, but was " + status);
        this.status = OrderStatus.IN_TRANSPORT;
    }

    public void deliver() {
        isTrue(status == OrderStatus.IN_TRANSPORT,
                "Cannot deliver: order must be in IN_TRANSPORT status, but was " + status);
        isTrue(isPaid,
                "Cannot deliver: order must be paid before delivery");
        this.status = OrderStatus.DELIVERED;
    }

    public List<ProductId> products() {
        return Collections.unmodifiableList(products);
    }
}

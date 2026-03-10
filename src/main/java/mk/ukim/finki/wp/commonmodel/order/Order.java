package mk.ukim.finki.wp.commonmodel.order;

import jakarta.persistence.*;
import lombok.Getter;
import mk.ukim.finki.wp.commonmodel.base.AbstractEntity;
import mk.ukim.finki.wp.commonmodel.valueobjects.OrderId;
import mk.ukim.finki.wp.commonmodel.valueobjects.ProductId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;

@Getter
@Entity
@Table(name = "orders")
public class Order extends AbstractEntity<OrderId> {

    @Embedded
    private OrderState state;

    @ElementCollection
    private List<ProductId> products;

    public Order() {
        super(new OrderId());
        this.state = new OrderState();
        this.products = new ArrayList<>();
    }

    protected Order(boolean jpa) {
        super();
    }

    public void addProduct(ProductId productId) {
        state.modifyProducts();
        notNull(productId, "productId must not be null");
        products.add(productId);
    }

    public void removeProduct(ProductId productId) {
        state.modifyProducts();
        notNull(productId, "productId must not be null");
        products.remove(productId);
    }

    public void clearProducts() {
        state.modifyProducts();
        products.clear();
    }

    public void cancel() {
        state.cancel();
    }

    public void submit() {
        state.submit(!products.isEmpty());
    }

    public void process() {
        state.process();
    }

    public void pay() {
        state.pay();
    }

    public void beginTransport() {
        state.beginTransport();
    }

    public void deliver() {
        state.deliver();
    }

    public List<ProductId> products() {
        return Collections.unmodifiableList(products);
    }
}
package mk.ukim.finki.wp.commonmodel.order;

import jakarta.persistence.*;
import lombok.Getter;
import mk.ukim.finki.wp.commonmodel.base.AbstractEntity;
import mk.ukim.finki.wp.commonmodel.valueobjects.OrderId;
import mk.ukim.finki.wp.commonmodel.valueobjects.ProductId;

import java.util.Collections;
import java.util.List;

@Getter
@Entity
@Table(name = "orders")
public class Order extends AbstractEntity<OrderId> {

    @Embedded
    private OrderState state;

    @ElementCollection
    private List<ProductId> products;

    protected Order(List<ProductId> products) {
        super(new OrderId());
        this.state = new OrderState();
        this.products = products;
    }

    protected Order() {
        super();
    }

    public void cancel() {
        state.cancel();
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
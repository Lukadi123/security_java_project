package mk.ukim.finki.wp.commonmodel.order;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import mk.ukim.finki.wp.commonmodel.base.ValueObject;

import static org.apache.commons.lang3.Validate.isTrue;

@Embeddable
public class OrderState implements ValueObject {

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private boolean isPaid;

    public OrderState() {
        this.orderStatus = OrderStatus.CREATED;
        this.isPaid = false;
    }

    protected OrderState(boolean jpa) {}

    public void modifyProducts() {
        isTrue(orderStatus == OrderStatus.CREATED,
                "Cannot modify products: order must be in CREATED status, but was " + orderStatus);
    }

    public void cancel() {
        isTrue(orderStatus == OrderStatus.CREATED,
                "Cannot cancel: order must be in CREATED status, but was " + orderStatus);
        this.orderStatus = OrderStatus.CANCELED;
    }

    public void submit(boolean hasProducts) {
        isTrue(orderStatus == OrderStatus.CREATED,
                "Cannot submit: order must be in CREATED status, but was " + orderStatus);
        isTrue(hasProducts,
                "Cannot submit: order must have at least one product");
        this.orderStatus = OrderStatus.SUBMITTED;
    }

    public void process() {
        isTrue(orderStatus == OrderStatus.SUBMITTED,
                "Cannot process: order must be in SUBMITTED status, but was " + orderStatus);
        this.orderStatus = OrderStatus.PROCESSED;
    }

    public void pay() {
        isTrue(orderStatus == OrderStatus.PROCESSED || orderStatus == OrderStatus.IN_TRANSPORT,
                "Cannot pay: order must be in PROCESSED or IN_TRANSPORT status, but was " + orderStatus);
        this.isPaid = true;
    }

    public void beginTransport() {
        isTrue(orderStatus == OrderStatus.PROCESSED,
                "Cannot begin transport: order must be in PROCESSED status, but was " + orderStatus);
        this.orderStatus = OrderStatus.IN_TRANSPORT;
    }

    public void deliver() {
        isTrue(orderStatus == OrderStatus.IN_TRANSPORT,
                "Cannot deliver: order must be in IN_TRANSPORT status, but was " + orderStatus);
        isTrue(isPaid,
                "Cannot deliver: order must be paid before delivery");
        this.orderStatus = OrderStatus.DELIVERED;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public boolean isPaid() {
        return isPaid;
    }
}
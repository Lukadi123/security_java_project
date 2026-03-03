package mk.ukim.finki.wp.commonmodel.valueobjects;

import jakarta.persistence.Embeddable;
import mk.ukim.finki.wp.commonmodel.base.ValueObject;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;
import static org.apache.commons.lang3.Validate.*;

@Embeddable
public class Money implements ValueObject {
    private Currency currency;
    private BigDecimal amount;

    public Money(Currency currency, BigDecimal amount) {
        notNull(currency, "Currency must not be null");
        notNull(amount, "Amount must not be null");
        isTrue(amount.compareTo(BigDecimal.ZERO) >= 0, "Amount must not be negative");
        this.currency = currency;
        this.amount = amount;
    }

    protected Money() {}

    public Currency getCurrency() { return currency; }
    public BigDecimal getAmount() { return amount; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(currency, money.currency) && Objects.equals(amount, money.amount);
    }

    @Override
    public int hashCode() { return Objects.hash(currency, amount); }

    @Override
    public String toString() { return amount + " " + currency; }
}
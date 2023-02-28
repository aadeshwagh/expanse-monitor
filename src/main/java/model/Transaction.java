package model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@ToString
public class Transaction {
    private String date;
    private String protocol;
    private String type;
    private String name;
    private String tnxId;
    private Double debit;
    private Double credit;
    private Double balance;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return date.equals(that.date) && Objects.equals(protocol, that.protocol) && Objects.equals(type, that.type) && Objects.equals(name, that.name) && tnxId.equals(that.tnxId) && Objects.equals(debit, that.debit) && Objects.equals(credit, that.credit) && Objects.equals(balance, that.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, protocol, type, name, tnxId, debit, credit, balance);
    }
}

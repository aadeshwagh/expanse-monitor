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
        return tnxId.equals(that.tnxId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tnxId);
    }
}

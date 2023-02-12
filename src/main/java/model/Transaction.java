package model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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

}

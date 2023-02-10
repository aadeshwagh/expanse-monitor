package model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
@Getter
@Setter
@ToString
public class Transaction {
    private String date;
    private String protocol;
    private String type;
    private String name;
    private String tnxId;
    private Double withdrawal;
    private Double deposit;
    private Double balance;

}

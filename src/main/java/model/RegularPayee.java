package model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RegularPayee {
    private String name;
    private String type;
    private double totalDebited;
    private double totalReceived;
    private double avgPayment;

}

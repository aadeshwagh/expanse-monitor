package model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class MonthlyExpenseSummary {
    private String monthYear;
    private List<Transaction> merchantPayments;
    private List<Transaction> personPayments;
    private List<Transaction> cashWithdrawal;
    private List<Transaction> otherTransactions;

    private double totalMerchantAmount;
    private double totalPersonDebitAmount;
    private double totalPersonCreditAmount;
    private double totalActualPersonDebitAmount;
    private double totalCreditAmountByPersonsOfInterest;
    private double totalCashWithdrawal;
    private double totalExpense;
    private double other;
    private Transaction highestTransaction;
    private double totalSavings;
    private double exceptedExpanse;
    private List<RegularPayee> regulars;
}

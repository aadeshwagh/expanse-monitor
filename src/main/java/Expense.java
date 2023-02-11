import model.RegularPayee;
import model.Result;
import model.Transaction;
import parser.ParsePdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class Expense {
    private final Result result;
    private final Set<Transaction> transactions;

    //people whose created amount should be ignore while calculating total expanse
    private final List<String> personsOfInterest;
    Expense(String statementPath, String password , List<String> personsOfInterest, Double expectedExpense){
        ParsePdf pp = new ParsePdf();
        transactions = pp.parseAxisBankStatement(statementPath,password);
        result =new Result();
        result.setExceptedExpanse(expectedExpense);
        this.personsOfInterest = personsOfInterest;

    }
    Expense(String statementPath, List<String> personsOfInterest, Double expectedExpense){
        ParsePdf pp = new ParsePdf();
        transactions = pp.parseAxisBankStatement(statementPath);
        result =new Result();
        result.setExceptedExpanse(expectedExpense);
        this.personsOfInterest = personsOfInterest;
    }

    public Result getMonthlyExpenseAnalysis(){
        categoriesPayments();
        calculateTotalAmounts();
        calculateTotals();


        return result;
    }

   private void categoriesPayments(){
        List<Transaction> merchant = new ArrayList<>();
        List<Transaction> person = new ArrayList<>();
        List<Transaction> cash = new ArrayList<>();
        List<Transaction> other = new ArrayList<>();

        transactions.forEach(transaction -> {
            if(transaction.getProtocol().toLowerCase().contains("cash"))
                cash.add(transaction);
            else{
                String type = transaction.getType();
                if(type.equals("P2M"))
                    merchant.add(transaction);
                else if (type.equals("P2A") || type.equals("TPFT"))
                    person.add(transaction);
                else
                    other.add(transaction);
            }
        });
    result.setMerchantPayments(merchant);
    result.setPersonPayments(person);
    result.setCashWithdrawal(cash);
    result.setOtherTransactions(other);

    }
    private void calculateTotalAmounts(){
        result.setTotalMerchantAmount(result.getMerchantPayments().stream().mapToDouble(Transaction::getDebit).sum());
        result.setTotalPersonDebitAmount(result.getPersonPayments().stream().filter(pay->pay.getDebit()!=null).mapToDouble(Transaction::getDebit).sum());
        result.setTotalPersonCreditAmount(result.getPersonPayments().stream().filter(pay->pay.getCredit()!=null).mapToDouble(Transaction::getCredit).sum());
        result.setTotalCashWithdrawal(result.getCashWithdrawal().stream().filter(pay->pay.getDebit()!=null).mapToDouble(Transaction::getDebit).sum());
        result.setOther(result.getOtherTransactions().stream().filter(pay->pay.getDebit()!=null).mapToDouble(Transaction::getDebit).sum());

        result.setTotalCreditAmountByPersonsOfInterest(result.getPersonPayments().stream().filter(person->personsOfInterest.stream().anyMatch(person.getName().toLowerCase()::contains)).mapToDouble(Transaction::getCredit).sum());

    }
    private void calculateTotals(){
        double totalExpanse;
        double merchantDebit = result.getTotalMerchantAmount();
        double personDebit = result.getTotalPersonDebitAmount();
        double pOfInterestCredit = result.getTotalCreditAmountByPersonsOfInterest();
        double personCredit = result.getTotalPersonCreditAmount();
        double cashDebit = result.getTotalCashWithdrawal();
        double otherDebit = result.getOther();

        double actualPersonCredit = personCredit - pOfInterestCredit;

        totalExpanse = merchantDebit + personDebit + cashDebit + otherDebit - actualPersonCredit ;

        result.setTotalExpense(totalExpanse);
        result.setTotalSavings(result.getExceptedExpanse() - totalExpanse);


    }
    private void calculateRegulars(){
        List<RegularPayee> regularPayees = new ArrayList<>();
        HashMap<String,Integer> transactionFrequency = new HashMap<>();

       // transactions.forEach();
    }


}

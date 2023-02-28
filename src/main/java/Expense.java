import model.RegularPayee;
import model.MonthlyExpenseSummary;
import model.Transaction;
import parser.ParsePdf;

import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;



public class Expense {
    private final MonthlyExpenseSummary result;
    private final Set<Transaction> transactions;

    //people whose created amount should be ignore while calculating total expanse
    private final List<String> personsOfInterest;
    Expense(String statementPath, String password , List<String> personsOfInterest, Double expectedExpense){
        ParsePdf pp = new ParsePdf();
        transactions = pp.parseAxisBankStatement(statementPath,password);
        result =new MonthlyExpenseSummary();
        result.setExceptedExpanse(expectedExpense);
        this.personsOfInterest = personsOfInterest;

    }
    Expense(String statementPath, List<String> personsOfInterest, Double expectedExpense){
        ParsePdf pp = new ParsePdf();
        transactions = pp.parseAxisBankStatement(statementPath);
        result =new MonthlyExpenseSummary();
        result.setExceptedExpanse(expectedExpense);
        this.personsOfInterest = personsOfInterest;
    }

    public MonthlyExpenseSummary getMonthlyExpenseAnalysis(){
        categoriesPayments();
        calculateTotalAmounts();
        calculateTotals();
        calculateRegulars();
        calculateHighestTransaction();


        return result;
    }

   private void categoriesPayments(){
        List<Transaction> merchant = new ArrayList<>();
        List<Transaction> person = new ArrayList<>();
        List<Transaction> cash = new ArrayList<>();
        List<Transaction> other = new ArrayList<>();

        if(!transactions.isEmpty()){
            String [] date = transactions.stream().toList().get(0).getDate().split("-");
           String month = Month.of(Integer.parseInt(date[1])).name();
            result.setMonthYear(month+"-"+date[2]);
        }

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

        result.setTotalActualPersonDebitAmount(personDebit - actualPersonCredit);

        totalExpanse = merchantDebit + personDebit + cashDebit + otherDebit - actualPersonCredit ;

        result.setTotalExpense(totalExpanse);
        result.setTotalSavings(result.getExceptedExpanse() - totalExpanse);



    }
    private void calculateRegulars(){
        List<RegularPayee> regularPayees = new ArrayList<>();
        HashMap<String,Integer> transactionFrequency = new HashMap<>();

       transactions.forEach(transaction -> {
           transactionFrequency.put(transaction.getName(), transactionFrequency.containsKey(transaction.getName()) ? transactionFrequency.get(transaction.getName()) + 1 : 1);
       });
       transactionFrequency.keySet().forEach(name-> {
           if (transactionFrequency.get(name) >= 3){
               Set<Transaction> payeeTransactions = transactions.stream().filter(transaction -> transaction.getName().equals(name)).collect(Collectors.toSet());
               String type = payeeTransactions.stream().findAny().get().getType();
               long totalPayments = payeeTransactions.stream().filter(transaction -> transaction.getDebit()!=null && transaction.getCredit() ==null ).count();
               double totalPayed = payeeTransactions.stream().filter(transaction -> transaction.getDebit()!=null).mapToDouble(Transaction::getDebit).sum();
               double totalReceived = payeeTransactions.stream().filter(transaction -> transaction.getCredit()!=null).mapToDouble(Transaction::getCredit).sum();
               double averagePayed = totalPayed/totalPayments;

               RegularPayee regularPayee = new RegularPayee();
               regularPayee.setType(type);
               regularPayee.setAvgPayment(averagePayed);
               regularPayee.setName(name);
               regularPayee.setTotalDebited(totalPayed);
               regularPayee.setTotalReceived(totalReceived);

               regularPayees.add(regularPayee);
           }

       });
      result.setRegulars(regularPayees);
    }

    private void calculateHighestTransaction(){
        Optional<Transaction> highest = transactions.stream().filter(transaction -> transaction.getDebit()!=null).max(Comparator.comparing(Transaction::getDebit));
        highest.ifPresent(result::setHighestTransaction);
    }


}

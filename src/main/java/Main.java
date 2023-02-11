import model.Transaction;
import parser.ParsePdf;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        ParsePdf pp = new ParsePdf();

        Expense e = new Expense("src/main/resources/December_axis.pdf","AADE0312",List.of("shirish","aadesh"),10000.00);
//        double mer = e.getMonthlyExpenseAnalysis().getTotalMerchantAmount();
//        double perDep = e.getMonthlyExpenseAnalysis().getTotalPersonDebitAmount();
//        double personCredit = e.getMonthlyExpenseAnalysis().getTotalPersonCreditAmount();
//        double cash = e.getMonthlyExpenseAnalysis().getTotalCashWithdrawal();
//        double other = e.getMonthlyExpenseAnalysis().getOther();
//        double total = mer + perDep + cash +other ;
//
//        System.out.println("Total expanse : "+total+"\n"+"Total Credit :"+personCredit);
//
//
//        System.out.println("Merchant amount: "+e.getMonthlyExpenseAnalysis().getTotalMerchantAmount()+" person debit : "+e.getMonthlyExpenseAnalysis().getTotalPersonDebitAmount()+" Person credit "+e.getMonthlyExpenseAnalysis().getTotalPersonCreditAmount()+" cash: "+e.getMonthlyExpenseAnalysis().getTotalCashWithdrawal()+" other "+e.getMonthlyExpenseAnalysis().getOther());
//        System.out.println(e.getMonthlyExpenseAnalysis().getMerchantPayments().size());
//        System.out.println(e.getMonthlyExpenseAnalysis().getPersonPayments().size());
////        for(Transaction t : e.getMonthlyExpenseAnalysis().getMerchantPayments())
////            System.out.println(t);
//
      System.out.println(e.getMonthlyExpenseAnalysis());
       }
}

import parser.ParsePdf;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        ParsePdf pp = new ParsePdf();

        Expense e = new Expense("axisbankstatement file path","password",List.of("person of intersts"),10000.00);
        Notion notion = new Notion();
        notion.createSummaryInNotion(e.getMonthlyExpenseAnalysis());
       }
}

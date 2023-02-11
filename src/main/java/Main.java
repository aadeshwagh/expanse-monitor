import model.Transaction;
import parser.ParsePdf;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        ParsePdf pp = new ParsePdf();

        Expense e = new Expense("src/main/resources/December_axis.pdf","AADE0312",List.of("shirish","aadesh"),10000.00);

       }
}

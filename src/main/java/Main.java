import parser.ParsePdf;

public class Main {
    public static void main(String[] args) {
        ParsePdf pp = new ParsePdf();
        pp.parseAxisBankStatement("src/main/resources/statement.pdf","AADE0312");
    }
}

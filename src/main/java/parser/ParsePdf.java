package parser;

import model.Transaction;
import org.apache.pdfbox.pdmodel.PDDocument;
import technology.tabula.*;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParsePdf {
    public Set<Transaction> parseAxisBankStatement(String statementPath,String password){
        try {
            // Load the password-protected PDF document
            PDDocument document = PDDocument.load(new File(statementPath), password);

            SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
            PageIterator pi = new ObjectExtractor(document).extract();
            String pattern = "^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[012])-[0-9]{4}$";

            Pattern p = Pattern.compile(pattern);
            Set<Transaction> transactions = new HashSet<>();

            while (pi.hasNext()) {
                // iterate over the pages of the document
                Page page = pi.next();
                List<Table> tables = sea.extract(page);
                for(Table table : tables){

                        List<List<RectangularTextContainer>> rows = table.getRows();
                    // iterate over the rows of the table
                    for (List<RectangularTextContainer> cells : rows) {
                        boolean newRow = true;
                        int count = 0;
                        Transaction transaction = null;
                        for (RectangularTextContainer content : cells) {
                            String text = content.getText().replace("\r", " ");
                            if(newRow){
                                Matcher m = p.matcher(text);
                                if(!m.find()){
                                    continue;
                                }
                                transaction= new Transaction();
                                newRow = false;
                            }
                                if(count==0)
                                    transaction.setDate(text);
                                if(count==1){
                                    if(text.isEmpty())
                                        continue;
                                    String[] message = text.split("/");
                                    transaction.setProtocol(message[0]);
                                    transaction.setType(message[1]);
                                    if(message[0].equals("MOB")){
                                        transaction.setTnxId(message[3]);
                                        transaction.setName(message[2]);
                                    }else{
                                        transaction.setTnxId(message[2]);
                                        transaction.setName(message[3]);
                                    }

                                }

                                if(count == 2){
                                    if(content.getText().isEmpty())
                                        transaction.setDebit(null);
                                    else
                                        transaction.setDebit(Double.parseDouble(text.replace(",","")));
                                }
                                if(count == 3){
                                    if(text.isEmpty())
                                        transaction.setCredit(null);
                                    else
                                        transaction.setCredit(Double.parseDouble(text.replace(",","")));
                                }
                                if(count==4)
                                    transaction.setBalance(Double.parseDouble(text.replace(",","")));


                                count++;
                            }
                        if(transaction != null)
                            transactions.add(transaction);

                        }

                    }

            }
            document.close();

            return transactions;

        } catch (IOException e) {
            throw new RuntimeException("Error reading password protected PDF file: " + e.getMessage());
        }


    }

    public Set<Transaction> parseAxisBankStatement(String statementPath){
        try {
            // Load the password-protected PDF document
            PDDocument document = PDDocument.load(new File(statementPath));

            SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
            PageIterator pi = new ObjectExtractor(document).extract();
            String pattern = "^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[012])-[0-9]{4}$";

            Pattern p = Pattern.compile(pattern);
            Set<Transaction> transactions = new HashSet<>();

            while (pi.hasNext()) {
                // iterate over the pages of the document
                Page page = pi.next();
                List<Table> tables = sea.extract(page);
                for(Table table : tables){

                    List<List<RectangularTextContainer>> rows = table.getRows();
                    // iterate over the rows of the table
                    for (List<RectangularTextContainer> cells : rows) {
                        boolean newRow = true;
                        int count = 0;
                        Transaction transaction = null;
                        for (RectangularTextContainer content : cells) {
                            String text = content.getText().replace("\r", " ");
                            if(newRow){
                                Matcher m = p.matcher(text);
                                if(!m.find()){
                                    continue;
                                }
                                transaction= new Transaction();
                                newRow = false;
                            }
                            if(count==0)
                                transaction.setDate(text);
                            if(count==1){
                                if(text.isEmpty())
                                    continue;
                                String[] message = text.split("/");
                                transaction.setProtocol(message[0]);
                                transaction.setType(message[1]);
                                if(message[0].equals("MOB")){
                                    transaction.setTnxId(message[3]);
                                    transaction.setName(message[2]);
                                }else{
                                    transaction.setTnxId(message[2]);
                                    transaction.setName(message[3]);
                                }

                            }

                            if(count == 2){
                                if(content.getText().isEmpty())
                                    transaction.setDebit(null);
                                else
                                    transaction.setDebit(Double.parseDouble(text.replace(",","")));
                            }
                            if(count == 3){
                                if(text.isEmpty())
                                    transaction.setCredit(null);
                                else
                                    transaction.setCredit(Double.parseDouble(text.replace(",","")));
                            }
                            if(count==4)
                                transaction.setBalance(Double.parseDouble(text.replace(",","")));


                            count++;
                        }
                        if(transaction != null)
                            transactions.add(transaction);

                    }

                }

            }
            document.close();

            return transactions;

        } catch (IOException e) {
            throw new RuntimeException("Error reading password protected PDF file: " + e.getMessage());
        }



    }


}

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import model.*;
import okhttp3.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Notion {

   private final OkHttpClient httpClient;
   private final String PAGE_ID ="<>";
   private final String AUTH = "Bearer <>";
    Notion(){
        httpClient =  new OkHttpClient();
    }

    public void createSummaryInNotion(MonthlyExpenseSummary summary){
        JsonObject payload = createJsonObjectForMonthlyExpenseSummary(summary);
        Map<String,String> headersMap = new HashMap<>();
        headersMap.put("Authorization",AUTH);
        headersMap.put("Notion-Version", "2022-02-22");
        headersMap.put("Content-Type","application/json");
        Headers headers = Headers.of(headersMap);
//        System.out.println(new Gson().toJson(payload));

        RequestBody requestBody = RequestBody.create(new Gson().toJson(payload),MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url("https://api.notion.com/v1/blocks/"+PAGE_ID+"/children")
                .patch(requestBody)
                .headers(headers)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            System.out.println(response.message());
            System.out.println(response.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JsonObject createJsonObjectForMonthlyExpenseSummary(MonthlyExpenseSummary expenseSummary){



        JsonObject billHeading = createMasterHeadings(expenseSummary.getMonthYear());
        JsonObject billOverViewHeading = createHeadings("Overview");
        String status = "Bad";
        if(expenseSummary.getTotalSavings() > 0)
            status = "Good";

        StringBuilder overview = new StringBuilder();
        overview.append("Status :").append(status).append("\n")
                .append("Total Monthly Expense :"+expenseSummary.getTotalExpense()+"\n")
                .append("Estimated Budget :"+expenseSummary.getExceptedExpanse()+"\n")
                .append("Total Savings :"+expenseSummary.getTotalSavings());



        JsonObject overviewParagraph = createParagraph(overview.toString());
        StringBuilder details = new StringBuilder();
        details.append("Total Merchant payments :").append(expenseSummary.getTotalMerchantAmount()).append("\n")
                .append("Total person payments :"+expenseSummary.getTotalPersonDebitAmount()+"\n")
                .append("Total person credit amounts :"+expenseSummary.getTotalPersonCreditAmount()+"\n")
                .append("Total Cash payments :"+expenseSummary.getTotalCashWithdrawal()+"\n")
                .append("Total Other payments :"+expenseSummary.getOther()+"\n")
                .append("Total Credited by person of interest :"+expenseSummary.getTotalCreditAmountByPersonsOfInterest());
        JsonObject billDetailsHeading = createHeadings("Details");
        JsonObject detailsParagraph = createParagraph(details.toString());

        JsonObject highestTransactionHeading = createHeadings("Highest transaction");

        StringBuilder highestTransaction = new StringBuilder();
        highestTransaction.append("Name :").append(expenseSummary.getHighestTransaction().getName()).append("\n")
                .append("Type :"+expenseSummary.getHighestTransaction().getType()+"\n")
                .append("protocol :"+expenseSummary.getHighestTransaction().getProtocol()+"\n")
                .append("Date :"+expenseSummary.getHighestTransaction().getDate()+"\n")
                .append("Debit Amount :"+expenseSummary.getHighestTransaction().getDebit()+"\n")
                .append("Credit amount :"+expenseSummary.getHighestTransaction().getCredit());

        JsonObject highestTransactionParagraph = createParagraph(highestTransaction.toString());

        JsonObject regularsHeading = createHeadings("Regulars");

        JsonObject regularsTable = createTable(expenseSummary.getRegulars());

        JsonObject divider = createDivider();

        JsonArray children = new JsonArray();
        children.add(billHeading);
        children.add(billOverViewHeading);
        children.add(overviewParagraph);
        children.add(billDetailsHeading);
        children.add(detailsParagraph);
        children.add(highestTransactionHeading);
        children.add(highestTransactionParagraph);
        children.add(regularsHeading);
        children.add(regularsTable);
        children.add(divider);

        JsonObject notionEntry = new JsonObject();
        notionEntry.add("children",children);
        return notionEntry;






    }

    private JsonObject createHeadings(String heading){
        Map<String,Boolean> headingAnnotations = new HashMap<>();
        headingAnnotations.put("italic",true);
        JsonObject billHeadingText = createText(heading,headingAnnotations);

        JsonArray billHeadingRichText = createTextArray(billHeadingText);
        return createHeading2(billHeadingRichText);

    }
    private JsonObject createMasterHeadings(String heading){
        Map<String,Boolean> headingAnnotations = new HashMap<>();
        headingAnnotations.put("underline",true);
        JsonObject billHeadingText = createText(heading,headingAnnotations);

        JsonArray billHeadingRichText = createTextArray(billHeadingText);
        return createHeading2(billHeadingRichText);

    }
    private JsonObject createTable(JsonArray tableRows){
        JsonObject table = new JsonObject();
        table.add("children",tableRows);
        table.addProperty("table_width",5);

        JsonObject tableObject = new JsonObject();
        tableObject.addProperty("object","block");
        tableObject.addProperty("type","table");
        tableObject.add("table",table);

        return tableObject;
    }
    private JsonObject createTable(List<RegularPayee> regulars){

        Map<String,Boolean> tableHeadAnos = new HashMap<>();
        tableHeadAnos.put("bold",true);

        JsonArray tableRows = new JsonArray();
        JsonArray nameHead= createTextArray(createText("Name",tableHeadAnos));
        JsonArray typeHead=  createTextArray(createText("Type",tableHeadAnos));
        JsonArray totalDebitHead=   createTextArray(createText("Total Debited",tableHeadAnos));
        JsonArray totalReceivedHead=   createTextArray(createText("Total Created",tableHeadAnos));
        JsonArray totalAvgHead=    createTextArray(createText("Average Payment",tableHeadAnos));

        JsonArray headCells = new JsonArray();
        headCells.add(nameHead);
        headCells.add(typeHead);
        headCells.add(totalDebitHead);
        headCells.add(totalReceivedHead);
        headCells.add(totalAvgHead);

        JsonObject headTableRow = createTableRow(headCells);
        tableRows.add(headTableRow);

        for(RegularPayee regularPayee : regulars){
          JsonArray name= createTextArray(createText(regularPayee.getName(),new HashMap<>()));
          JsonArray type=  createTextArray(createText(regularPayee.getType(),new HashMap<>()));
          JsonArray totalDebit=   createTextArray(createText(regularPayee.getTotalDebited()+"",new HashMap<>()));
          JsonArray totalReceived=   createTextArray(createText(regularPayee.getTotalReceived()+"",new HashMap<>()));
          JsonArray totalAvg=    createTextArray(createText(regularPayee.getAvgPayment()+"",new HashMap<>()));

          JsonArray cells = new JsonArray();
          cells.add(name);
          cells.add(type);
          cells.add(totalDebit);
          cells.add(totalReceived);
          cells.add(totalAvg);

          JsonObject tableRow = createTableRow(cells);
          tableRows.add(tableRow);
        }

        return  createTable(tableRows);
    }
    private JsonObject createParagraph(String content){
        JsonObject paragraphText = createText(content,new HashMap<>());

        JsonArray paragraphRichTextArray = createTextArray(paragraphText);

        return createParagraph(paragraphRichTextArray);


    }

    private JsonObject createText(String content,Map<String,Boolean> anos){
        /*
        {
                        "type": "text",
                        "text":
                        {
                            "content": "July-2022"
                        },
                         "annotations": {
                            "underline": true
                         }
                    }
         */
        JsonObject text = new JsonObject();
        text.addProperty("content",content);

        JsonObject annotations = new JsonObject();
        for(String aName : anos.keySet())
            annotations.addProperty(aName,anos.get(aName));

        JsonObject textObject = new JsonObject();
        textObject.addProperty("type","text");
        textObject.add("text",text);
        textObject.add("annotations",annotations);

        return textObject;



    }
    private JsonArray createTextArray(JsonObject text){
        JsonArray richText = new JsonArray();
        richText.add(text);

        return richText;
    }

    private JsonObject createHeading2(JsonArray richText){

        JsonObject heading2 = new JsonObject();
        heading2.add("rich_text",richText);

        JsonObject heading2Object = new JsonObject();
        heading2Object.addProperty("object","block");
        heading2Object.addProperty("type","heading_2");
        heading2Object.add("heading_2",heading2);

        return heading2Object;

    }
    private JsonObject createParagraph(JsonArray richText){
        JsonObject paragraph = new JsonObject();
        paragraph.add("rich_text",richText);

        JsonObject paragraphObject = new JsonObject();
        paragraphObject.addProperty("object","block");
        paragraphObject.addProperty("type","paragraph");
        paragraphObject.add("paragraph",paragraph);

        return paragraphObject;
    }
    private JsonObject createTableRow(JsonArray cells){
        /*
        [
                                        {
                                        "type": "text",
                                        "text": {
                                            "content": "Name",
                                            "link": null
                                        },
                                        "annotations": {
                                            "bold": true
                                        }
                                        }
                                    ],
         */


        JsonObject tableRow = new JsonObject();
        tableRow.add("cells",cells);

        JsonObject tableRowObject = new JsonObject();
        tableRowObject.addProperty("object","block");
        tableRowObject.addProperty("type","table_row");
        tableRowObject.add("table_row",tableRow);





        return tableRowObject;

    }

    private JsonObject createDivider(){
        JsonObject dividerObject = new JsonObject();

        dividerObject.add("divider",new JsonObject());
        dividerObject.addProperty("object","block");
        dividerObject.addProperty("type","divider");
        return dividerObject;

    }


}

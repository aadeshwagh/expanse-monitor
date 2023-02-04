# Expense Monitor

This project will take monthly statements in pdf format and parse them to extract different transactions and categorize them.
<br>
**Note: Only supported for bank statement format for axis bank for now.**

## Proposed categories 

There are two types of categories

1) **Merchant Payments**
2) **Person Payments**
3) **Cash withdrawal**

Merchants payments can be divided into following sub categories based on additional data (will not be included in first version)

1) Food (includes snacks)
2) Lunch
3) Dinner
4) Big purchases (limit will be custom)
5) Other

person payments can me divide into

1) Rent
2) Total debited amount (categorised by person)
3) Total created amount (categorised by person)

Some general results

1) Total expanse
2) Total savings (will be calculated by : total money send by registered payer - total expanses)

Cash category is self-explanatory

## Milestones
* **v1.0**<br>
create a basic pdf statement parser for axis bank statement and categorize data in three main categories and show general results.<br>
* **v2.0**<br>
add support for additional sub categories.<br>
* gi**v3.0**<br>
add support for getting statements from email and creating automatic bill summary in notion

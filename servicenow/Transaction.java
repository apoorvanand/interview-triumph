// Transaction class represents a financial transaction
package servicenow;
import java.util.*;
enum TransactionType { DEBIT, CREDIT, WITHDRAWAL, DEPOSIT }
public class Transaction {
    private String transactionId;
    private double amount;
    private Date date;
    private TransactionType type;
    private String description;
    
}

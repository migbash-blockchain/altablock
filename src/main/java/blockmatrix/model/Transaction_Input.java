package blockmatrix.model;

public class Transaction_Input {

    // __________________
    // Class Variables

    String transaction_Output_Id;    // Reference to TransactionOutputs -> transactionId
    Transaction_Output UTXO;         // Contains the Unspent transaction output

    // __________________
    // Class Constructor

    public Transaction_Input(String transactionOutputId) { this.transaction_Output_Id = transactionOutputId; }

    // __________________
    // Getters

    public String getTransactionOutputId() { return this.transaction_Output_Id; }
    public Transaction_Output getUTXO() { return this.UTXO; }
}

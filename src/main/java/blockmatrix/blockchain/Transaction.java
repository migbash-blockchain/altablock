package blockmatrix.blockchain;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class Model for a "Transaction" that will be stored inside a Block
 */

public class Transaction {

    // __________________
    // Class Variables

    String transactionId;                                                           // 'this' transaction hash/id
    PublicKey sender;                                                               // 'this' senders address (public key)
    PublicKey recipient;                                                            // 'this' recipients address (public key)
    float value;                                                                    // 'this' transaction value / cost
    private String info;                                                            // 'this' transaction data
    private int blockNumber;                                                        // block number in which this transaction is stored
    private byte[] signature;                                                       // prevents other people from spending funds in our wallet, our signature
    private static int sequence = 0;                                                // a rough count of how many transactions have been generated

    ArrayList<Transaction_Input> inputs = new ArrayList<Transaction_Input>();
    ArrayList<Transaction_Output> outputs = new ArrayList<Transaction_Output>();

    // __________________
    // Class Constructor

    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<Transaction_Input> inputs, String info) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
        this.info = info;
        this.blockNumber = -1;
    }

    // __________________
    // Class Methods

    private String calculate_Transaction_Hash() {

        // This Calculates the transaction hash (which will be used as its Id)

        sequence++; // increase the sequence to avoid 2 identical transactions having the same hash

        String data = StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(recipient) +
                        Float.toString(value) +
                        info +
                        sequence;

        return StringUtil.apply_Sha256(data);
    }

    public void generate_Transaction_Signature(PrivateKey privateKey) {

        // Signs all the data we do not wish to be tampered with

        String data = StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(recipient) +
                        Float.toString(value);

        signature = StringUtil.apply_ECDSA_Signature(privateKey, data);
    }

    public boolean verify_Transaction_Signature() {

        // Verifies the data we signed has not been tampered with

        String data = StringUtil.getStringFromKey(sender) +
                StringUtil.getStringFromKey(recipient) +
                Float.toString(value);

        return StringUtil.verify_ECDSA_Signature(sender, data, signature);
    }

    boolean process_Transaction() {

        if (!verify_Transaction_Signature()) {
            System.out.println("Transaction Signature failed to verify");
            return false;
        }

        // gather transaction inputs (Make sure they are unspent):
        for(Transaction_Input i : inputs) {
            i.UTXO = BlockMatrix.UTXOs.get(i.transaction_Output_Id);
        }

        // check if transaction is valid
        if(get_InputsValue() < BlockMatrix.minimumTransaction) {
            System.out.println("#Transaction Inputs to small: " + get_InputsValue());
            return false;
        }

        // generate transaction outputs
        float leftOver = get_InputsValue() - value;                                  // gets the left over "change"
        transactionId = calculate_Transaction_Hash();                                // ..
        outputs.add(new Transaction_Output(this.recipient, value, transactionId));   // send value to recipient
        outputs.add(new Transaction_Output(this.sender, leftOver, transactionId));   // send the left over "change" back to the sender

        // add outputs to our UTXO list
        for (Transaction_Output output : outputs) {
            BlockMatrix.UTXOs.put(output.id, output);
        }

        // remove transaction inputs from UTXO lists as spent:
        for (Transaction_Input i : inputs) {
            if (i.UTXO == null) continue; // if transaction can't be found, skip it
            BlockMatrix.UTXOs.remove(i.UTXO.id);
        }

        return true;
    }

    public float get_InputsValue() {
        // returns sum of inputs (UTXOs) values

        float total = 0;
        for (Transaction_Input i : inputs) {
            if (i.UTXO == null) continue; // if transaction can't be found skip it
            total += i.UTXO.value;
        }
        return total;

    }

    public float get_OutputsValue() {
        // returns sum of outputs:

        float total = 0;
        for(Transaction_Output o : outputs) {
            total += o.value;
        }
        return total;
    }

    public void clear_Transaction_Info() {
        this.info = "CLEARED";
        this.transactionId =  calculate_Transaction_Hash();
    }

    // __________________
    // Getters & Setters

    public int getBlockNumber() { return this.blockNumber; }
    public String getTransactionId() { return this.transactionId; }
    public PublicKey getSender() { return this.sender; }
    public PublicKey getRecipient() { return this.recipient; }
    public float getValue() { return this.value; }
    public String getInfo() { return this.info; }
    public byte[] getSignature() { return this.signature; }
    public ArrayList<Transaction_Input> getInputs() { return this.inputs; }
    public ArrayList<Transaction_Output> getOutputs() { return this.outputs; }

    void setBlockNumber(int num) { this.blockNumber = num; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n Transaction Details");
        sb.append("\n transactionId: ");
        sb.append(transactionId);
        sb.append("\n sender: ");
        sb.append(sender);
        sb.append("\n recipient: ");
        sb.append(recipient);
        sb.append("\n value: ");
        sb.append(value);
        sb.append("\n info: ");
        sb.append(info);
        sb.append("\n signature: ");
        sb.append(Arrays.toString(signature));
        return sb.toString();
    }
}

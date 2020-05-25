package blockmatrix.blockchain;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * Class Model for "Wallet"
 * holds the modal structure for a wallet on
 * on the blockmatrix;
 */

@Component
public class Wallet {

    // __________________
    // Class Variables

    private PrivateKey privateKey;                                           // 'this' wallet private Key
    private PublicKey publicKey;                                             // 'this' wallet public Key
    private HashMap<String, Transaction_Output> UTXOs = new HashMap<>();     // only UTXOs owned by this wallet.
    private ArrayList<Transaction> transaction_list = new ArrayList<>();     // only transactions made by this wallet.

    // __________________
    // Class Constructors

    public Wallet() { generate_Wallet_KeyPair(); }

    // __________________
    // Class Methods

    private void generate_Wallet_KeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(ecSpec, random);                                     // 256 bytes provides an acceptable security level
            KeyPair keyPair = keyGen.generateKeyPair();

            // Set the public and private keys from the keyPair
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();

        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public float get_Wallet_Balance() {

        // Return balance and stores the UTXOs owned by this wallet in this.UTXOs

        float total = 0;

        // iterate over blockmatrix and obtain all UTXOs
        for (Map.Entry<String, Transaction_Output> item: BlockMatrix.UTXOs.entrySet()){

            Transaction_Output UTXO = item.getValue();                // get transaction (output UTXO) value

            // if output belongs to me (if coins belong to me)
            if(UTXO.is_Transaction_Mine(publicKey)) {
                UTXOs.put(UTXO.id, UTXO);                             // add it to our list of unspent transactions
                total += UTXO.value;                                  // assign UTXO value to total
            }
        }

        return total;
    }

    public Transaction send_Wallet_Funds(PublicKey recipient, float value, String info) {

        // Generates and returns a new transaction from this wallet.

        // [Validation] -> gather balance and check funds.
        if(get_Wallet_Balance() < value) {
            System.out.println("# Not Enough funds to send transaction. Transaction Discarded.");
            return null;
        }

        // create array list of transaction inputs
        ArrayList<Transaction_Input> inputs = new ArrayList<>();

        float total = 0;

        for (Map.Entry<String, Transaction_Output> item: UTXOs.entrySet()){
            Transaction_Output UTXO = item.getValue();                                                  // get total of UTXO in out wallet
            total += UTXO.value;
            inputs.add(new Transaction_Input(UTXO.id));
            if(total > value) break;
        }

        // generate new transaction instance
        Transaction newTransaction = new Transaction(publicKey, recipient , value, inputs, info);
        newTransaction.generate_Transaction_Signature(privateKey);

        // Remove input values from our Wallet UTXO to keep only the "change/remainder" coins...
        for(Transaction_Input input: inputs){
            UTXOs.remove(input.getTransactionOutputId());
        }

        // add this transaction to our list of transactions in the wallet;
        transaction_list.add(newTransaction);

        return newTransaction;
    }

    // __________________
    // Getters

    public PrivateKey getPrivateKey() { return this.privateKey; }
    public PublicKey getPublicKey() { return this.publicKey; }
    public HashMap<String, Transaction_Output> getUTXOs() { return this.UTXOs; }
    public ArrayList<Transaction> getTransactions() {return this.transaction_list;}

}

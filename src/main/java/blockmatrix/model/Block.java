package blockmatrix.model;

import java.util.ArrayList;
import java.util.Date;

import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import blockmatrix.helpers.StringUtil;

/**
 * Class Model for "Block(s)"
 * holds the modal structure for a block in 
 * the "blockmatrix"
 */

@Component
public class Block {

    Logger logger = LoggerFactory.getLogger(Block.class);

    // __________________
    // Class Variables

    private String hash;                                                // 'this' block hash value
    private String merkleRoot;                                          // 'this' block hash value (tree-like combinations of all transaction hashes)
    private ArrayList<Transaction> transactions = new ArrayList<>();    // 'this' block transactions list
    private long timeStamp;                                             // number of milliseconds since 1/1/1970
    private int nonce;                                                  // first number a blockchain miner needs to discover before solving for a block in the blockchain
    private boolean genesis;                                            // whether or not this block is genesis block, by default it is 'FALSE'
    private static int difficulty = 5;                                  // blockchain mining difficulty (more higher = more difficult)

    // __________________
    // Class Constructors

    public Block() {
        this.timeStamp = new Date().getTime();
        this.hash = calculate_Block_Hash();
        this.genesis = false;
    }

    public Block(boolean genesis) {
        this.timeStamp = new Date().getTime();
        this.hash = calculate_Block_Hash();
        this.genesis = genesis;
    }

    // __________________
    // Class Methods

    public String calculate_Block_Hash() {
        return StringUtil.apply_Sha256(Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);
    }

    public void mine_Block() {
        String target = new String(new char[difficulty]).replace('\0', '0');    // Create a string with difficulty * "0" 
        
        merkleRoot = StringUtil.getMerkleRoot(transactions);                    // Get the MerkleRoot (Block Hash)

        while(!hash.substring(0, difficulty).equals(target)) {                  // Infinite while loop until the hash is found
            nonce++;
            hash = calculate_Block_Hash();
        }

        System.out.println("Block Mined: " + hash);
    }

    public boolean add_Transaction_To_Block(Transaction transaction) {

        // check if transaction is empty/null
        if (transaction == null) {
            return false;
        }

        // ignore transaction if it is a genesis block
        if(!genesis) {
            if(!transaction.process_Transaction()) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }

        transactions.add(transaction);                                               // Add Transaction to block transactions "List"
        System.out.println("Transaction Successfully added to Block");
        return true;
    }

    public void clear_Transaction_Info_In_Block(int transactionNumber) {
        transactions.get(transactionNumber).clear_Transaction_Info();
        merkleRoot = StringUtil.getMerkleRoot(transactions);
        this.hash = calculate_Block_Hash();
    }

    public void modifyTX_InfoInBlock(int transactionNumber, String new_transaction_info){
        transactions.get(transactionNumber).modifyTX(new_transaction_info);
        merkleRoot = StringUtil.getMerkleRoot(transactions);
        this.hash = calculate_Block_Hash();
    }

    // __________________
    // Getters

    public String getHash() { return this.hash; }
    public long getTimeStamp() { return this.timeStamp; }
    public ArrayList<Transaction> getTransactions() { return this.transactions; }

    // __________________
    // Class Helper Methods

    public void print_Block_Transactions() {
        System.out.println("\n Block transactions: ");

        int count = 1;

        for (Transaction t: transactions) {
            System.out.println("Info for transaction " + count + " in this block:");
            System.out.println(t.toString());
            count++;
        }
    }

    public ArrayList<String> getStringTx() {
        ArrayList<String> list = new ArrayList<>();

        for (Transaction tx : transactions) {
            list.add(tx.toString());
        }

        return list;
    }
}

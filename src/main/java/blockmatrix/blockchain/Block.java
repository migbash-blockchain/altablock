package blockmatrix.blockchain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;

/**
 * Class Model for "Block(s)"
 */

public class Block {

    Logger logger = LoggerFactory.getLogger(Block.class);

    // __________________
    // Class Variables

    private String hash;                                              // 'this' block hash value
    private String merkleRoot;                                        // 'this' block hash value (tree-like combinations of all transaction hashes)
    private ArrayList<Transaction> transactions = new ArrayList<>();  // 'this' block transactions list
    private long timeStamp;                                           // number of milliseconds since 1/1/1970
    private int nonce;                                                // first number a blockchain miner needs to discover before solving for a block in the blockchain
    private boolean genesis;                                          // whether or not this block is genesis block, by default it is 'FALSE'

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
        merkleRoot = StringUtil.getMerkleRoot(transactions);
        hash = calculate_Block_Hash();
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

    // __________________
    // Getters

    public String getHash() { return hash; }
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

}

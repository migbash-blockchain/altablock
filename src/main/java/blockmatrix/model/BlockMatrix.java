package blockmatrix.model;

import java.net.URL;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.GsonBuilder;

import org.springframework.stereotype.Component;

import blockmatrix.helpers.StringUtil;

/**
 * BlockMatrix.java
 * 
 * Essentialy the equivalent of a 'Blockchain' class, however with the addition
 * of specific methods for handling, updating and verifying the tampering in a "BlockMatrix".
 */

@Component
public class BlockMatrix {

    // __________________
    // BlockMatrix Variables

    private int dimension;                                                // block matrix is size dimension^2,
    private int inputCount = 0;                                           // blocks added to the block matrix,
    private boolean deletionValidity;                                     // verify whether or not all deletions have been valid,
    private Block[][] blockData;                                          // nested-array representing a 'matrix',
    private String[] rowHashes;                                           // array of all hashes in a blockmatrix,
    private String[] columnHashes;                                        // array of all hashes in a blockmatrix,
    static float minimumTransaction;                                      // ..
    private Transaction genesisTransaction;                               // ..
    static HashMap<String, Transaction_Output> UTXOs = new HashMap<>();   // list containing all of the UTXO's transaction
    private boolean generated;                                            // verify whether the genesis block/wallet has been instantiated
    private ArrayList<Integer> blocksWithModifiedData;                    // ..
    private Set<URL> nodesList;                                           // stores all "registered" nodes on the blockmatrix network 

    // __________________
    // BlockMatrix Constructors

    public BlockMatrix() {}

    public BlockMatrix(int dimension) {

        if (dimension < 2) {
            throw new IllegalArgumentException("BlockMatrix must have dimensions of at least 2x2");
        }

        this.dimension = dimension;
        this.blockData = new Block[dimension][dimension];
        this.rowHashes = new String[dimension];
        this.columnHashes = new String[dimension];
        this.minimumTransaction = 0.1f;
        this.deletionValidity = true;
        this.generated = false;
        this.blocksWithModifiedData = new ArrayList<>();
        this.nodesList = Collections.synchronizedSet(new HashSet<>());

        for (int i = 0; i < dimension; i++) {
            update_Row_Hash(i);
            update_Column_Hash(i);
        }

    }

    // __________________
    // BlockMatrix Action Methods

    public void add_Block(Block newBlock) {
        newBlock.mine_Block();
        add_Block_To_Matrix(newBlock);
    }

    private void add_Block_To_Matrix(Block block) {
        
        // adds a block to our blockmatrix
        inputCount++;

        // no more space in the matrix
        if (inputCount > (dimension* dimension) - dimension) {
            inputCount--;
            System.out.println("Error: Addition of " + block.toString() + " to BlockMatrix failed, no more space");
            return;
        }

        // insertion location code gotten from block matrix paper
        if (inputCount % 2 == 0) {
            // Block count is even

            int s = (int) Math.floor(Math.sqrt(inputCount));
            int i = (inputCount <= s*s + s) ? s : s + 1;
            int j = (inputCount - (i*i - i + 2))/2;
            blockData[i][j] = block;

            update_Row_Hash(i);
            update_Column_Hash(j);

        } else {
            // Block count is odd

            int s = (int) Math.floor(Math.sqrt(inputCount + 1));
            int j = (inputCount < s*s + s) ? s: s + 1;
            int i = (inputCount - (j*j - j + 1))/2;
            blockData[i][j] = block;

            update_Row_Hash(i);
            update_Column_Hash(j);
        }

        for (Transaction t: block.getTransactions()) {
            t.setBlockNumber(inputCount);
        }
    }

    public void generate_Genesis_Block(Wallet wallet, float value) {

        // Creates; mines; adds genesis block to blockmatrix

        if (!this.generated) {

            Wallet coinbase = new Wallet();  // instantiate a "genesis" wallet

            // create genesis transaction, which sends coins to our first blockchain wallet
            genesisTransaction = new Transaction(coinbase.getPublicKey(), wallet.getPublicKey(), value, null, null);   // ..
            genesisTransaction.generate_Transaction_Signature(coinbase.getPrivateKey());	                                        // manually sign the genesis transaction
            genesisTransaction.transactionId = "0";                                                                                 // manually set the transaction id
            genesisTransaction.outputs.add(new Transaction_Output(genesisTransaction.recipient, genesisTransaction.value,
                    genesisTransaction.transactionId));                                                                             // manually add the Transactions Output
            UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));                                     // its important to store our first transaction in the UTXOs list.

            System.out.println("Creating and Mining Genesis block... ");
            Block genesis = new Block(true);
            genesis.add_Transaction_To_Block(genesisTransaction);
            add_Block(genesis);
            this.generated = true;
        } else {
            System.out.println("#Error: BlockMatrix already generated.");
        }
    }

    public void setUpSecurity() {
        
        // sets up our security provider so we can create our wallets
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    private void update_Row_Hash(int row) {
        // Uses data in each block in the row except those that are null and those in the diagonal
        rowHashes[row] =  calculate_Row_Hash(row);
    }

    private void update_Column_Hash(int column) {
        // Uses data in each block in the column except those that are null and those in the diagonal
        columnHashes[column] = calculate_Column_Hash(column);
    }

    public void modifyTXinfo_InBM(int blockNumber, String txId, String newTxInfo) {
        
        // the "delete" function, which will overwrite and message info passed in along with the transaction for every transaction in the block

        this.blocksWithModifiedData.add(blockNumber);                               // Add 'this' block to a list to keep track of all modified blocks
        int row = get_Block_Row_Index(blockNumber);                                 // ..
        int column = get_Block_Column_Index(blockNumber);                           // ..

        getBlock(blockNumber).modifyTX_InfoInBlock(txId, newTxInfo);    // Modify the transaction in that particular block

        String[] prevRowHashes = this.getRowHashes().clone();
        String[] prevColumnHashes = this.getColumnHashes().clone();

        update_Row_Hash(row);
        update_Column_Hash(column);

        String[] newRowHashes = this.getRowHashes().clone();
        String[] newColumnHashes = this.getColumnHashes().clone();

        if (!check_Valid_Deletion(prevRowHashes, prevColumnHashes, newRowHashes, newColumnHashes)) {
            System.out.println("Bad deletion, more or less than one row and column hash affected");
            deletionValidity = false; // This might be better as something that throws an exception.
        }

    }

    public void registerNodes(URL url){
        this.nodesList.add(url);
    }
    
    // __________________
    // BlockMatrix Validation & Checks

    public Boolean is_Matrix_Valid() {
        
        // sees if our matrix has maintained its security, or if it has been tampered with
        Block currentBlock;                                                                          // ..
        HashMap<String, Transaction_Output> tempUTXOs = new HashMap<>();                             // temporary working list of unspent transactions of a given block state
        tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));      // populate this list with temporary unspent transactions

        // loop through matrix to check block hashes start at 2 to skip the genesis transaction/block
        for (int i = 2; i < getInputCount(); i++) {

            currentBlock = getBlock(i);

            // compare registered hash to calculated hash
            if (!currentBlock.getHash().equals(currentBlock.calculate_Block_Hash())) {
                System.out.println("Hashes for Block " + i + " not equal (first instance of block with unequal hashes, there may be more)"); //TEST:
                return false;
            }

            // check if hash is solved (mined)
            // if(!currentBlock.getHash().substring(0, difficulty).equals(hashTarget)) {
            // System.out.println("Block " + i +  " hasn't been mined (first instance of unmined block, there may be more)");
            // return false;
            // }

            // loop through blockchain transactions
            Transaction_Output tempOutput;

            for(int t=0; t < currentBlock.getTransactions().size(); t++) {
                Transaction currentTransaction = currentBlock.getTransactions().get(t);

                if(!currentTransaction.verify_Transaction_Signature()) {
                    System.out.println("# Signature on Transaction(" + t + ") is Invalid");
                    return false;
                }

                if(currentTransaction.get_InputsValue() != currentTransaction.get_OutputsValue()) {
                    System.out.println("# Inputs are not equal to outputs on Transaction(" + t + ")");
                    return false;
                }

                // verify out input transactions on the blockchain in out tempUTXO
                for(Transaction_Input input: currentTransaction.inputs) {

                    // ..
                    tempOutput = tempUTXOs.get(input.transaction_Output_Id);

                    // check for the input Transaction value to be existing
                    if(tempOutput == null) {
                        System.out.println("# Referenced input on Transaction(" + t + ") in Block(" + i + ") is Missing");
                        return false;
                    }

                    // check for the input Transaction value to be correct
                    if(input.UTXO.value != tempOutput.value) {
                        System.out.println("# Referenced input Transaction(" + t + ") value is Invalid");
                        return false;
                    }

                    tempUTXOs.remove(input.transaction_Output_Id);
                }

                // place all transactions in blockchain in our tempUTXO
                for(Transaction_Output output: currentTransaction.outputs) {
                    tempUTXOs.put(output.id, output);
                }

                // check blockchain transactions for correct recipient
                if(currentTransaction.outputs.get(0).recipient != currentTransaction.recipient) {
                    System.out.println("#Transaction(" + t + ") output recipient is not who it should be");
                    return false;
                }

                // check blockchain transactions for correct sender
                if(currentTransaction.outputs.get(1).recipient != currentTransaction.sender) {
                    System.out.println("#Transaction(" + t + ") output 'change' is not sent to sender.");
                    return false;
                }
            }
        }

        // check if all row hashes are valid
        for (int i = 0; i < this.getDimension(); i++) {

            if (!this.calculate_Row_Hash(i).equals(this.getRowHashes()[i])) {
                System.out.println("Row hashes for row " + i + " not equal (first instance of row with unequal hashes, there may be more");
                return false;
            }
        }

        // check if all column hashes are valid
        for (int i = 0; i < this.getDimension(); i++) {

            if (!this.calculate_Column_Hash(i).equals(this.getColumnHashes()[i])) {
                System.out.println("Column hashes for row " + i +  " not equal (first instance of column with unequal hashes, there may be more");
                return false;
            }
        }

        // check if all deletions have been valid
        if (!this.getDeletionValidity()) {

            System.out.println("One or more deletions were not valid and altered more than one row and column hash");
            return false;
        }

        return true;
    }

    private boolean check_Valid_Deletion(String[] prevRow, String[] prevCol, String[] newRow, String[] newCol) {
        // tests to make sure only one row hash and one column hash have been modified. If not, then integrity is likely compromised

        int numRowChanged = 0;
        int numColChanged = 0;

        for (int i = 0; i < dimension; i++) {
            if (!prevRow[i].equals(newRow[i])) {
                numRowChanged++;
            }
            if (!prevCol[i].equals(newCol[i])) {
                numColChanged++;
            }
        }

        if (numRowChanged != 1 || numColChanged != 1) {
            return false;
        }

        return true;
    }
    
    // __________________
    // BlockMatrix Getters [For Metrics]
    
    public Block getBlock(int blockNumber) { return blockData[get_Block_Row_Index(blockNumber)][get_Block_Column_Index(blockNumber)]; }
    public String getBlockData(int blockNumber) { return new GsonBuilder().setPrettyPrinting().create().toJson(getBlock(blockNumber).getTransactions()); }
    public ArrayList<Transaction> getBlockTransactions(int blockNumber) { return getBlock(blockNumber).getTransactions(); }
    public int getDimension() { return dimension; }
    public int getInputCount() { return inputCount; }
    public String[] getRowHashes() { return rowHashes; }
    public String[] getColumnHashes() { return columnHashes; }
    public float getMinimumTransaction() { return minimumTransaction; }
    private boolean getDeletionValidity() { return this.deletionValidity; }
    public ArrayList<Integer> getBlocksWithModifiedData() {
        Collections.sort(this.blocksWithModifiedData);
        return this.blocksWithModifiedData;
    }
    public HashMap<String, Transaction_Output> getUTXOs() { return UTXOs; }
    public Set<URL> getList() {return Collections.unmodifiableSet(this.nodesList); }

    public Transaction getTransaction(String tx_id) {

        for(Transaction tx : getAllTransactions()) {
            if (tx.getTransactionId().equals(tx_id)) {
                return tx;
            }
        }
        return null;
    }

    public ArrayList<Transaction> getAllTransactions() {

        ArrayList<Transaction> list = new ArrayList<>();
        int i = 1;

        // Loop through each block in the network
        while(i-1 < inputCount) {
            list.addAll(getBlock(i).getTransactions());
            System.out.println(list.toString());
            i++;
        }
        return list;
    }

    public ArrayList<String> getAllStringTransactions() {
        ArrayList<String> list = new ArrayList<>();
        int i = 1;

        // Loop through each block in the network
        while(i-1 < inputCount) {
            list.addAll(getBlock(i).getStringTx());
            System.out.println(list.toString());
            i++;
        }
        return list;
    }

    public ArrayList<Block> getAllBlocks() {

        ArrayList<Block> block_list = new ArrayList<>();
        int i = 1;

        // Loop through each block in the network
        while(i-1 < inputCount) {
            block_list.add(getBlock(i));
            i++;
        }
        return block_list;
    }
    
    // ________________
    // BlockMatrix Setters

    public void setMinimumTransaction(float num) { minimumTransaction = num; }

    // __________________
    // BlockMatrix Helper Methods
    
    private int get_Block_Row_Index(int blockNumber) {
        
        // helper method to get the row of a block, given a block number
        if (blockNumber % 2 == 0) {

            // Block number is even
            int s = (int) Math.floor(Math.sqrt(blockNumber));
            int row = (blockNumber <= s*s + s) ? s : s + 1;
            System.out.println("Block Row:" + row); 
            return row;

        } else {

            // Block count is odd
            int s = (int) Math.floor(Math.sqrt(blockNumber + 1));
            int column = (blockNumber < s*s + s) ? s: s + 1;
            int row = (blockNumber - (column*column - column + 1))/2;
            System.out.println("Block Row:" + row);
            return row;
        }
    }

    private int get_Block_Column_Index(int blockNumber) {
        // helper method to get the column of a block given a block number

        if (blockNumber % 2 == 0) {
            // Block number is even
            int s = (int) Math.floor(Math.sqrt(blockNumber));
            int row = (blockNumber <= s*s + s) ? s : s + 1;
            int column = (blockNumber - (row*row - row + 2))/2;
            return column;
        } else {
            // Block number is odd
            int s = (int) Math.floor(Math.sqrt(blockNumber + 1));
            int column = (blockNumber < s*s + s) ? s: s + 1;
            return column;
        }
    }

    private String calculate_Row_Hash(int row) {

        StringBuilder sb = new StringBuilder();

        for (int column = 0; column < dimension; column++) {
            if (row != column && blockData[row][column] != null) {
                sb.append(blockData[row][column].getHash());
            }
        }
        return StringUtil.apply_Sha256(sb.toString());
    }

    private String calculate_Column_Hash(int column) {

        StringBuilder sb = new StringBuilder();

        for (int row = 0; row < dimension; row++) {
            if (row != column && blockData[row][column] != null) {
                sb.append(blockData[row][column].getHash());
            }
        }

        return StringUtil.apply_Sha256(sb.toString());
    }

    public String toString() {

        StringBuilder sb = new StringBuilder();

        for (Block[] row: blockData) {
            sb.append(String.format("%5s", Arrays.toString(row)));
            sb.append("\n");
        }
        return sb.toString();
    }

    public void print_RowHashes() {

        System.out.println("\nRow hashes:");

        System.out.println("----------------------------------------------------------------");

        for (String rowHash : rowHashes) {
            System.out.println(rowHash);
        }

        System.out.println("---------------------------------------------------------------- \n");
    }

    public void print_ColumnHashes() {

        System.out.println("\nColumn hashes:");

        System.out.println("----------------------------------------------------------------");

        for (String columnHash : columnHashes) {
            System.out.println(columnHash);
        }

        System.out.println("----------------------------------------------------------------\n");
    }

    public void printHashes() {
        print_RowHashes();
        print_ColumnHashes();
    }

}

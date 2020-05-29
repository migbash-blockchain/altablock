package blockmatrix;

import java.util.HashMap;

import blockmatrix.model.Block;
import blockmatrix.model.BlockMatrix;
import blockmatrix.model.Transaction;
import blockmatrix.model.Transaction_Output;
import blockmatrix.model.Wallet;

/** 
 * This is a Class is for purely demonstrative porpuses of
 * how the blockmatrix would work "step-by-step" in a practical
 * enviroment using Decentralized Fianance (DeFi).
*/

public class BlockMatrix_Example {

    // __________________
    // Class Variables

    private static BlockMatrix bm;
    public static HashMap<String, Transaction_Output> UTXOs;
    public static int difficulty = 3;
    public static float minimumTransaction = 0.1F;
    public static Wallet walletA;
    public static Wallet walletB;
    public static Transaction genesisTransaction;

    // __________________
    // Class Constructors

    public BlockMatrix_Example() {}

    // __________________
    // Main Method

    public static void main(String[] args) {
        bm = new BlockMatrix(1000);                               // Instantiate a New BlockMatrix
        bm.setUpSecurity();                                       // Add Block Securtiy to our BlockMatrix
        bm.setMinimumTransaction(3.0F);                           // ..

        walletA = new Wallet();                                   // Create Wallet_A
        walletB = new Wallet();                                   // Create Wallet_B

        Block block2 = new Block();                               // Instantiate a New Block

        bm.generate_Genesis_Block(walletA, 200.0F);               // ..

        System.out.println("\n WalletA's balance is: " + walletA.get_Wallet_Balance());
        System.out.println("\n WalletA is Attempting to send funds (40) to WalletB...");

        block2.add_Transaction_To_Block(walletA.send_Wallet_Funds(walletB.getPublicKey(), 40.0F, "Here is 40 coins!"));         //..
        block2.add_Transaction_To_Block(walletA.send_Wallet_Funds(walletB.getPublicKey(), 20.0F, "Here is another 20!"));       //..

        block2.add_Transaction_To_Block(walletA.send_Wallet_Funds(walletB.getPublicKey(), 40.0F, "Here is 40 coins!"));         //..
        block2.add_Transaction_To_Block(walletA.send_Wallet_Funds(walletB.getPublicKey(), 20.0F, "Here is another 20!"));       //..

        block2.print_Block_Transactions();                                                                                      //..

        bm.add_Block(block2);                                                                                                   // Add our block with transactions to the BlockMatrix

        // bm.clear_Info_In_Transaction(2, 1);                                                                                     // Clear Transaction Info. passed along in the BlockNumber: 2 & Transaction Number: 1

        block2.print_Block_Transactions();                                                                                      //..

        System.out.println("\n WalletA's balance is: " + walletA.get_Wallet_Balance());
        System.out.println("\n WalletB's balance is: " + walletB.get_Wallet_Balance());
        System.out.println("\n Matrix is Valid: " + bm.is_Matrix_Valid());
    }
}
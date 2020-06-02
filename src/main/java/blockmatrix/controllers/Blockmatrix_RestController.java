package blockmatrix.controllers;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import blockmatrix.helpers.StringUtil;
import blockmatrix.model.Block;
import blockmatrix.model.BlockMatrix;
import blockmatrix.model.Transaction;
import blockmatrix.model.Wallet;

/**
 * This class is responsible for the handling of the RESTful calls made to the blockmatrix
 * which would return calls in a pretty JSON format to anyone who requests it.
 */

@RestController
@EnableAutoConfiguration
@EnableScheduling
@RequestMapping("/")
public class Blockmatrix_RestController {

    @Autowired
    private BlockMatrix new_blockMatrix;

    @Autowired
    private Wallet new_wallet;

    @Autowired
    private Wallet wallet_genesis;

    @Autowired
    private Block new_block;

    @Value("${blockchain.node.id}")
    private String blockChainNodeId;

    /**
     * ==================================
     * === Blockchain Data (Explorer) ===
     * ==================================
     */

    
    

    @RequestMapping(path = "/get_blockmatrix_blocks")
    public String getBlockMatrixBlocks() {

        return new GsonBuilder().setPrettyPrinting().create().toJson(new_blockMatrix.getAllBlocks()); 
    }

    /**
     * REST API - Get a particular block data
     * _____
     * Desc: [GET] BLock Data inside the BlockMatrix
     * _____
     * @param blockNumber
     * @return
     * 
     */

    @RequestMapping(path = "/get_block_data")
    public String getBlockData(@RequestParam(value = "num", required = true, defaultValue = "1") int blockNumber) {
        return new_blockMatrix.getBlockData(blockNumber);
    }

    /**
     * REST API - Get a particular block Transaction
     * _____
     * Desc: [GET] all of the block transaction data
     * _____
     * 
     */

    @RequestMapping(path = "/get_block_transactions")
    public String getBlockTransactions(@RequestParam(value = "num", required = true, defaultValue = "1") int blockNumber) {
        return new_blockMatrix.getBlockTransactions(blockNumber).toArray().toString();
    }

    /**
     * REST API - Get BlockMatrix
     * _____
     * Desc: Gathers all the data from the BlockMatrix,
     * 
     * _____
     * @return JSON (HashMap)
     */

    @RequestMapping(path = "/get_blockmatrix")
    public ResponseEntity<Object> getBlockMatrix() {

        Map<String, Object> response = new HashMap<>();
        response.put("block_count", new_blockMatrix.getInputCount());
        response.put("block_mod_count", new_blockMatrix.getBlocksWithModifiedData().size());
        response.put("tx_count", new_blockMatrix.getAllTransactions().size());

        return new ResponseEntity<Object>(response, HttpStatus.CREATED);
    }

    /**
     * REST API - Get all BlockMatrix Transactions
     * _____
     * Desc: Gathers all of the Transactions in the BlockMatrix Stored
     *       stored in an arraylist.
     * _____ 
     * @return JSON (HashMap)
     * 
     */

    @RequestMapping(path = "/get_blockmatrix_transactions")
    public String getBlockMatrixTX() {

        return new GsonBuilder().setPrettyPrinting().create().toJson(new_blockMatrix.getAllTransactions()); 
    }

    // ====================
    // === User Actions ===
    // ====================

    @RequestMapping(path = "/access_wallet")
    public ResponseEntity<Object> accessWallet(@RequestParam(value = "pub_key", required = true) String pub_key, 
        @RequestParam(value = "priv_key", required = true) String priv_key) {

        PublicKey publicKey = StringUtil.getPublickKeyFromString(pub_key);
        PrivateKey privateKey = StringUtil.getPrivetKeyFromString(priv_key);
        Wallet user_importWallet = new Wallet(privateKey, publicKey);
        
        Map<String, Object> response = new HashMap<>();
        response.put("response", "Wallet Created! :)");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
            

    /**
     * [REST API] - Genreate New Wallet
     * _____
     * Desc: Generates a new wallet for the user
     * _____ 
     * @return JSON (HashMap)
     */

    @RequestMapping(path = "/generate_new_wallet")
    public String generate_new_wallet() {
        Wallet new_wallet = new Wallet();
        String pub_key = StringUtil.getStringFromKey(new_wallet.getPublicKey());
        String priv_key = StringUtil.getStringFromKey(new_wallet.getPrivateKey());
        return "Public Key: " + pub_key + '\n' +
            "Private Key: " + priv_key;
    }

    /**
     * [REST API] - Genreate New Wallet
     * 
     * @param value
     * @param msg
     * 
     */

    @RequestMapping(path = "/send_funds")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> send_funds(@RequestParam(value = "funds", required = true) float value,
        @RequestParam(value = "msg", required = true) String msg) {
        Block n_block = new Block();
        Boolean tx_state = n_block.add_Transaction_To_Block(wallet_genesis.send_Wallet_Funds(new_wallet.getPublicKey(), value, msg));
        new_blockMatrix.add_Block(n_block);
        return new ResponseEntity<String>(tx_state.toString(), HttpStatus.OK);
    }

    @RequestMapping(path = "/modify_transaction_info")
    public void modify_transaction_info(@RequestParam(value = "num", required = true, defaultValue = "1") int blockNumber,
        @RequestParam(value = "transaction", required = true, defaultValue = "0") int transactionNumber,
        @RequestParam(value = "new_info", required = true, defaultValue = "null") String new_info) {
        new_blockMatrix.modifyTXinfo_InBM(blockNumber, transactionNumber, new_info);
    }

    // @Scheduled(fixedRate = 10000) // ms = (60 sec)
    // @RequestMapping("/add_block")
    // public void add_block(){
    //     new_block = new Block();
    //     new_blockMatrix.add_Block(new_block);
    // }
}
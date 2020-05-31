package blockmatrix.controllers;

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

    // __________________
    // Blockchain Data (Explorer)
    // 
    // Used as RESTful end-points for [real-time] blockmatrix Data

    @RequestMapping(path = "/get_matrix_block_num")
    public int get_matrix_block_num() {
        return new_blockMatrix.getInputCount();
    }

    @RequestMapping(path = "/get_block_data")
    public String getBlockData(@RequestParam(value = "num", required = true, defaultValue = "1") int blockNumber) {
        return new_blockMatrix.getBlockData(blockNumber);
    }

    @RequestMapping(path = "/get_block_transactions")
    public String getBlockTransactions(@RequestParam(value = "num", required = true, defaultValue = "1") int blockNumber) {
        return new_blockMatrix.getBlockTransactions(blockNumber).toArray().toString();
    }

    @RequestMapping(path = "/get_blockmatrix")
    public String getBlockMatrix() {

        Map<String, Object> response = new HashMap<>();
        // response.put("block_count", new_blockMatrix.getInputCount());
        // response.put("block_mod_count", new_blockMatrix.getBlocksWithModifiedData().size());
        // response.put("tx_count", new_blockMatrix.getAllTransactions().size());
        for (Transaction t: new_blockMatrix.getAllTransactions()) {
            response.put("tx", t.toString());
        }

        return new GsonBuilder().setPrettyPrinting().create().toJson(new_blockMatrix.getAllTransactions());
    }

    // __________________
    // User Actions

    @RequestMapping(path = "/generate_new_wallet")
    public String generate_new_wallet() {
        Wallet new_wallet = new Wallet();
        String pub_key = StringUtil.getStringFromKey(new_wallet.getPublicKey());
        String priv_key = StringUtil.getStringFromKey(new_wallet.getPrivateKey());
        return "Public Key: " + pub_key + '\n' +
            "Private Key: " + priv_key;
    }

    @RequestMapping(path = "/send_funds")
    @ResponseStatus(HttpStatus.CREATED)
    public void send_funds(@RequestParam(value = "funds", required = true) float value,
        @RequestParam(value = "msg", required = true) String msg) {
        Block n_block = new Block();
        n_block.add_Transaction_To_Block(wallet_genesis.send_Wallet_Funds(new_wallet.getPublicKey(), value, msg));
        new_blockMatrix.add_Block(n_block);
    }

    @RequestMapping(path = "/modify_transaction_info")
    public void modify_transaction_info(@RequestParam(value = "num", required = true, defaultValue = "1") int blockNumber,
        @RequestParam(value = "transaction", required = true, defaultValue = "0") int transactionNumber,
        @RequestParam(value = "new_info", required = true, defaultValue = "null") String new_info) {
        new_blockMatrix.modifyTXinfo_InBM(blockNumber, transactionNumber, new_info);
    }

    //    @Scheduled(fixedRate = 5000) // ms = (5 sec)
    //    @RequestMapping("/add_block")
    //    public void add_block(){
    //        new_blockMatrix.add_Block(new_block);
    //    }

}
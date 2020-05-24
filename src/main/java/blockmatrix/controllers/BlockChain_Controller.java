package blockmatrix.controllers;

import blockmatrix.blockchain.Block;
import blockmatrix.blockchain.BlockMatrix;
import blockmatrix.blockchain.Transaction;
import blockmatrix.blockchain.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * This class is responsible for the handling of the RESTful calls made to the blockmatrix
 * which would return calls in a pretty JSON format to anyone who requests it.
 */

@RestController
@EnableAutoConfiguration
@EnableScheduling
@RequestMapping("/")
public class BlockChain_Controller {

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

    @RequestMapping("/get_block_data")
    public String getBlockData(@RequestParam(value="num", required=true, defaultValue="1") int blockNumber) {
        return new_blockMatrix.getBlockData(blockNumber);
    }

    @RequestMapping("/get_block_transactions")
    public ArrayList<Transaction> getBlockTransactions(@RequestParam(value = "num", required = true, defaultValue = "1") int blockNumber) {
        return new_blockMatrix.getBlockTransactions(blockNumber);
    }

    // __________________
    // User Actions

    @RequestMapping(value = "/generate_new_wallet")
    public String generate_new_wallet() {
        return new Wallet().toString();
    }

    @RequestMapping(value = "/send_funds")
    @ResponseStatus(HttpStatus.CREATED)
    public void send_funds(@RequestParam(value="funds", required = true) float value,
                           @RequestParam(value="msg", required = true) String msg) {
        Block n_block = new Block();
        n_block.add_Transaction_To_Block(wallet_genesis.send_Wallet_Funds(new_wallet.getPublicKey(), value, msg));
        new_blockMatrix.add_Block(n_block);
    }

    @RequestMapping(value = "/modify_transaction_info")
    public void modify_transaction_info(@RequestParam(value="num", required = true, defaultValue = "1") int blockNumber,
                                        @RequestParam(value="transaction", required = true, defaultValue = "0") int transactionNumber,
                                        @RequestParam(value="new_info", required = true, defaultValue = "null") String new_info) {
        new_blockMatrix.modifyTXinfo_InBM(blockNumber, transactionNumber, new_info);
    }

//    @Scheduled(fixedRate = 5000) // ms = (5 sec)
//    @RequestMapping("/add_block")
//    public void add_block(){
//        new_blockMatrix.add_Block(new_block);
//    }
}

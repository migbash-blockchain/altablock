package blockmatrix.controllers;

import blockmatrix.blockchain.Block;
import blockmatrix.blockchain.BlockMatrix;
import blockmatrix.blockchain.Transaction;
import blockmatrix.blockchain.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@EnableAutoConfiguration
@RequestMapping("/")
public class BlockChain_Controller {

    @Autowired
    private BlockMatrix new_blockMatrix;

    @Autowired
    private Wallet new_wallet;

    @Autowired
    private Block new_block;

    @Value("${blockchain.node.id}")
    private String blockChainNodeId;

    // __________________
    // Blockchain Data (Info)

    @RequestMapping("/block_count")
    public int block_count() {
        return new_blockMatrix.getInputCount();
    }

    @RequestMapping("/modified_blocks")
    public String chain() {
        return new_blockMatrix.getBlocksWithModifiedData().toString();
    }

    @RequestMapping("/get_block_data")
    public String getBlockData(@RequestParam(value="num", required=true, defaultValue="1") int blockNumber) {
        return new_blockMatrix.getBlockData(blockNumber);
    }

    @RequestMapping("/get_block_transactions")
    public ArrayList<Transaction> getBlockTransactions(@RequestParam(value = "num", required = true, defaultValue = "1") int blockNumber) {
        return new_blockMatrix.getBlockTransactions(blockNumber);
    }

    // __________________
    // Blockchain Actions



    // __________________
    // User Actions

    @RequestMapping("/generate_new_wallet")
    public String generate_new_wallet() {
        return new Wallet().toString();
    }

    @RequestMapping(value = "/send_funds")
    @ResponseStatus(HttpStatus.CREATED)
    public Boolean send_funds(@RequestParam(value="funds", required = true) float value,
                           @RequestParam(value="msg", required = true) String msg) {
        return new_block.add_Transaction_To_Block(new_wallet.send_Wallet_Funds(new_wallet.getPublicKey(), value, msg));
    }
}

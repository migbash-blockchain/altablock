package blockmatrix.controllers;

import blockmatrix.blockchain.BlockMatrix;
import blockmatrix.blockchain.StringUtil;
import blockmatrix.blockchain.Wallet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * This class is responsible for managing the viewable dashboard which is
 * directly connected to the blockmatrix for a clearer visualization of
 * what is going on the blockmatrix at any moment in time.
 */

@Controller
public class Wallet_Controller {

    // __________________
    // Web App Variables

    @Autowired
    private BlockMatrix new_blockMatrix;

    @Autowired
    private Wallet wallet_genesis;

    @Autowired
    private Wallet new_wallet;

    @Value("${blockchain.node.id}")
    private String blockChainNodeId;

    /**
     * _________________
     * Blockchain Data (Explorer) @GetMapping
     * 
     * Returns ->
     * 
     * - Block Count Number in the BlockMatrix 
     * - Modified Blocks Number in the BlockMatrix
     * - BlockMatrix Status (if blocks are correct or not)
     * - BlockMatrix current dimensions
     * - BlockMatrix all UTXOs'
     * - BlockMatrix all transactions list
     * 
     * @param model
     * @return
     * 
     */

    @GetMapping("/block_explorer")
    public String block_explorer(Model model) {
        model.addAttribute("block_num", new_blockMatrix.getInputCount());
        model.addAttribute("mod_blocks", new_blockMatrix.getBlocksWithModifiedData().toString());
        model.addAttribute("bm_verify", new_blockMatrix.is_Matrix_Valid().toString());
        model.addAttribute("bm_dimen", new_blockMatrix.getDimension());
        model.addAttribute("bm_utxo", new_blockMatrix.getUTXOs());
        model.addAttribute("bm_all_transactions", new_blockMatrix.getAllTransactions());
        return "html/block_explorer";
    }

    /**
     * __________________
     * User Dashboard (UI/UX) @GetMapping
     * 
     * Returns ->
     * 
     * - Users Wallet Balance
     * - Users Wallet UTXO List
     * - Users Public Key
     * - Users Private Key [! Warning]
     * - Users Wallet Whole TXs List
     * 
     * @param model
     * @return
     */

    @GetMapping("/wallet")
    public String wallet(Model model) {
        model.addAttribute("uuid", blockChainNodeId); // TESTME
        model.addAttribute("balance", wallet_genesis.get_Wallet_Balance());
        model.addAttribute("transactions", wallet_genesis.getUTXOs());
        model.addAttribute("pub_address", StringUtil.getStringFromKey(wallet_genesis.getPublicKey()));
        model.addAttribute("priv_address", StringUtil.getStringFromKey(wallet_genesis.getPrivateKey()));
        model.addAttribute("wallet_txs", wallet_genesis.getTransactions());
        return "html/wallet_ui";
    }
}

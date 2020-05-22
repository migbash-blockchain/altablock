package blockmatrix.controllers;

import blockmatrix.blockchain.BlockMatrix;
import blockmatrix.blockchain.StringUtil;
import blockmatrix.blockchain.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Node_Controller {

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

    // __________________
    // Blockchain Data (Explorer)

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

    // __________________
    // User Dashboard (UI/UX)

    @GetMapping("/wallet")
    public String wallet(Model model) {
        model.addAttribute("uuid", blockChainNodeId);
        model.addAttribute("balance", wallet_genesis.get_Wallet_Balance());
        model.addAttribute("transactions", wallet_genesis.getUTXOs());
        model.addAttribute("pub_address", StringUtil.getStringFromKey(wallet_genesis.getPublicKey()));
        model.addAttribute("priv_address", StringUtil.getStringFromKey(wallet_genesis.getPrivateKey()));
        model.addAttribute("bm_user_all_transactions", new_blockMatrix.getAllTransactions());
        return "html/wallet_ui";
    }
}

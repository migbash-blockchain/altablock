package blockmatrix.controllers;

import blockmatrix.blockchain.BlockMatrix;
import blockmatrix.blockchain.Transaction;
import blockmatrix.blockchain.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

@Controller
public class Node_Controller {

    // __________________
    // Web App Variables

    @Autowired
    private BlockMatrix new_blockMatrix;

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
        return "block_explorer";
    }

    // __________________
    // User Dashboard

    @GetMapping("/wallet")
    public String wallet(Model model) {
        model.addAttribute("uuid", blockChainNodeId);
        model.addAttribute("balance", new_wallet.get_Wallet_Balance());
        model.addAttribute("transactions", new_wallet.getUTXOs());
        model.addAttribute("pub_address", new_wallet.getPublicKey());
        model.addAttribute("priv_address", new_wallet.getPrivateKey());
        return "wallet_ui";
    }
}

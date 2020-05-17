package blockmatrix.controllers;

import blockmatrix.blockchain.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Node_Controller {

    @Autowired
    private Wallet new_wallet;

    @Value("${blockchain.node.id}")
    private String blockChainNodeId;

    // __________________
    // User UI

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

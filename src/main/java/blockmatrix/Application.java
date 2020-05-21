package blockmatrix;

import blockmatrix.blockchain.Block;
import blockmatrix.blockchain.BlockMatrix;
import blockmatrix.blockchain.Wallet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

@SpringBootApplication
public class Application {

    private Wallet wallet_genesis;

    public static void main(String[] args) {

        if (System.getProperty("blockchain.node.id") == null) {
            System.setProperty("blockchain.node.id", UUID.randomUUID().toString().replace("-", ""));
        }

        SpringApplication.run(Application.class, args);     // Start the SpringBoot Application
    }

    @Bean
    public Wallet wallet_genesis(){
        return wallet_genesis;
    }

    @Bean
    public BlockMatrix new_blockMatrix() {
        BlockMatrix bm = new BlockMatrix(10000);                        // instantiate new BlockMatrix
        bm.setUpSecurity();                                             // apply block security to the BlockMatrix
        bm.setMinimumTransaction(3.0F);                                 // ..
        wallet_genesis = new Wallet();                                  // instantiate a genesis Wallet
        bm.generate_Genesis_Block(wallet_genesis, 200.0F);              // ..
        System.out.println(wallet_genesis.get_Wallet_Balance());
        return bm;
    }

    @Bean
    public Block new_block() {
        return new Block();
    }

    @Bean
    public Wallet new_wallet() {
        return new Wallet();
    }
}
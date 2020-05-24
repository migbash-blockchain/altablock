package blockmatrix.blockchain;

import java.security.*;
import java.util.ArrayList;
import java.util.Base64;

import org.springframework.stereotype.Component;

/**
 * This Class is used for using for:
 * 
 *  - (Return String) Generating a "SHA256" from a String
 *  - (Return byte[] Array) Apply ECDSA "Signatures" & verify its validity
 *  - (Return String) Obtain a readable string from a Private/Public Key
 *  - (Return String) Obtain a hash merkel-root for all the trnasactions that will be in a block
 */

@Component
public class StringUtil {

    // _________________
    // Class Methods

    public static String apply_Sha256(String input){

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");                 // instantiate the use of SHA-256 encoding,

            byte[] hash = digest.digest(input.getBytes("UTF-8"));            // applies SHA-256 to our transaction input, forming an array of bytes
            StringBuilder hexString = new StringBuilder();                               // 'this' will contain hash as hexi-decimal,

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);

                if (hex.length() == 1) {
                    hexString.append('0');
                }

                hexString.append(hex);
            }
            return hexString.toString();

        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] apply_ECDSA_Signature(PrivateKey privateKey, String input) {

        // Applies ECDSA Signature and returns the result ( as bytes ).

        Signature dsa;
        byte[] output = new byte[0];

        try {
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            byte[] realSig = dsa.sign();
            output = realSig;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return output;
    }

    public static boolean verify_ECDSA_Signature(PublicKey publicKey, String data, byte[] signature) {

        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

    }

    // _________________
    // Class Getters

    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static String getMerkleRoot(ArrayList<Transaction> transactions) {
        // Takes an array of transactions and returns a merkle root.

        int count = transactions.size();                                         // size of transaction list in a block

        ArrayList<String> previousTreeLayer = new ArrayList<String>();                // instantiate a treeLayer List

        // Add all block transactions to a List
        for(Transaction transaction : transactions) {
            previousTreeLayer.add(transaction.transactionId);
        }

        ArrayList<String> treeLayer = previousTreeLayer;                         // assign

        while(count > 1) {
            treeLayer = new ArrayList<String>();

            for(int i = 1; i < previousTreeLayer.size(); i++) {
                treeLayer.add(apply_Sha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
            }

            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }

        String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";

        return merkleRoot;
    }
}

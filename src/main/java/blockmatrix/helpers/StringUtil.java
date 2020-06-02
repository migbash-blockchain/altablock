package blockmatrix.helpers;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;

import org.springframework.stereotype.Component;

import blockmatrix.model.Transaction;

/**
 * This a Helper Class used for using for:
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

    /**
     * Steps:
     * 
     * - 'MessageDigest digest' - instantiate the use of SHA-256 encoding,
     * - 'byte[] hash' - applies SHA-256 to our transaction input, forming an array of bytes
     * - 'hexString' - will contain hash as hexi-decimal,
     * 
     */

    public static String apply_Sha256(String input){

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
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
    // Helper Methods / Functions

    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static PublicKey getPublickKeyFromString(String keyString) {
        try {
            KeyFactory ecKeyFac = KeyFactory.getInstance("ECDSA", "BC");
            byte [] byte_pubkey  = Base64.getDecoder().decode(keyString);
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(byte_pubkey);
            PublicKey publicKey2 = ecKeyFac.generatePublic(x509EncodedKeySpec);
            return publicKey2;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getMerkleRoot(ArrayList<Transaction> transactions) {
        // Takes an array of transactions and returns a merkle root.

        int count = transactions.size();                                              // size of transaction list in a block

        ArrayList<String> previousTreeLayer = new ArrayList<String>();                // instantiate a treeLayer List

        // Add all block transactions to a List
        for(Transaction transaction : transactions) {
            previousTreeLayer.add(transaction.getTransactionId());
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

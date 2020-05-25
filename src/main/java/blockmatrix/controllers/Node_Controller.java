package blockmatrix.controllers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import blockmatrix.blockchain.BlockMatrix;

@Controller
@RequestMapping("/node")
public class Node_Controller {

    // __________________
    // Web App Variables

    @Autowired
    private BlockMatrix new_blockMatrix;

    /**
     * Registering the nodes onto the network
     */

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<Object> registerNodes(@RequestBody List<String> nodes) {
        if (nodes == null || nodes.size() == 0) {
            return new ResponseEntity<Object>("Error: Please supply a valid list of nodes", HttpStatus.BAD_REQUEST);
        }

        for (String node : nodes) {
            try {
                new_blockMatrix.registerNodes(new URL(node));
            } catch (MalformedURLException e) {
                return new ResponseEntity<Object>("Error: Invalid node " + node + ", Please supply a valid node", HttpStatus.BAD_REQUEST);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "New nodes have been added");
        response.put("total_nodes", new_blockMatrix.getList());

        return new ResponseEntity<Object>(response, HttpStatus.CREATED);
    }

}
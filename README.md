![Java CI with Maven](https://github.com/migbash/blockmatrix_wallet/workflows/Java%20CI%20with%20Maven/badge.svg?branch=master)
![Heroku](https://pyheroku-badge.herokuapp.com/?app=altablock&style=flat)

Live Example -> https://altablock.herokuapp.com/wallet

This is an example of a DeFI Blockchain structure using the BlockMatrix Structure.
---

## Project Snaphshots

![image](https://user-images.githubusercontent.com/20924663/84569761-27ea1b00-ad89-11ea-8328-a1481a27ee74.png)
![image](https://user-images.githubusercontent.com/20924663/84569785-39cbbe00-ad89-11ea-8d56-bb80b4251210.png)
![image](https://user-images.githubusercontent.com/20924663/84569796-51a34200-ad89-11ea-8955-eb9147131e20.png)

## Motivation

Solving the GDPR concerns within the blockchain industry to in reaction to the 2018 European
law stating the key points in placing user privacy 1st and the so called "Right to be forgotten", 
if the user decides to opt out of their data (pseudonyms or referential) stored on the blockchain.

## Project Structure

This project is built upon the Spring Boot (Java) Framework with a in-built UI panel for easier interface and
visualization of the Blockchain (Blockmatrix) all in one package. I also throughtout the project switched to html5
templating engine (Pug/Jade), as well as opted for (Sass/Scss) for better design management.

## Features

- Send Transactions to other People on the network with a valid/existing public wallet address.
- Ability to modify or delete your transactions information __after__ it has been sent & validated by the network, (only if you are the owner of that particular transaction)
- A fully working so called "Block Explorer", where you can view the _state_ of the BlockMatrix, with statistics such as:
    - Number of Blocks on the Network (inside the Block-Matrix/Blockchain),
    - Number of "tampered"/"modified" blocks transaction information in the entire "BlockMatrix",
    - All transaction made on the BlockChain / BlockMatrix,
    - Blockmatrix Dimensions

## Inconveniences

- Currently the project holds x2 wallets on the network, with the ability to create inifite amount (until Java Memeroy Heap exceeds my Heroku Dyno hehe),
- Upon interaction with the wallet, you can only send by default to another 'fixed' wallet on the network.
- The "Access Another Wallet" & "View Wallet Keys" buttons currently do not do anything as I have not implmented an action for them.
- The "Search capability" on the "/block_explorer" is not currently very well designed aesthetically and only will display the JSON ouptut in another tab (yikes)

## Issues

- I have implemented live block mining, but it is not enabled currently and thus every transaction will automatically mine a new block and place it in the blockcmatrix.
- The ability to "register new nodes" on the network works and stores unique nodes onto the blockmatrix, but currently lacking the "/resolve" method gateway endpoint for keeping the blockmatrix up-to-date on other users machines.
- Using Heroku on a free tier does not allow me to instanciate a higher "blockmatrix dimension" value (ie: 10000^2) because it causes a "Java Heap Leak Error"... Although it will work perfectly with higher dimensions on your personal machine depedning on your RAM capacity.
- Heroku Free tier will reset the Dyno upon a certain period of inactivity, earasing thus all of the blockmatrix data and reboot its from scratch.

## Getting Set-up

1. Download / clone the repository to your local computer,
2. Open this file in your IDE or text-editor,
3. Run the Application.class app,
4. you should have now your 'localhost:5000/wallet' running, should be identical to the live Heroku Version.

OR

You can download and run the Java.exe package file located [here]() or in the [/release_branch]().

OR

Just visit the Live Heroku version [here](https://altablock.herokuapp.com/wallet).

## Resources

- Blockchain Immutability research paper [here](https://www.researchgate.net/publication/336822518_Blockchain_Mutability_Challenges_and_Proposed_Solutions),
- Follow up Research Paper from unistgov [here](https://csrc.nist.gov/CSRC/media/Publications/white-paper/2018/05/31/data-structure-for-integrity-protection-with-erasure-capability/draft/documents/data-structure-for-integrity-with-erasure-draft.pdf),
- Referecnce Offical GitHub Repository on BlockMatrix from unistgov [here](https://github.com/usnistgov/blockmatrix),

## Takeaway

All in all I continue to work on my personal research and discovering new technologies and the blockmatrix seems very exciting. It was a crazy 3 weeks projects (from research to MVP) and will be looking at ways to utilize this technology in the near future. Currently thinking to apply it HealthCare systems &/or Identity System or even use Smart Contracts with it.

## Future Project Diagram

![image](https://user-images.githubusercontent.com/20924663/107161698-5959cb80-6996-11eb-9425-7e081e0193aa.png)

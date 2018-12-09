# Project: Auction House
## Students:  Alexander Booher, Charles Habermehl, Farhang Rouhi

## Introduction
Summary:
The goal of this project was to create an agent-controlled auction house system that utilizes network sockets for communication between each of the functioning components. 
The agent can create a bank account, get a list of auctions from their bank, and connect to a chosen auction house to bid on items.

More detail: In this project, we have a bank with known ip and port. We can create many 
agents and auction houses to connect to the bank and interact with each other. The user can get a list of all auction houses
available, along with their ip and port number. Then, it will connect to any of those auction houses. The agent, can ask
about the items available in that auction house and bid on those auctions. The agent can win the bid or loose it to a higher bidder.
If she wins, she will ask the bank to transfer the money to that auction house and it will send the receipt.
## Contributions
Alex Booher and Farhang Rouhi worked on the Auction House component.
Charles Habermehl worked on the Bank component.
Farhang Rouhi worked on the Agent component.

## Usage
Give details about how to use the program. (Imagine making it easy for someone that you don't know to run and use your project.)
Our program was made with the console being the primary way to interact with the system. 
It outputs important information and each compnent takes in string commands.

Agent commands:
1. log out: it logs out and terminates the program peacefully, if no bids are waiting.
2. get auction houses: it gets and prints the auction houses.
3. get auctions from "auction house number": this one gets auctions from the selected auction house.
 Note that the number is the number printed when calling get auction houses.
4. bid on "itemNum" for "amount" in "auctionHouse number": This one allows the user to bid on a certain item.
 Note that the itemNum will be printed as the index number or id. The amount is the amount of bid. and the
 auction house number is printed when we print auction houses.

Auction house:
terminate or log out or exit: they will terminate the auction house peacefully. Note that it will wait until
all of the bids are gone and all the receipts are received. Meanwhile, no one can bid on anything and all items are 
unavailable.

### Bank jar
To run the bank application, run **java -jar Bank.jar**.

### Agent jar
To run the bank application, run **java -jar Agent.jar**.

### AuctionHouse jar
To run the bank application, run **java -jar Auction_House.jar**.

## Docs
The documentation PDF is [here.] (AuctionHouseDesign.pdf)


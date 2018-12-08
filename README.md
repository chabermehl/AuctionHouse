# Project: Auction House
## Student(s):  Alexander Booher, Charles Habermehl, Farhang Rouhi

## Introduction
The goal of this project was to create an agent-controlled auction house system that utilizes network sockets for communication between each of the functioning components. 
The agent can create a bank account, get a list of auctions from their bank, and connect to a chosen auction house to bid on items.

## Contributions
Alex Booher worked on the Auction House component.
Charles Habermehl worked on the Bank component.
Farhang Rouhi worked on the Agent component.

## Usage
Give details about how to use the program. (Imagine making it easy for someone that you don't know to run and use your project.)
Our program was made with the console being the primary way to interact with the system. 
It outputs important information and each compnent takes in string commands.

## Project Assumptions
This section is where you put any clarifications about your project & how it works concerning any vagueness in the specification documents.

## Jar Files
#### Bank jar
To run the Bank application, run **java -jar Bank.jar**. 
- **NOTE:** The jar file doesn't seem to work from terminal, but it can be run via double-clicking. This means 
that console output isn't visible.

#### Agent jar
To run the Agent application, run **java -jar Agent.jar**.

#### AuctionHouse jar
To run the AuctionHouse application, run **java -jar AuctionHouse.jar**.

## Docs
The documentation PDF is [here.] (AuctionHouseDesign.pdf)

## Status
### Implemented Features
Basic messaging seems to be working. AuctionHouse and connect to bank, and so can Agent. Agent can communicate with 
auction house.

### Known Issues
Running the Bank app from the terminal doesn't seem to work. It does work when run by double-clicking, however.
After Agent makes a bid on an item, it doesn't seem to be able to communicate with the auction house any longer.
Auction House doesn't check if bidder has enough funds to bid before trying to freeze money.

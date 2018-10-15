import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ServerThread extends Thread {

	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Server server;
	private String username;
	private String gamename;
	private Integer index = 0;
	private Integer players = 0;
	private ArrayList<Card> cards = new ArrayList<Card>();
	private Integer sum = 0;
	private boolean hasAce;
	private Integer bet = 0;
	private boolean bust = false;
	private boolean blackjack = false;
	private Integer chips = 500;
	private boolean dealerbj = false;
	private Integer acecounter = 0;
	private boolean win = false;
	private boolean doublewin = false;
	private boolean lose = false;
	private Integer bigsum = 0;
	private Integer smallsum = 0;
	
	//Constructor
	public ServerThread(Socket socket, Server server) {
		try {
			this.server = server;
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
			this.start();
		} catch (IOException ioe){
			System.out.println("ioe in ServerThread constructor: " + ioe.getMessage());
		}
	}
	
	//take a Message as an input, and send a Message to Client
	public void sendMessage(Message message) {
		try {
			oos.writeObject(message);
			oos.flush();
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
	
	//send a specific Message. this function will be called in Server or in this ServerThread
	public void sendWhoJoins(ServerThread st) {
		Message send = new Message("WhoJoins");
		send.setResult_joinin(st.getUsername());
		sendMessage(send);
	}
	
	public void sendReadyToStart() {
			Message send = new Message("ReadyToStart");
			sendMessage(send);
	}
	
	public void sendYourTurnToMakeBet() {
		Message send = new Message("YourTurn");
		sendMessage(send);
	}
	
	public void sendOtherTurnToMakeBet(String username) {
		Message send = new Message("OtherTurn");
		send.setWhoseturn(username);
		sendMessage(send);
	}
	
	public void sendYourTurnToHitStay() {
		Message send = new Message("YourTurnHS");
		sendMessage(send);
	}
	
	public void sendOtherTurnToHitStay(String username) {
		Message send = new Message("OtherTurnHS");
		send.setWhoseturnhs(username);
		sendMessage(send);
	}
	
	public void sendBetAmount(Integer bet, String username) {
		Message send = new Message("BetAmount");
		send.setOtherbet(bet);
		send.setOtherbetusername(username);
		sendMessage(send);
	}
	
	public void sendStatus(String display) {
		Message send = new Message("Status");
		send.setStatus(display);
		sendMessage(send);
	}
	
	public void sendFinishedHit(String dealtcard) {
		Message send = new Message("FinishedHit");
		send.setDealtcard(dealtcard);
		send.setBust(bust);
		send.setBlackjack(blackjack);
		sendMessage(send);
	}
	
	public void sendOtherHitted(String dealtcard, boolean busted, boolean blackjack) {
		Message send = new Message("OtherHit");
		send.setDealtcard(dealtcard);
		send.setResult_otherbusted(busted);
		send.setResult_otherblackjack(blackjack);
		sendMessage(send);
	}
	
	public void sendOtherStayed(String username) {
		Message send = new Message("OtherStay");
		sendMessage(send);
	}
	
	public void sendDealerStay(String display) {
		Message send = new Message("DealerStay");
		send.setDealerdisplay(display);
		sendMessage(send);
	}
	
	public void sendDealerHit(Integer hitcounter, String display, String shorterdisplay) {
		Message send = new Message("DealerHit");
		send.setDealerdisplay(display);
		send.setHittime(hitcounter);
		send.setShorterdisplay(shorterdisplay);
		sendMessage(send);
	}
	
	public void sendEmptyChips(boolean empty) {
		Message send = new Message("EmptyChips");
		send.setResult_empty(empty);
		sendMessage(send);
	}
	
	public void sendChipsData(String chipsdata) {
		Message send = new Message("ChipsData");
		send.setChipsdata(chipsdata);
		sendMessage(send);
	}
	
	public void sendMyChips() {
		Message send = new Message("MyChips");
		send.setChips(chips);
		sendMessage(send);
	}
	
	public void sendWinLose(String result) {
		Message send = new Message("WinLose");
		send.setWinlose(result);
		sendMessage(send);
	}
	 
	//keep receiving message from Client, specify the Message by their types
	public void run() {
		try {
			while(true) {
				Message message = (Message)ois.readObject();
				String type = message.getType();
				if (type.equals("StartGame")) {
					gamename = message.getGamename();
					players = message.getPlayers();
					boolean success = server.createGame(gamename, players);
					Message send = new Message("StartGame");
					send.setResult_gamename(success);
					sendMessage(send);
				}
				else if (type.equals("CreateUser")) {
					gamename = message.getGamename();
					username = message.getUsername();
					this.setUsername(username);
					boolean success = server.createUser(gamename, username, this);
					Message send = new Message("CreateUser");
					send.setResult_username(success);
					sendMessage(send);
				}
				else if (type.equals("JoinGame")) {
					gamename = message.getGamename();
					boolean success = server.joinGame(gamename, this);
					players = server.getPlayers(gamename);
					Message send = new Message("JoinGame");
					send.setResult_gamename(success);
					send.setPlayers(players);
					sendMessage(send);
				}
				else if (type.equals("ReadyToStart")) {
					String gamename = message.getGamename();
					server.readyToStart(gamename);
				}
				else if (type.equals("TurnToMakeBet")) {
					String gamename = message.getGamename();
					//String username = message.getUsername();
					server.OfficialStart(gamename);
				}
				else if (type.equals("BetAmount")) {
					bet = message.getBet();
					server.usersBet(bet, this);
					Integer other = index+1;
					if(other < players) {
						server.betHelper(other, gamename);
					}
				}
				else if (type.equals("StartDealCards")) {
					server.dealCards(gamename);
				}
				else if (type.equals("StartHitStay")) {
					server.startHitStay(gamename);
				}
				else if (type.equals("Hit")) {
					server.hit(gamename, username);
				}
				else if (type.equals("Stay")) {
					server.stay(gamename, username);
				}
				else if (type.equals("EndMyRun")) {
					server.printStatus(gamename, this);
					Integer otherhs = index+1;
					if(otherhs < players) {
						server.hitStayHelper(otherhs, gamename);
					}
				}
				else if (type.equals("DealerToPlay")) {
					server.dealerPlay(gamename);
				}
				else if (type.equals("EmptyChips")) {
					server.emptyChips(gamename);
//					 server.clearCards(gamename);
				}
				else if (type.equals("ChipsData")) {
					server.chipsData(gamename);
				}
				else if (type.equals("WinLose")) {
					server.checkWinLose(gamename);
				}
				else {
					
				}
			}
		} catch (IOException ioe) {
			System.out.println("ioe in Serverthread.run(): " + ioe.getMessage());
		} catch (ClassNotFoundException cnfe) {
			System.out.println("cnfe: " + cnfe.getMessage());
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public ArrayList getCards() {
		return cards;
	}

	public void setCards(ArrayList<Card> cards) {
		this.cards = cards;
	}
	
	
	public void setGamename(String gamename) {
		this.gamename = gamename;
	}
	public String getGamename() {
		return gamename;
	}
	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}

	public Integer getPlayers() {
		return players;
	}

	public void setPlayers(Integer players) {
		this.players = players;
	}
	
	public void addCard(Card card) {
		 cards.add(card);
	}
	public void addSum(Integer add) {
		sum = sum + add;
	}
	
	//if the bigger sum bust, return smaller sum
	public Integer getSum() {
		if(bigsum > 21) {
			return smallsum;
		}
		else {
			return bigsum;
		}
//		Integer sum = adjustValue();
//		return sum;
	}
//	public Integer adjustValue() {
//		while(sum > 21) {
//			if(acecounter > 0) {
//				sum-=10;
//				acecounter--;
//			}
//			else {
//				break;
//			}
//		}
//		if(sum>21) {
//			bust = true;
//		}
//		
//		return sum;
//	}
	public boolean getHasAce() {
		return hasAce;
	}
	public void setHasAce(boolean has) {
		this.hasAce = has;
	}

	public Integer getBet() {
		return bet;
	}

	public void setBet(Integer bet) {
		this.bet = bet;
	}

	public boolean getBust() {
		return bust;
	}
	
	public void setBlackjack(boolean blackjack) {
		this.blackjack = blackjack;
	}
	
	public boolean getBlackjack() {
		return blackjack;
	}

	public Integer getChips() {
		return chips;
	}

	public void setChips(Integer chips) {
		this.chips = chips;
	}

	public Integer getAcecounter() {
		return acecounter;
	}

	public void setAcecounter(Integer acecounter) {
		this.acecounter = acecounter;
	}

	public boolean getWin() {
		return win;
	}

	public void setWin(boolean win) {
		this.win = win;
	}

	public boolean getDoublewin() {
		return doublewin;
	}

	public void setDoublewin(boolean doublewin) {
		this.doublewin = doublewin;
	}

	public boolean getLose() {
		return lose;
	}

	public void setLose(boolean lose) {
		this.lose = lose;
	}
	
//	public void determineResult() {
//		if()
//	}
	
	//when one round ends, clear all the card list and booleans 
	public void clearCards() {
		cards.clear();
		sum = 0;
		smallsum = 0;
		bigsum = 0;
		blackjack = false;
		bust = false;
	}

	public int getBigsum() {
		return bigsum;
	}

	public void setBigsum(int bigsum) {
		this.bigsum = bigsum;
	}
	
	public void addBigsum(int big) {
		bigsum+=big;
	}

	public int getSmallsum() {
		return smallsum;
	}

	public void setSmallsum(int smallsum) {
		this.smallsum = smallsum;
	}
	
	public void addSmallsum(int small) {
		smallsum+=small;
	}
	
	// every time when a new card is added to a card list, update the booleans
	public void updateBool() {
		if(smallsum > 21) {
			bust = true;
			blackjack = false;
		}
		else if(smallsum == 21 || bigsum == 21) {
			blackjack = true;
			bust = false;
		}
		else {
			blackjack = false;
			bust = false;
		}
	}
}

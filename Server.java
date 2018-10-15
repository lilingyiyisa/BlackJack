import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;


public class Server {
	private Vector<ServerThread> serverThreads = new Vector<ServerThread>();
	private Map<Game,ArrayList<ServerThread>> games = new HashMap<>(); 
//	  map.put(game,users);  
//	  for(Map.Entry m:map.entrySet()){  
//	   System.out.println(m.getKey()+" "+m.getValue());  
//	  }  
	
	//Constructor
	public Server(String port) throws IOException, IllegalArgumentException {
		ServerSocket ss = new ServerSocket(Integer.parseInt(port));
		serverThreads = new Vector<ServerThread>();
		games = new HashMap<Game,ArrayList<ServerThread>>();
		System.out.println("Successfully started the Black Jack server on port " + port);
		while(true) {
			Socket s = ss.accept();
			ServerThread st = new ServerThread(s, this);
			serverThreads.add(st);
		}
	}
	
	public static void main(String[] args) {
		System.out.println("Welcome to the Black Jack Server!");
		Scanner scan = new Scanner(System.in);
		String port = null;
		boolean valid = false;
		while (valid == false) {
			System.out.println("Please enter a port");
			port = scan.nextLine();
			try {
				Server server = new Server(port);
				valid = true;
			} catch (IOException | IllegalArgumentException ioe) {
				System.out.println("Invalid port number.");
			}
		}
		
	}
	
	//take the String of gamename, check if it is valid. if valid, store the number of players
	public boolean createGame(String gamename, Integer players) {
		//There are games in the server
		for(Game game : games.keySet()) {
			if(gamename.equals(game.getName())) {
				return false;
			}
		}
		//There are no games in the server
		Game game = new Game(gamename);
		game.setPlayers(players);
		games.put(game, new ArrayList<>());
		return true;
	}
	
	//take the String of username, check if it is valid. if valid, add the ServerThread in game
	public boolean createUser(String gamename, String username, ServerThread st) {
		//There are games in server
		for(Game game : games.keySet()) {
			if(gamename.equals(game.getName())) {
				for(int j=0; j<game.getUsers().size(); j++) {
					ServerThread user = game.getUsers().get(j);
					if(username.equals(user.getUsername())) {
						return false;
					}
				}
				game.addUser(st);
				st.setIndex(game.getUsers().size()-1);
				if(game.getUsers().size()>1) {
					ServerThread user = game.getUsers().get(0);
					user.sendWhoJoins(st);
				}
				return true;
			}
		}
		//There are no games in server
		Game game = new Game(gamename);
		game.addUser(st);
		return true;
	}
	
	//check if the gameroom is still able to include the new user or not
	public boolean joinGame(String gamename, ServerThread st) {
		for(Game game : games.keySet()) {
			if(gamename.equals(game.getName())) {
				if (game.getUsers().size() < game.getPlayers()) {
					return true;
				}
			}
		}
		return false;
	}
	
	//take the String of gamename, send a Message to every user in the game that it is ready to start
	public void readyToStart(String gamename) {
		for(Game game : games.keySet()) {
				if(gamename.equals(game.getName())) {
						for(int i=0; i<game.getUsers().size(); i++) {
							ServerThread serverthread = game.getUsers().get(i);
							serverthread.sendReadyToStart();
					}
				}
		}
		
		//return false;
	}
	
	//Once every user in the game is ready to start, give the first user in the game the turn to make bet
	public void OfficialStart(String gamename) {
				betHelper(0, gamename);			
	}
	
	//takes the current turn index as input. tell the user it is his/her turn. tell the others in the game that is this user's turn
	public void betHelper(int index, String gamename) {
		for(Game game : games.keySet()) {
			if(gamename.equals(game.getName())) {
				game.getUsers().get(index).sendYourTurnToMakeBet();
				for(int i=0; i<game.getUsers().size(); i++) {
					if(i!=index) {
						game.getUsers().get(i).sendOtherTurnToMakeBet(game.getUsers().get(index).getUsername());
					}
				}
				
			}
		}
	}
	
	//send a message to other ServerThread in the same game with the input ServerThread how much they bet
	public void usersBet(Integer bet, ServerThread st) {
		String gamename = st.getGamename();
		for(Game game : games.keySet()) {
			if(gamename.equals(game.getName())) {
				for(int i=0; i<game.getUsers().size(); i++) {
					ServerThread serverthread = game.getUsers().get(i);
					if(!serverthread.equals(st)) {
						serverthread.sendBetAmount(bet, st.getUsername());
					}
				}
			}
		}
	}

	//get the number of players in one game
	public Integer getPlayers(String gamename) {
		Integer players = -1;
		for(Game game : games.keySet()) {
			if(gamename.equals(game.getName())) {
				players = game.getPlayers();
			}
		}
		return players;
	}
	
	//call a function in game to deal cards 
	public void dealCards(String gamename) {
		for(Game game : games.keySet()) {
			if(gamename.equals(game.getName())) {
				game.dealCards(); //everyone gets cards
				String display = game.display();
				for(int i=0; i<game.getUsers().size(); i++) {
					game.getUsers().get(i).sendStatus(display);
				}
			}
			//make a 2D array of all the cards information 
		}
	}
	
	//give this first user in the game the turn to hit or stay
	public void startHitStay(String gamename) {
		hitStayHelper(0, gamename);
	}
	
	//take the current turn index as input, tell the user it is his/her turn. tell the others in the game that is this user's turn
	public void hitStayHelper(Integer index, String gamename) {
		for(Game game : games.keySet()) {
			if(gamename.equals(game.getName())) {
				game.getUsers().get(index).sendYourTurnToHitStay();
				//game.hit(game.getUsers().get(index));
				for(int i=0; i<game.getUsers().size(); i++) {
					if(i!=index) {
						game.getUsers().get(i).sendOtherTurnToHitStay(game.getUsers().get(index).getUsername());
					}
				}		
			}
		}
	}
	
	//get called when the user choose to hit
	public void hit(String gamename, String username) {
		for(Game game : games.keySet()) {
			if(gamename.equals(game.getName())) {
				for(int i=0; i<game.getUsers().size(); i++) {
					if(username.equals(game.getUsers().get(i).getUsername())) {
						ServerThread st = game.getUsers().get(i);
						String dealtcard = game.hit(st);
						st.sendFinishedHit(dealtcard);
						boolean busted = st.getBust();
						boolean blackjack = st.getBlackjack();
						for(int j=0; j<game.getUsers().size(); j++) {
							if(j!=i) {
								ServerThread other = game.getUsers().get(j);
								other.sendOtherHitted(dealtcard, busted, blackjack);
							}
						}
					}
				}
			}
		}
	}
	
	//get called when the user choose to stay
	public void stay(String gamename, String username) {
		for(Game game : games.keySet()) {
			if(gamename.equals(game.getName())) {
				for(int i=0; i<game.getUsers().size(); i++) {
					if(!username.equals(game.getUsers().get(i).getUsername())) {
						ServerThread st = game.getUsers().get(i);
						st.sendOtherStayed(username);
					}
				}
			}
		}
	}
	
	//send the string to each ServerThread in the game with correctly formated status
	public void printStatus(String gamename, ServerThread st) {
		for(Game game : games.keySet()) {
			if(gamename.equals(game.getName())) {
				String display = game.displayPlayer(st);
				for(int i=0; i<game.getUsers().size(); i++) {
					ServerThread serverthread = game.getUsers().get(i);
					serverthread.sendStatus(display);
				}
			}
		}
	}
	
	//get called when it is dealer's turn to play
	public void dealerPlay(String gamename) {
		for(Game game : games.keySet()) {
			if(gamename.equals(game.getName())) {
				Integer result = game.dealerPlay();
				Integer hitcounter = game.getHitcounter();
				String display = game.displayDealer();
				String shorterdisplay = game.dealerHitDisplay();
				if (result == 1) { //dealer stay
					for(int i=0; i<game.getUsers().size(); i++) {
						ServerThread st = game.getUsers().get(i);
						st.sendDealerStay(display);
					}
				}
				else { //result == 2 // dealer hit
					for(int i=0; i<game.getUsers().size(); i++) {
						ServerThread st = game.getUsers().get(i);
						st.sendDealerHit(hitcounter, display, shorterdisplay);
					}
				}
			}
		}
	}
	
	//check if the game should be over or not
	public void emptyChips(String gamename) {
		boolean empty = false;
		for(Game game : games.keySet()) {
			if(gamename.equals(game.getName())) {
				for(int i=0; i<game.getUsers().size(); i++) {
					ServerThread st = game.getUsers().get(i);
					if(st.getChips()==0) {
						empty = true;
					}
				}
				for(int i=0; i<game.getUsers().size(); i++) {
					ServerThread st = game.getUsers().get(i);
					st.sendEmptyChips(empty);
				}
			}
		}
		clearCards(gamename);
	}
	
	// send the result data to each ServerThread in the game
	public void chipsData(String gamename) {
		String display = "";
		for(Game game : games.keySet()) {
			if(gamename.equals(game.getName())) {
				for(int i=0; i<game.getUsers().size(); i++) {
					ServerThread st = game.getUsers().get(i);
					Integer difference = 0;
					String status = "";
					String bustjack = "";
					//compare each st with dealer
					if(st.getBust() == true) {
						status = "deducted";
						difference = st.getBet();
						bustjack = " busted. ";
						st.setChips(st.getChips()-st.getBet());
					}
					else {
						if(st.getBlackjack() == true) {
							if(game.getDealer().getBlackjack() == true) {
								bustjack = "tied with the dealer. ";
								status = "remained";
								difference = 0;
							}
							else {
								status = "added";
								difference = 2*st.getBet();
								bustjack = " had blackjack. ";
								st.setChips(st.getChips()+2*st.getBet());
							}
						}
						else {
							if(game.getDealer().getBlackjack() == true) {
								status = "deducted";
								difference = st.getBet();
								bustjack = " had a sum less than the dealer's blackjack. ";
								st.setChips(st.getChips()-st.getBet());
							}
							else {
								if(st.getSum() < game.getDealer().getSum()) {
									if(game.getDealer().getBust() == true) {
										status = "added";
										difference = st.getBet();
										bustjack = " had a valid value. Dealer busted. ";
										st.setChips(st.getChips()+st.getBet());
									}
									else {
										status = "deducted";
										difference = st.getBet();
										bustjack = " had a sum less than the dealer's. ";
										st.setChips(st.getChips()-st.getBet());
									}
									
								}
								else if(st.getSum() == game.getDealer().getSum()) {
									bustjack = " tied with the dealer. ";
									status = "remained";
									difference = 0;
								}
								else {
									status = "added";
									difference = st.getBet();
									bustjack = " had a sum greater than the dealer's. ";
									st.setChips(st.getChips()+st.getBet());
								}
							}
						}
					}
					display+=st.getUsername() + bustjack + difference +" chips were " + status + " to " + st.getUsername() + "'s total \n";
					
				}
				for(int i=0; i<game.getUsers().size(); i++) {
					ServerThread st = game.getUsers().get(i);
					st.sendChipsData(display);
				}
			}
		}
	}
	
	//get called when each round is over
	public void clearCards(String gamename) {
		for(Game game : games.keySet()) {
			if(gamename.equals(game.getName())) {
				game.getDealer().clearCards();
				game.setHide(true);
				for(int i=0; i<game.getUsers().size(); i++) {
					ServerThread st = game.getUsers().get(i);
					st.clearCards();
					st.sendMyChips(); //send individual chips update
				}
			}
		}
	}
	
	//check the final result whether a user win or lose
	public void checkWinLose(String gamename) {
		String display="";
		for(Game game : games.keySet()) {
			if(gamename.equals(game.getName())) {
				for(int i=0; i<game.getUsers().size(); i++) {
					ServerThread st = game.getUsers().get(i);
					if(st.getChips() == 0) {
						display+=st.getUsername() + " lose. \n";
					}
					else {
						display+=st.getUsername() + " win. \n";
					}
				}
				for(int i=0; i<game.getUsers().size(); i++) {
					ServerThread st = game.getUsers().get(i);
					st.sendWinLose(display);
				}
			}
		}
	}
}
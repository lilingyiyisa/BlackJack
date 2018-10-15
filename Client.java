import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
	
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private String username;
	private String gamename;
	private boolean success = false;
	private Integer joined;
	private Integer players;
	private Integer chips = 500;
	private String choice; 
	private boolean bust;
	private boolean blackjack;
	private boolean endthisrun = false;
	private boolean endmyrun = false;
	
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getGamename() {
		return gamename;
	}

	public void setGamename(String gamename) {
		this.gamename = gamename;
	}
	
	//take a Message as input and send the Message to ServerThread
	public void sendMessage(Message message) {
		try {
			oos.writeObject(message);
			oos.flush();
		} catch (IOException ioe) {
			System.out.println("ioe in Clinet sendMessage(): " + ioe.getMessage());
		}
	}
	
	//Constructor
	public Client(String ipaddress, String port) throws IOException, IllegalArgumentException, ClassNotFoundException{
		Socket socket = new Socket(ipaddress, Integer.parseInt(port));
		oos = new ObjectOutputStream(socket.getOutputStream());
		ois = new ObjectInputStream(socket.getInputStream());
	}
	
	public static void main(String[] args) {
		System.out.println("Welcome to Black Jack!");
		Scanner scan = new Scanner(System.in);
		String ipaddress = null;
		String port = null;
		Client client = null;
		boolean valid = false;
		while(valid == false) {
			try {
				System.out.println("Please enter the ipaddress");
				ipaddress = scan.nextLine();
				System.out.println("Please enter the port");
				port = scan.nextLine();
				client = new Client(ipaddress, port);
				valid = true;
				client.displayMenu();
			} catch (IOException | IllegalArgumentException | ClassNotFoundException ioe) {
				System.out.println("Unable to connect to server with provided fields");
			}
		}
	}
	
	//after created a valid Client, display the menu
	public void displayMenu() throws ClassNotFoundException, IOException {
		Scanner scan = new Scanner(System.in);
		System.out.println("Please choose from the options below");
		System.out.println("1) Start Game");
		System.out.println("2) Join Game");
		choice = scan.nextLine();
		//player is the host of the game
		if (choice.equals("1")) {
			success = false;
			while(success == false) {
				System.out.println("Please choose the number of players in the game (1-3)");
				String splayers = scan.nextLine();
				if(splayers.equals("1")||splayers.equals("2")||splayers.equals("3") ) {
					success = true;
					players = Integer.parseInt(splayers);
				}
				else {
					System.out.println("invalid input");
				}
			}
			success = false;
			while(success == false) {
				System.out.println("Please choose a name for your game");
				gamename = scan.nextLine();
				Message message = new Message("StartGame");
				message.setGamename(gamename);
				message.setPlayers(players);
				sendMessage(message);
				Message received = (Message)ois.readObject();
				//if(received.getType().equals("StartGame"))
					success = received.getResult_gamename();
					if (success == false) {
						System.out.println("Invalid choice. This game name has already been chosen by another user");
					}
			}

			System.out.println("Please choose a username");
			username = scan.nextLine();
			Message message = new Message("CreateUser");
			message.setUsername(gamename, username);
			sendMessage(message);
			
			joined = 1;
			System.out.println("Waiting for " + (players-joined) + " other players to join...");
			while(players-joined > 0) {
				Message received2 = (Message)ois.readObject();
				if(received2.getType().equals("WhoJoins")) {
					String player = received2.getResult_joinin();
					joined++;
					System.out.println(player +" joined the game");
					if(players-joined != 0) {
						System.out.println("Waiting for " + (players-joined) + " other players to join...");
					}
				}
			}
				
			Message send = new Message("ReadyToStart"); //let the server tell other users in the game WE CAN START
			send.setGamename(gamename);
			sendMessage(send);

			System.out.println("Let the game commence. Good luck to all players!");
			//officialStart();
		}
		//player is the guest of the game
		else if (choice.equals("2")) {
			success = false;
			while(success == false) {
				System.out.println("Please enter the name of the game you wish to join in");
				gamename = scan.nextLine();
				Message message = new Message("JoinGame");
				message.setGamename(gamename);
				sendMessage(message);
				Message received = (Message)ois.readObject(); //receive "JoinGame"
				success = received.getResult_gamename();
				if (success == false) {
					System.out.println("Invalid choice. There are no onging games with this name");
				}
				if (success == true) {
					players = received.getPlayers();
				}
			}
			
			success = false;
			while(success == false) {
				System.out.println("Please choose a username");
				username = scan.nextLine();
				Message message = new Message("CreateUser");
				message.setUsername(gamename, username);
				sendMessage(message);
				Message received = (Message)ois.readObject();
				if(received.getType().equals("CreateUser")) {
					success = received.getResult_username();
					if (success == false) {
						System.out.println("Invalid choice. This username has already been chosen by another player in this game");
					}
				}
			}
			System.out.println("The game will start shortly. Waiting for other players to join...");
			Message received3 = (Message)ois.readObject(); //Server tells me game room full, WE CAN START
			if(received3.getType().equals("ReadyToStart")) {
				System.out.println("Let the game commence. Good luck to all players!");
			}
		}		
		else {
			System.out.println("Invalid choice");
		}
		
		//finished set-up, officially start to play the game
		boolean emptyChips = false;
		int round = 1;
		while(emptyChips == false) {
			System.out.println("ROUND "+round);
			officialStart();
			if(choice.equals("1")) { //let the host to ask for chips data
				Message send = new Message("ChipsData");
				sendMessage(send);
			}
			Message received7 = (Message)ois.readObject();
			System.out.println(received7.getChipsdata());
			if(choice.equals("1")) {
				Message send = new Message("EmptyChips");  //also clear the cards
				sendMessage(send);
			}
			for(int i=0; i<2; i++) {
				Message received = (Message)ois.readObject();
				if(received.getType().equals("MyChips")) {
					chips = received.getChips();
				}
				else if(received.getType().equals("EmptyChips")) {
					emptyChips = received.getResult_empty();
				}
				else {
					System.out.println("invalid");
				}
			}
			round++;
		}
		if (choice.equals("1")) { //let the host ask for who win and who lose
			Message send = new Message("WinLose");
			sendMessage(send);
		}
		Message received = (Message)ois.readObject();
		System.out.println(received.getWinlose());
		System.out.println("GAME OVER. SEE YOU NEXT TIME");
	}
	
	//each round of the game
	public void officialStart() throws ClassNotFoundException, IOException {
		System.out.println("Dealer is shuffling cards..");
		if(choice.equals("1")) { //if this is the starter, let the server know we can make bet now
			Message send = new Message("TurnToMakeBet");
			send.setGamename(gamename);
			sendMessage(send);
		}
		int counter = 0; 
		
		while(counter < players) { //should receive 3 messages, one is your turn, two are other's turn
			Message received = (Message)ois.readObject();
			if(received.getType().equals("YourTurn")) {
				System.out.println(username + ", it is your turn to make a bet. Your chip total is " + chips);
				makeBet();
				counter++;
			}
			if(received.getType().equals("OtherTurn")) {
				String whosturn = received.getWhoseturn();
				System.out.println("It is " + whosturn + "'s turn to make a bet.");
				counter++;
				Message received2 = (Message)ois.readObject(); //"BetAmount"
				Integer otherbet = received2.getOtherbet();
				String otherbetusername = received2.getOtherbetusername();
				System.out.println(otherbetusername + "bet " + otherbet + " chips");
			}
		}
		
		if(choice.equals("1")) { //let the server know we can deal cards
			Message send = new Message("StartDealCards");
			sendMessage(send);
		}
		Message received = (Message)ois.readObject(); //receive "Status"
		System.out.println(received.getStatus());
		
		if(choice.equals("1")) { //let the server know we can start hit or stay
			Message send = new Message("StartHitStay");
			sendMessage(send);
		}
		
		for(int i=0; i<players; i++) { //while this round ends:??? everyone stayed or busted
			Message received4 = (Message)ois.readObject();
			if(received4.getType().equals("YourTurnHS")) {
				System.out.println("It is your turn to add cards to your hand");
				hitStay();
			}
			if(received4.getType().equals("OtherTurnHS")) {
				endthisrun = false;
				//received message of a new string with updated display (" ") 
				String whoseturnhs = received4.getWhoseturnhs();
				System.out.println("It is "+whoseturnhs+" 's turn to add cards");
				while(endthisrun == false) {
					Message received5 = (Message)ois.readObject();
					if(received5.getType().equals("OtherHit")) {
						String dealtcard = received5.getDealtcard();
						System.out.println(whoseturnhs + "hit. They were dealt with" + dealtcard);
					}
					else if(received5.getType().equals("OtherStay")) {
						System.out.println(whoseturnhs + "stayed");
						endthisrun = true;
					}
					else if(received5.getType().equals("OtherBust")){
						System.out.println(whoseturnhs + "busted! They lose chips");
						endthisrun = true;
					}
				}
				Message received6 = (Message)ois.readObject();
				String printout = received6.getStatus();
				System.out.println(printout);
			}
		}
		//dealer's turn
		System.out.println("It is now time for the dealer to play.");
		if(choice.equals("1")) {
			Message send = new Message("DealerToPlay");
			sendMessage(send);
		}
		Message received2 = (Message)ois.readObject();
		if(received2.getType().equals("DealerStay")) {
			System.out.println(received2.getDealerdisplay());
		}
		else if(received2.getType().equals("DealerHit")) {
			Integer hittime = received2.getHittime();
			String shorterdisplay = received2.getShorterdisplay();
			System.out.println("The dealer hit " + hittime + " time. They were dealt: " + shorterdisplay);
			System.out.println(received2.getDealerdisplay());
		}
		else {
			System.out.println("invalid");
		}		
		
	}
	
	// turn to make bet
	public void makeBet() {
		Scanner scan = new Scanner(System.in);
		Integer chips = scan.nextInt();
		scan.nextLine();
		System.out.println("You bet "+chips+" chips");
		Message send = new Message("BetAmount");
		send.setBet(chips);
		sendMessage(send);
	}
	
	//turn to choose hit or stay
	public void hitStay() throws ClassNotFoundException, IOException {
		String hschoice = "2";
		endmyrun = false;
		while(endmyrun == false) {
			System.out.println("Enter either '1' or 'stay' to stay. Enter either '2' or 'hit' to hit.");
			Scanner scan = new Scanner(System.in);
			hschoice = scan.nextLine();
			if(hschoice.equals("1") || hschoice.equals("stay")) {
				System.out.println("You stayed");
				Message send = new Message("Stay");
				sendMessage(send);
				endmyrun = true;
			}
			else if(hschoice.equals("2") || hschoice.equals("hit")) {
				Message send = new Message("Hit");
				sendMessage(send);
				Message received =  (Message)ois.readObject();
				String dealtcard = received.getDealtcard();
				bust = received.getBust();
				System.out.println("You hit. You were dealt with the " + dealtcard);
				endmyrun = false;
			}
			else {
				System.out.println("Invalid input");
			}
			if (bust == true) {
				System.out.println("You busted. You lose " + chips + " chips");
				endmyrun = true;
			}	
		}
		//end my run, print out my status
		Message send = new Message("EndMyRun");
		sendMessage(send);
		Message received = (Message)ois.readObject();
		String printout = received.getStatus();
		System.out.println(printout);
	}
	
}

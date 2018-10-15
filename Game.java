import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

public class Game {
	private ArrayList<ServerThread> users = new ArrayList<ServerThread>();
	private String name;
	private Integer bet;
	private Integer players;
	private String cardindex;
	private String suits[] = { "SPADE", "DIAMOND", "CLUB", "HEART" };
	private String ranks[] = { "ACE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "TEN", "JACK", "QUEEN", "KING" };
	private Dealer dealer = new Dealer();
	private String [][] allcards;
	private Vector<Card> cards = new Vector<Card>(); //52cards
	private HashSet<Integer> set = new HashSet<Integer>();
	private Integer hitcounter = 0;
	private boolean hide = true;
	private Vector<Card> dealerhit = new Vector<Card>();
	//each time generate a random index from 0 to 52
	//add to set
	// if already in set, generate a new one
	
	public Game(String name) {
		this.name = name;
		users = new ArrayList<>();
		for(int i=0; i<4; i++) {
			for(int j=0; j<13; j++) {
				Card card = new Card(suits[i], ranks[j]);
				cards.add(card);
			}
		}
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<ServerThread> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<ServerThread> users) {
		this.users = users;
	}
	
	public void addUser(ServerThread user) {
		users.add(user);
	}
	
	public void makeBet(Integer bet) {
		this.bet = bet;
	}
	
	public Integer getBet() {
		return bet;
	}
	
//	public void changeBet(String status) {
//		if(status.equals("bust")) {
//			
//		}
//	}
	
//	public void dealerPrintCard() {
//		
//	}

	public Integer getPlayers() {
		return players;
	}

	public void setPlayers(Integer players) {
		this.players = players;
	}
	
//	public void shuffle() {
//
//	}
	
	//initially assign 2 cards to each player and then dealer
	public void dealCards() {
		int number = -1;
		int dtempsum = 0;
		// assign cards to dealer
		for (int j=0; j<2; j++) {
			boolean success = true;
			while(success == true) {
				Random rand = new Random();
				number = rand.nextInt(Integer.MAX_VALUE)%52;
				success = set.contains(number);
			}
			set.add(number);
			dealer.addCard(cards.get(number));
			if(cards.get(number).getRank().equals("ACE")) {
//				dealer.setHasAce(true);
//				dealer.setAcecounter(1);
				if(dealer.getSmallsum()==dealer.getBigsum()) {
					dealer.addSmallsum(1);
					dealer.addBigsum(11);
				}
				else {
					dealer.addSmallsum(1);
					dealer.addBigsum(1);
				}
			}
			dealer.addSmallsum(cards.get(number).getValue());
			dealer.addBigsum(cards.get(number).getValue());
			dealer.updateBool();
//			dtempsum+=cards.get(number).getValue();
		}
//		if(dtempsum == 21) {
//			dealer.setBlackjack(true);
//		}
		int ptempsum = 0;
		for(int i=0; i<users.size(); i++) {
			ServerThread user = users.get(i);
			for (int j=0; j<2; j++) {
				boolean success = true;
				while(success == true) {
					Random rand = new Random();
					number = rand.nextInt(Integer.MAX_VALUE)%52;
					success = set.contains(number);
				}
				set.add(number);
				user.addCard(cards.get(number));
				if(cards.get(number).getRank().equals("ACE")) {
//					user.setHasAce(true);
//					user.setAcecounter(1);
					if(user.getSmallsum()==user.getBigsum()) {
						user.addSmallsum(1);
						user.addBigsum(11);
					}
					else {
						user.addSmallsum(1);
						user.addBigsum(1);
					}
				}
				user.addSmallsum(cards.get(number).getValue());
				user.addBigsum(cards.get(number).getValue());
				user.updateBool();
//				ptempsum+=cards.get(number).getValue();
			}
//			if(ptempsum == 21) {
//				user.setBlackjack(true);
//			}
		}
		
	}
	
	// return a String with correctly formated dealer information
	public String displayDealer() {
		String display = "";
		display+="-----------------------------------------------------\n";
		if(hide == true) {
			display+="DEALER\n\nCards: ";
			display+="| ? | " + dealer.getCards().get(1).toString() + " |";
		}
		else {
			display+="DEALER\n\nStatus: ";
			Integer sum = dealer.getSum();
			if(dealer.getBust()==false && dealer.getBlackjack()==false) {
				display+=dealer.getSmallsum();
				display+=" or ";
				display+=dealer.getBigsum();		
				display+="\n";
//				if(dealer.getHasAce()) {
//					if(sum<21) {
//						Integer less = sum-10;
//						display+=less.toString()+" or "+sum.toString();
//					}
//					else {
//						display+=sum.toString();
//					}
//				}
//				else {
//					display+=sum.toString();	
//				}
			}
			else if (dealer.getBust() == true){
				display+=sum.toString();
				display+=" - bust\n";
			}
			else if (dealer.getBlackjack() == true) {
				display+="21 - blackjack\n";
			}
			for(int i=0; i<dealer.getCards().size(); i++) {
				display+="| "+dealer.getCards().get(i).toString() + " | ";
			}
		}
		display+="\n----------------------------------------------------";
		return display;
	}
	
	//return a String with correctly formated player information
	public String displayPlayer(ServerThread st) {
		Integer sum = st.getSum();
		String display = "";
		display+="\n----------------------------------------------------\n";
		display+="Player: " + st.getUsername() + "\n\nStatus: ";
		//there is ACE, if sum <21, return sum or sum+10
		if(st.getBust()==false && st.getBlackjack()==false) {
			display+=st.getSmallsum();
			display+=" or ";
			display+=st.getBigsum();		
//			if(st.getHasAce()) {
//				if(sum<21) {
//					Integer less = sum-10;
//					display+=less.toString()+" or "+sum.toString();
//				}
//				else {
//					display+=sum.toString();
//
//				}
//			}
//			else {
//				display+=sum.toString();	
//			}
		}
		else if (st.getBust() == true){
			display+=sum;
			display+=" - bust";
		}
		else if (st.getBlackjack() == true) {
			display+="21 - blackjack";
		}
		display+="\nCards: ";
		for(int i=0; i<st.getCards().size(); i++) {
			display+="| "+st.getCards().get(i) + " | ";	
		}
		display+="\nChip Total: "+ st.getChips() + " | Bet Amount: "+st.getBet()+"\n";
		display+="---------------------------------------------------\n";
		return display;
	}
	
	// return a String with dealer information and each player's information
	public String display() {
		String display = "";
		display+=displayDealer();
		for(int i=0; i<users.size(); i++) {
			display+=displayPlayer(users.get(i));
		}
		return display;
	}
	
	public String getCardindex() {
		return cardindex;
	}

	public void setCardindex(String cardindex) {
		this.cardindex = cardindex;
	}

	public String [][] getAllcards() {
		return allcards;
	}

	public void setAllcards(String [][] allcards) {
		this.allcards = allcards;
	}
	
	// take a ServerThread as input and assign a Card to this ServerThread. return the assigned Card.toString()
	public String hit(ServerThread st) {
			int number = -1;
			boolean success = true;
			while(success == true) {
				Random rand = new Random();
				number = rand.nextInt(Integer.MAX_VALUE)%52;
				success = set.contains(number);
			}
			set.add(number);
			st.addCard(cards.get(number));
			if(cards.get(number).getRank().equals("ACE")) {
//				user.setHasAce(true);
//				user.setAcecounter(1);
				if(st.getSmallsum()==st.getBigsum()) {
					st.addSmallsum(1);
					st.addBigsum(11);
				}
				else {
					st.addSmallsum(1);
					st.addBigsum(1);
				}
			}
			st.addSmallsum(cards.get(number).getValue());
			st.addBigsum(cards.get(number).getValue());
			st.updateBool();
			return cards.get(number).toString();
	}
	
	// when it is dealer's turn to play, decide whether hit or stay, return the choice as an Integer
	public Integer dealerPlay() {
		dealerhit.clear();
		hide = false;
		if(dealer.getSum() < 17) {
			setHitcounter(0);
			while (dealer.getSum() < 17) { //dealer hit
				int number = -1;
				boolean success = true;
				while(success == true) {
					Random rand = new Random();
					number = rand.nextInt(Integer.MAX_VALUE)%52;
					success = set.contains(number);
				}
				set.add(number);
				dealer.addCard(cards.get(number));
				dealerhit.add(cards.get(number));
				if(cards.get(number).getRank().equals("ACE")) {
//					user.setHasAce(true);
//					user.setAcecounter(1);
					if(dealer.getSmallsum()==dealer.getBigsum()) {
						dealer.addSmallsum(1);
						dealer.addBigsum(11);
					}
					else {
						dealer.addSmallsum(1);
						dealer.addBigsum(1);
					}
				}
				dealer.addSmallsum(cards.get(number).getValue());
				dealer.addBigsum(cards.get(number).getValue());
				dealer.updateBool();
				setHitcounter(getHitcounter() + 1);
			}
			return 2;
		}
		else { 
			return 1;
		}
	}


	public Integer getHitcounter() {
		return hitcounter;
	}


	public void setHitcounter(Integer hitcounter) {
		this.hitcounter = hitcounter;
	}


	public Vector<Card> getDealerhit() {
		return dealerhit;
	}


	public void setDealerhit(Vector<Card> dealerhit) {
		this.dealerhit = dealerhit;
	}

	// return a String of dealer's new Card list
	public String dealerHitDisplay() {
		String display="";
		for(int i=0; i<dealerhit.size(); i++) {
			display+=dealerhit.get(i).toString();
			display+=" | ";
		}
		return display;
	}
	
	public Dealer getDealer() {
		return dealer;
	}
	
	public void setHide(boolean hide) {
		this.hide = hide;
	}
	
	public boolean getHide() {
		return hide;
	}
	
}

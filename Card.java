public class Card {
	private String suit;
	private String rank;
	public Card(String suit, String rank) {   // constructor
	      this.setSuit(suit);
	      this.setRank(rank);
	}
	public String getSuit() {
		return suit;
	}
	public void setSuit(String suit) {
		this.suit = suit;
	}
	public String getRank() {
		return rank;
	}
	public void setRank(String rank) {
		this.rank = rank;
	} 
	public Integer getValue() {
		//special case, not using this method
//		if(rank.equals("ACE")) {
//			return 11;
//		}
		if(rank.equals("TWO")) {
			return 2;
		}
		if(rank.equals("THREE")) {
			return 3;
		}
		if(rank.equals("FOUR")) {
			return 4;
		}
		if(rank.equals("FIVE")) {
			return 5;
		}
		if(rank.equals("SIX")) {
			return 6;
		}
		if(rank.equals("SEVEN")) {
			return 7;
		}
		if(rank.equals("EIGHT")) {
			return 8;
		}
		if(rank.equals("NINE")) {
			return 9;
		}
		if(rank.equals("TEN")) {
			return 10;
		}
		if(rank.equals("JACK")) {
			return 10;
		}
		if(rank.equals("QUEEN")) {
			return 10;
		}
		if(rank.equals("KING")) {
			return 10;
		}
		return 0;
	}
	//return the String of Card
	public String toString() {
		return rank+" of "+suit;
	}
}

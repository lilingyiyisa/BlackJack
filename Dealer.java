import java.util.ArrayList;

//Dealer class acts similar to a ServerThread
public class Dealer {
	private ArrayList<Card> cards = new ArrayList<Card>();
	private Integer sum = 0;
	private boolean hasAce;
	private boolean bust = false;
	private boolean blackjack = false;
	private Integer acecounter = 0;
	private int smallsum = 0;
	private int bigsum = 0;
	
	public ArrayList<Card> getCards() {
		return cards;
	}

	public void setCards(ArrayList<Card> cards) {
		this.cards = cards;
	}
	
	public void addCard(Card card) {
		cards.add(card);
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

	public void setSum(Integer sum) {
		this.sum = sum;
	}
	
	public void addSum(Integer add) {
		this.sum = this.sum+add;
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
//		return sum;
//	}
	
	public boolean getHasAce() {
		return hasAce;
	}
	public void setHasAce(boolean has) {
		this.hasAce = has;
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

	public Integer getAcecounter() {
		return acecounter;
	}

	public void setAcecounter(Integer acecounter) {
		this.acecounter += acecounter;
	}

	//when one round ends, clear all the card list and booleans 
	public void clearCards() {
		cards.clear();
		sum = 0;
		blackjack = false;
		bust = false;
		smallsum = 0;
		bigsum = 0;
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

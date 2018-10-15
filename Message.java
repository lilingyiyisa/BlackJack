import java.io.Serializable;

//Message only serves as a medium between Client and ServerThread
//store the information needed while communicating
public class Message implements Serializable {
		public static final long serialVersionID = 1L;
		private String type;
		private String gamename;
		private boolean result_gamename;
		private String username;
		private boolean result_username;
		private Client client;
		private String result_joinin;
		private Integer players;
		private boolean result_readytostart;
		private boolean result_turntomakebet;
		private String whoseturn;
		private Integer bet;
		private Integer otherbet;
		private String otherbetusername;
		private String status;
		private String whoseturnhs;
		private String stayedusername;
		private String dealtcard;
		private boolean result_endthisrun;
		private boolean result_otherbusted;
		private boolean result_otherblackjack;
		private boolean bust;
		private boolean blackjack;
		private String dealerdisplay;
		private Integer hittime;
		private String shorterdisplay;
		private boolean result_empty;
		private String chipsdata;
		private Integer chips;
		private String winlose;
		
		public Message(String type) {
			this.type = type;
		}
		
		public String getType() {
			return type;
		}

		public String getGamename() {
			return gamename;
		}

		public void setGamename(String gamename) {
			this.gamename = gamename;
		}

		public boolean getResult_gamename() {
			return result_gamename;
		}

		public void setResult_gamename(boolean result_gamename) {
			this.result_gamename = result_gamename;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String gamename, String username) {
			this.gamename = gamename;
			this.username = username;
		}

		public boolean getResult_username() {
			return result_username;
		}

		public void setResult_username(boolean result_username) {
			this.result_username = result_username;
		}

//		public Client getClient() {
//			return client;
//		}
//
//		public void setClient(Client client) {
//			this.client = client;
//		}

		public String getResult_joinin() {
			return result_joinin;
		}

		public void setResult_joinin(String result_joinin) {
			this.result_joinin = result_joinin;
		}

		public Integer getPlayers() {
			return players;
		}

		public void setPlayers(Integer players) {
			this.players = players;
		}

		public boolean getResult_readytostart() {
			return result_readytostart;
		}

		public void setResult_readytostart(boolean result_readytostart) {
			this.result_readytostart = result_readytostart;
		}

		public boolean getResult_turntomakebet() {
			return result_turntomakebet;
		}

		public void setResult_turntomakebet(boolean result_turntomakebet) {
			this.result_turntomakebet = result_turntomakebet;
		}

		public String getWhoseturn() {
			return whoseturn;
		}

		public void setWhoseturn(String whoseturn) {
			this.whoseturn = whoseturn;
		}

		public Integer getBet() {
			return bet;
		}

		public void setBet(Integer bet) {
			this.bet = bet;
		}

		public Integer getOtherbet() {
			return otherbet;
		}

		public void setOtherbet(Integer otherbet) {
			this.otherbet = otherbet;
		}

		public String getOtherbetusername() {
			return otherbetusername;
		}

		public void setOtherbetusername(String otherbetusername) {
			this.otherbetusername = otherbetusername;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getWhoseturnhs() {
			return whoseturnhs;
		}

		public void setWhoseturnhs(String whoseturnhs) {
			this.whoseturnhs = whoseturnhs;
		}
		
		public String getStayedusername() {
			return stayedusername;
		}

		public void setStayedusername(String stayedusername) {
			this.stayedusername = stayedusername;
		}

		public String getDealtcard() {
			return dealtcard;
		}

		public void setDealtcard(String dealtcard) {
			this.dealtcard = dealtcard;
		}

		public boolean getResult_endthisrun() {
			return result_endthisrun;
		}

		public void setResult_endthisrun(boolean result_endthisrun) {
			this.result_endthisrun = result_endthisrun;
		}

		public boolean getResult_otherbusted() {
			return result_otherbusted;
		}

		public void setResult_otherbusted(boolean result_otherbusted) {
			this.result_otherbusted = result_otherbusted;
		}

		public boolean getResult_otherblackjack() {
			return result_otherblackjack;
		}

		public void setResult_otherblackjack(boolean result_otherblackjack) {
			this.result_otherblackjack = result_otherblackjack;
		}

		public boolean getBust() {
			return bust;
		}

		public void setBust(boolean bust) {
			this.bust = bust;
		}

		public boolean getBlackjack() {
			return blackjack;
		}

		public void setBlackjack(boolean blackjack) {
			this.blackjack = blackjack;
		}

		public String getDealerdisplay() {
			return dealerdisplay;
		}

		public void setDealerdisplay(String dealerdisplay) {
			this.dealerdisplay = dealerdisplay;
		}

		public Integer getHittime() {
			return hittime;
		}

		public void setHittime(Integer hittime) {
			this.hittime = hittime;
		}

		public String getShorterdisplay() {
			return shorterdisplay;
		}

		public void setShorterdisplay(String shorterdisplay) {
			this.shorterdisplay = shorterdisplay;
		}

		public boolean getResult_empty() {
			return result_empty;
		}

		public void setResult_empty(boolean result_empty) {
			this.result_empty = result_empty;
		}

		public String getChipsdata() {
			return chipsdata;
		}

		public void setChipsdata(String chipsdata) {
			this.chipsdata = chipsdata;
		}

		public Integer getChips() {
			return chips;
		}

		public void setChips(Integer chips) {
			this.chips = chips;
		}

		public String getWinlose() {
			return winlose;
		}

		public void setWinlose(String winlose) {
			this.winlose = winlose;
		}

		
}

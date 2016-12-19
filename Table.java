import java.util.ArrayList;

public class Table {

	static final int MAXPLAYER = 4;
	private Deck deck;
	private Player[] players;
	private Dealer dealer = new Dealer();
	int[] pos_betArray = new int[MAXPLAYER];
	private int nDecks;

	public static void main(String[] args) {

	}

	public Table(int nDeck) {

		this.nDecks = nDecks;
		deck = new Deck(nDeck);
		players = new Player[MAXPLAYER];

	}

	public void set_player(int pos, Player p) {
		if ((pos >= 0) && (pos < MAXPLAYER)) {
			players[pos] = p;
		}

	}

	public Player[] get_player() {

		return players;
	}

	public void set_dealer(Dealer d) {

		dealer = d;
	}

	public Card get_face_up_card_of_dealer() {
		return dealer.getOneRoundCard().get(1);// 第2張牌-->index = 1
	}

	private void ask_each_player_about_bets() {
		for (int i = 0; i < players.length; i++) {
			if (this.players[i] != null) {
				players[i].say_hello();
				int bet = players[i].make_bet();
				if (bet > players[i].get_current_chips()) {
					pos_betArray[i] = 0;
				} 
				else {
					pos_betArray[i] = players[i].make_bet();
				}
			}
		}
	}

	private void distribute_cards_to_dealer_and_players() {
		for (int i = 0; i < players.length; i++) {
			if ((players[i] != null) && (pos_betArray[i] != 0)) {
				ArrayList<Card> playerCard = new ArrayList();
				playerCard.add(deck.getOneCard(true));
				playerCard.add(deck.getOneCard(true));
				players[i].setOneRoundCard(playerCard);
			}
		}
		ArrayList<Card> dealerCard = new ArrayList();
		dealerCard.add(deck.getOneCard(false)); // 第1張蓋著
		dealerCard.add(deck.getOneCard(true)); // 第2張打開
		dealer.setOneRoundCard(dealerCard);
		System.out.print("Dealer's face up card is ");
		Card dealerFaceUpCard = get_face_up_card_of_dealer();
		dealerFaceUpCard.printCard();
	}

	private void ask_each_player_about_hits() {
		for (int i = 0; i < players.length; i++) {
			ArrayList<Card> playerCard = new ArrayList();
			boolean hit = false;
			do {
				hit = players[i].hit_me(this);
				if (hit) {
					playerCard = players[i].getOneRoundCard();
					playerCard.add(deck.getOneCard(true));
					players[i].setOneRoundCard(playerCard);
					System.out.print("Hit! ");
					System.out.println(players[i].get_name() + "'s Cards now:");
					for (Card c : playerCard) {
						c.printCard();
					}
				} 
				else {
					System.out.println(players[i].get_name() + ", Pass hit!");
					System.out.println(players[i].get_name() + ", Final Card:");
					for (Card c : players[i].getOneRoundCard()) {
						c.printCard();
					}
				}
			} 
			while (hit);
		}

	}

	private void ask_dealer_about_hits() {
		ArrayList<Card> dealerCard = new ArrayList();
		boolean hit = false;
		do {
			hit = dealer.hit_me(this);
			if (hit) {

				dealerCard = dealer.getOneRoundCard();
				dealerCard.add(deck.getOneCard(true));
				dealer.setOneRoundCard(dealerCard);
				System.out.print("Hit! ");
			}
			if (dealer.getTotalValue() > 21) {
				hit = false;
			}
		} 
		while (hit);
		System.out.println("Dealer's hit is over!");

	}

	private void calculate_chips() {
		int dealerValue = dealer.getTotalValue();
		System.out.print("Dealer's card value is " + dealerValue + " ,Cards:");
		dealer.printAllCard();
		
		for (int i = 0; i < players.length; i++) {
			if ((players[i] != null) && (pos_betArray[i] != 0)) {
				int playerValue = players[i].getTotalValue();
				System.out.print(players[i].get_name() + " card value is " + playerValue);
				
				//1--玩家爆了
				if (players[i].getTotalValue() > 21) {
					//1.1莊家也爆了-->平手
					if (dealer.getTotalValue() > 21) {
						System.out.println(", chips have no change!, the Chips now is: " 
											+ players[i].get_current_chips());
					} 
					//1.2莊家沒爆-->玩家輸，莊家贏
					else {
						players[i].increase_chips(-pos_betArray[i]);
						System.out.println(", Loss " + pos_betArray[i] 
											+ " Chips, the Chips now is: "
											+ players[i].get_current_chips());
					}
				} 
				//2--玩家剛好21點
				else if (players[i].getTotalValue() == 21) { 
					//2.1 BlackJack-->前2張牌是10+A
					if ((players[i].getOneRoundCard().size() == 2) && (players[i].hasAce())) 
					{
						//2.1.1 莊家不是21點-->玩家贏，莊家輸-->2倍賭資
						if (dealer.getTotalValue() != 21) {
							players[i].increase_chips(pos_betArray[i]*2);
							System.out.println("Get " + pos_betArray[i]
									+ " Chips, the Chips now is: " 
									+ players[i].get_current_chips());
						} 
						//2.1.2 莊家也一樣-->平手
						else if ((dealer.getOneRoundCard().size() == 2) && (dealer.hasAce())){
							
							System.out.println("chips have no change!, the Chips now is: "
									+ players[i].get_current_chips());
						} 
						//2.1.3 其他-->玩家贏，莊家輸-->2倍賭資
						else {
							players[i].increase_chips(pos_betArray[i]*2);
							System.out.println("Get " + pos_betArray[i]
												+ " Chips, the Chips now is: " 
												+ players[i].get_current_chips());
						}
					}
					//2.2 不是BlackJack，但21點，莊家不是21點-->玩家贏，莊家輸
					else if (dealer.getTotalValue() != 21) {
						players[i].increase_chips(pos_betArray[i] );
						System.out.println(",Get " + pos_betArray[i] 
								+ " Chips, the Chips now is: "
								+ players[i].get_current_chips());
					} 
					//2.3 也是21點，但不是一開始就發滿的
					else {          
						System.out.println(
								",chips have no change!The Chips now is: " 
						+ players[i].get_current_chips());
					}
				} 
				//3--莊家爆了-->玩家贏，莊家輸
				else if (dealer.getTotalValue() > 21) {
					players[i].increase_chips(pos_betArray[i]);
					System.out.println(", Get " + pos_betArray[i] 
										+ " Chips, the Chips now is: "
										+ players[i].get_current_chips());
				} 
				//4--都沒爆，但是玩家點數和比較大-->玩家贏，莊家輸
				else if (dealer.getTotalValue() < players[i].getTotalValue()) {
					players[i].increase_chips(pos_betArray[i]);
					System.out.println(", Get " + pos_betArray[i] 
										+ " Chips, the Chips now is: "
										+ players[i].get_current_chips());
				} 
				//5--都沒爆，但是莊家點數和比較大-->玩家輸，莊家贏
				else if (dealer.getTotalValue() > players[i].getTotalValue()) {
					players[i].increase_chips(-pos_betArray[i]);
					System.out.println(", Loss " + pos_betArray[i] 
										+ " Chips, the Chips now is: "
										+ players[i].get_current_chips());
				} 
				//其他-->平手
				else {
					System.out.println(", chips have no change! The Chips now is: " 
										+ players[i].get_current_chips());
				}

			}
		}

	}

	public int[] get_palyers_bet() {
		return pos_betArray;

	}

	public void play() {
		ask_each_player_about_bets();
		distribute_cards_to_dealer_and_players();
		ask_each_player_about_hits();
		ask_dealer_about_hits();
		calculate_chips();
	}

}
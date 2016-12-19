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
		return dealer.getOneRoundCard().get(1);// ��2�i�P-->index = 1
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
		dealerCard.add(deck.getOneCard(false)); // ��1�i�\��
		dealerCard.add(deck.getOneCard(true)); // ��2�i���}
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
				
				//1--���a�z�F
				if (players[i].getTotalValue() > 21) {
					//1.1���a�]�z�F-->����
					if (dealer.getTotalValue() > 21) {
						System.out.println(", chips have no change!, the Chips now is: " 
											+ players[i].get_current_chips());
					} 
					//1.2���a�S�z-->���a��A���aĹ
					else {
						players[i].increase_chips(-pos_betArray[i]);
						System.out.println(", Loss " + pos_betArray[i] 
											+ " Chips, the Chips now is: "
											+ players[i].get_current_chips());
					}
				} 
				//2--���a��n21�I
				else if (players[i].getTotalValue() == 21) { 
					//2.1 BlackJack-->�e2�i�P�O10+A
					if ((players[i].getOneRoundCard().size() == 2) && (players[i].hasAce())) 
					{
						//2.1.1 ���a���O21�I-->���aĹ�A���a��-->2�����
						if (dealer.getTotalValue() != 21) {
							players[i].increase_chips(pos_betArray[i]*2);
							System.out.println("Get " + pos_betArray[i]
									+ " Chips, the Chips now is: " 
									+ players[i].get_current_chips());
						} 
						//2.1.2 ���a�]�@��-->����
						else if ((dealer.getOneRoundCard().size() == 2) && (dealer.hasAce())){
							
							System.out.println("chips have no change!, the Chips now is: "
									+ players[i].get_current_chips());
						} 
						//2.1.3 ��L-->���aĹ�A���a��-->2�����
						else {
							players[i].increase_chips(pos_betArray[i]*2);
							System.out.println("Get " + pos_betArray[i]
												+ " Chips, the Chips now is: " 
												+ players[i].get_current_chips());
						}
					}
					//2.2 ���OBlackJack�A��21�I�A���a���O21�I-->���aĹ�A���a��
					else if (dealer.getTotalValue() != 21) {
						players[i].increase_chips(pos_betArray[i] );
						System.out.println(",Get " + pos_betArray[i] 
								+ " Chips, the Chips now is: "
								+ players[i].get_current_chips());
					} 
					//2.3 �]�O21�I�A�����O�@�}�l�N�o����
					else {          
						System.out.println(
								",chips have no change!The Chips now is: " 
						+ players[i].get_current_chips());
					}
				} 
				//3--���a�z�F-->���aĹ�A���a��
				else if (dealer.getTotalValue() > 21) {
					players[i].increase_chips(pos_betArray[i]);
					System.out.println(", Get " + pos_betArray[i] 
										+ " Chips, the Chips now is: "
										+ players[i].get_current_chips());
				} 
				//4--���S�z�A���O���a�I�ƩM����j-->���aĹ�A���a��
				else if (dealer.getTotalValue() < players[i].getTotalValue()) {
					players[i].increase_chips(pos_betArray[i]);
					System.out.println(", Get " + pos_betArray[i] 
										+ " Chips, the Chips now is: "
										+ players[i].get_current_chips());
				} 
				//5--���S�z�A���O���a�I�ƩM����j-->���a��A���aĹ
				else if (dealer.getTotalValue() > players[i].getTotalValue()) {
					players[i].increase_chips(-pos_betArray[i]);
					System.out.println(", Loss " + pos_betArray[i] 
										+ " Chips, the Chips now is: "
										+ players[i].get_current_chips());
				} 
				//��L-->����
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
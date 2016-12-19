import java.util.ArrayList;
import java.util.Scanner;

public class Player extends Person {// 重複地宣告&method要刪掉

	Scanner sc = new Scanner(System.in);
	private String name;
	private int chips;
	private int bet;

	public static void main(String[] args) {

	}

	public Player(String name, int chips) {
		this.name = name;
		this.chips = chips;
	}

	public String get_name() {

		return name;

	}

	public void say_hello() {

		System.out.println("Hello, I am " + name + ".");
		System.out.println("I have " + chips + " chips.");

	}

	public int make_bet() {
		bet = 1;
		if (chips >= 1) {

			return bet;

		} else {

			System.out.println("無剩餘籌碼，無法下注");
			return 0;
		}
	}
	@Override
	public boolean hit_me(Table table) {

		int total_value = getTotalValue();
		if (total_value < 17)
			return true;
		else if (total_value == 17 && hasAce()) {
			return true;
		} else {
			if (total_value >= 21)
				return false;
			else {
				Player[] players = table.get_player();
				int lose_count = 0;
				int v_count = 0;
				int[] betArray = table.get_palyers_bet();
				for (int i = 0; i < players.length; i++) {
					if (players[i] == null) {
						continue;
					}
					if (players[i].getTotalValue() != 0) {
						if (total_value < players[i].getTotalValue()) {
							lose_count += betArray[i];
						} else if (total_value > players[i].getTotalValue()) {
							v_count += betArray[i];
						}
					}
				}
				if (v_count < lose_count)
					return true;
				else
					return false;
			}
		}
	}

	public void increase_chips(int d) {

		chips += d;
	}

	public int get_current_chips() {

		return chips;
	}
}

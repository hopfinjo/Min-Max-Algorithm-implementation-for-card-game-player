import java.util.Random;

public class Deck {

    private Card[] cards;
    public static final int numValues = 10;
    public static final int numSuits = 4;
    public static final int length = numValues * numSuits;
    private static Random random = new Random();

    public Deck() {
        int k = 0;
        cards = new Card[length];
        for (int j = 0; j < numSuits; j++) {
            for (int i = 0; i < 9; i++)
                cards[k++] = new Card(i, j);
            cards[k++] = new Card(12, j);
        }
    }

    public Deck(Deck deck) {
        cards = deck.cards.clone();
    }

    public Card get(int i) {
        return cards[i];
    }

    public static void shuffle(Card[] cards) {
        for (int i = 1; i < cards.length; i++) {
            int r = random.nextInt(i + 1);
            Card t = cards[r];
            cards[r] = cards[i];
            cards[i] = t;
        }
    }

    public void shuffle() {
        shuffle(cards);
    }
}

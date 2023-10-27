public class Card {

    public enum Value { TWO("2"), THREE("3"), FOUR("4"), FIVE("5"), SIX("6"), SEVEN("7"), 
	    EIGHT("8"), NINE("9"), TEN("10"), JACK("Jack"), QUEEN("Queen"), KING("King"), ACE("Ace");
	private String name;
	Value(String name) { this.name = name; }
	public String toString() { return name; }
    }

    public enum Suit { CLUBS("clubs"), DIAMONDS("diamonds"), HEARTS("hearts"), SPADES("spades");
	private String name;
	Suit(String name) { this.name = name; }
	public String toString() { return name; }
    }

    private static Value[] allValues = Value.values();
    private static Suit[] allSuits = Suit.values();

    public Value value;
    public Suit suit;

    public Card(Value value, Suit suit) {
	this.value = value; this.suit = suit; 
    }

    public Card(Card c) {
	this(c.value, c.suit);
    }

    public Card(int i, int j) {
	this(allValues[i], allSuits[j]);
    }

    public static int convert(char c) {
	switch(c) {
	case 'c' : case 'C' : return 0;
	case 'd' : case 'D' : return 1;
	case 'h' : case 'H' : return 2;
	case 's' : case 'S' : return 3;
	default : return 0;
	}
    }

    public Card(int i, char c) {
	this(i-2, convert(c));
    }

    public Card(int i) {
	this(i / 4, i % 4);
    }

    public String toString() { return "" + value + " of " + suit + " (" + getNumber() + ")"; }

    public int getNumber() { return value.ordinal() * 4 + suit.ordinal(); }

    public boolean equals(Object o) {
	if(o == null) return false;
	if(!(o instanceof Card)) return false;
	return value == ((Card)o).value && suit == ((Card)o).suit;
    }
}

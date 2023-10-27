/*
 * Maximilian Hopfer
 * Card-Game Player using Min-Max-Algorithm with alpha beta pruning
 * 
 * The algorithm stores picked cards and does not re-compute cards with same value:
 * Since the two cards 3hearts, 3clubs have the same outcome for a hand, if I computed possible outcome
 * for 3hearts as a first card played, I can assume the same for 3clubs as a first card played.
 * Furthermore, I use a timed loop to maximize the possible hands my algorithm can play against.
 * To pick the best possible card to play, I use the difference of wins-losses.
 * Also I am not creating new nodes, instead I am undoing each move after the recursive call
 * 
 */

import java.util.ArrayList;
import java.util.HashMap;

public class MaxiH {

	public static Player getPlayer() {
		return new MaxiPlayer();
	}

	public static class MaxiPlayer implements Player {

		public class Node {
			ArrayList<Card> myhand, playedCards, otherhandsCards;
			int tricks1, tricks2, numOnTable, onTable;

			public Node(ArrayList<Card> myhand, ArrayList<Card> playedCards, ArrayList<Card> otherhandsCards,
					int onTable,
					int numOnTable, int tricks1, int tricks2) {
				this.myhand = new ArrayList<>(myhand);
				this.playedCards = new ArrayList<>(playedCards);
				this.otherhandsCards = new ArrayList<>(otherhandsCards);
				this.numOnTable = numOnTable;
				this.onTable = onTable;
				this.tricks1 = tricks1;
				this.tricks2 = tricks2;
			}

			public boolean gameIsOver() {
				if (Math.abs(tricks1 - tricks2) > (14 - playedCards.size())) {
					return true;
				}
				return false;
			}

			public int getValueOfCard(Card card) {
				return card.value == Card.Value.ACE ? 1 : card.value.ordinal() + 2;

			}

			public void updateTricksandNumTable(boolean isMaximizingPlayer) {
				if (isMaximizingPlayer) {
					if (checkPerfectSquare(onTable)) {
						tricks1 += numOnTable;
						numOnTable = 0;
					}
				}
				if (checkPerfectSquare(onTable)) {
					tricks2 += numOnTable;
					numOnTable = 0;
				}
			}

		}

		public int getValueOfCard(Card card) {
			return card.value == Card.Value.ACE ? 1 : card.value.ordinal() + 2;

		}

		public boolean checkPerfectSquare(int i) {
			return i == 1 || i == 4 || i == 9 || i == 16 || i == 25 || i == 36 || i == 49 || i == 64 || i == 81
					|| i == 100 || i == 121; // 4 10's, 4 9's, 4 8's, 2 7's = 122.

		}

		public ArrayList<Card> getAllPossibleCards(ArrayList<Card> myhand, ArrayList<Card> playedCards) {
			Deck deck = new Deck();
			deck.shuffle();
			ArrayList<Card> allCards = new ArrayList<>();
			for (int i = 0; i < Deck.length; i++) {
				Card card = deck.get(i);
				if (myhand.contains(card) || playedCards.contains(card)) {
					continue;
				}
				allCards.add(card);
			}
			return allCards;
		}

		public ArrayList<Card> getRandomHand(ArrayList<Card> myhand, ArrayList<Card> playedCards) {

			int randomhandsize = 0;
			if (playedCards.size() % 2 == 0) {
				randomhandsize = myhand.size();
			} else {
				randomhandsize = myhand.size() - 1;
			}
			Deck deck = new Deck();
			deck.shuffle();
			ArrayList<Card> randomcardsarr = new ArrayList<>();
			ArrayList<Card> randomhand = new ArrayList<>();

			for (int i = 0; i < 40; i++) {
				Card card = deck.get(i);
				if (myhand.contains(card) || playedCards.contains(card)) {
					continue;
				}
				randomcardsarr.add(card);
			}

			for (int i = 0; i < randomhandsize; i++) {
				randomhand.add(randomcardsarr.get(i));
			}

			return randomhand;
		}

		public boolean checkifCardWasAlreadyPLayed(int[] cardsAlreadyUsed, int currentCardValueIndex,
				boolean isMaxPlayer,
				Card currentCard, HashMap<Card, Integer> hmWins, HashMap<Card, Integer> hmLoss) {

			if (cardsAlreadyUsed[currentCardValueIndex] == 0) {
				return false;
			}

			// if won the last time with this card, win now again!!
			if (isMaxPlayer) {
				if (cardsAlreadyUsed[currentCardValueIndex] == 1) {
					if (hmWins.containsKey(currentCard)) {
						int value = hmWins.get(currentCard);
						value++;
						hmWins.put(currentCard, value);
					} else {
						hmWins.put(currentCard, 1);
					}
				} else {
					if (hmLoss.containsKey(currentCard)) {
						int value = hmLoss.get(currentCard);
						value++;
						hmLoss.put(currentCard, value);
					} else {
						hmLoss.put(currentCard, 1);
					}
				}
			} else {
				if (cardsAlreadyUsed[currentCardValueIndex] == -1) {
					if (hmWins.containsKey(currentCard)) {
						int value = hmWins.get(currentCard);
						value++;
						hmWins.put(currentCard, value);
					} else {
						hmWins.put(currentCard, 1);
					}
				} else {
					if (hmLoss.containsKey(currentCard)) {
						int value = hmLoss.get(currentCard);
						value++;
						hmLoss.put(currentCard, value);
					} else {
						hmLoss.put(currentCard, 1);
					}
				}
			}

			return true;
		}

		public int getWinningCount(HashMap<Card, Integer> hmWins, HashMap<Card, Integer> hmLoss, Card currC) {
			if (hmLoss.get(currC) == null && hmWins.get(currC) == null) {
				return 0;
			}

			int thisCount = 0;
			if (hmLoss.get(currC) != null && hmWins.get(currC) != null) {
				thisCount = hmWins.get(currC) - hmLoss.get(currC);
			} else if (hmLoss.get(currC) == null) {
				thisCount = hmWins.get(currC);
			} else if (hmWins.get(currC) == null) {
				thisCount = (-1) * hmLoss.get(currC);
			}
			return thisCount;
		}

		public Card playCard(ArrayList<Card> myhand, ArrayList<Card> playedCards, int numOnTable, int onTable,
				int tricks1, int tricks2) {

			HashMap<Card, Integer> hmWins = new HashMap<>();
			HashMap<Card, Integer> hmLoss = new HashMap<>();

			long startTime = System.currentTimeMillis();
			long duration = 9500;

			while (System.currentTimeMillis() - startTime < duration) {

				ArrayList<Card> randomHand = getRandomHand(myhand, playedCards);
				Node node = new Node(myhand, playedCards, randomHand, onTable, numOnTable,
						tricks1, tricks2);

				// cardsAlreadyUsed stores result of each card-value that is picked to play
				// if ACE is played, then pos0 of cardsAlreadyUsed has result of ACE
				int[] cardsAlreadyUsed = new int[10];

				for (int i = 0; i < myhand.size(); i++) {

					Card currentCard = myhand.get(i);
					int currentCardValueIndex = getValueOfCard(currentCard) - 1;

					if (playedCards.size() % 2 == 0) {
						// I am tricks1 and I want to maximize tricks1!

						// updates the wins and loss hashmaps as well.
						if (checkifCardWasAlreadyPLayed(cardsAlreadyUsed, currentCardValueIndex,
								true, currentCard, hmWins, hmLoss)) {
							continue;
						}


						int oldNumOnTable = node.numOnTable;
						int oldTricks1 = node.tricks1;
						int oldTricks2 = node.tricks2;

						Card currCard = node.myhand.get(i);

						node.myhand.remove(i);
						node.playedCards.add(currCard);
						node.numOnTable++;
						node.onTable += node.getValueOfCard(currCard);

						// update tricks and set onTable to 0 if needed
						node.updateTricksandNumTable(true);

						int depth = node.myhand.size() + node.otherhandsCards.size();

						int score = minimax(node, depth, false, Integer.MIN_VALUE,
								Integer.MAX_VALUE);

						// if I won with this card:
						if (score == 1) {
							cardsAlreadyUsed[currentCardValueIndex] = -1;
							if (hmWins.containsKey(currCard)) {
								int value = hmWins.get(currCard);
								value++;
								hmWins.put(currCard, value);
							} else {
								hmWins.put(currCard, 1);
							}
						}

						// if I lost with this card! means that i decrement the moves
						if (score == -1) {
							cardsAlreadyUsed[currentCardValueIndex] = -1;

							if (hmLoss.containsKey(currCard)) {
								int value = hmLoss.get(currCard);
								value++;
								hmLoss.put(currCard, value);
							} else {
								hmLoss.put(currCard, 1);
							}
						}

						// Undo the move
						node.myhand.add(i, currCard);
						node.playedCards.remove(currCard);
						node.numOnTable--;
						node.onTable -= node.getValueOfCard(currCard);
						node.numOnTable = oldNumOnTable;
						node.tricks1 = oldTricks1;
						node.tricks2 = oldTricks2;

					} else {
						// I am tricks2 and want to maximize tricks2.

						// updates the wins and loss hashmaps as well.
						if (checkifCardWasAlreadyPLayed(cardsAlreadyUsed, currentCardValueIndex,
								true, currentCard, hmWins, hmLoss)) {
							continue;
						}

						int oldNumOnTable = node.numOnTable;
						int oldTricks1 = node.tricks1;
						int oldTricks2 = node.tricks2;

						Card currCard = node.myhand.get(i);

						node.myhand.remove(i);
						node.playedCards.add(currCard);
						node.numOnTable++;
						node.onTable += node.getValueOfCard(currCard);

						// update tricks and set onTable to 0 if needed
						node.updateTricksandNumTable(false);

						int depth = node.myhand.size() + node.otherhandsCards.size();

						int score = minimax(node, depth, true, Integer.MIN_VALUE,
								Integer.MAX_VALUE);

						// if I won with this card: score = -1
						if (score == -1) {
							cardsAlreadyUsed[currentCardValueIndex] = 1;
							if (hmWins.containsKey(currCard)) {
								int value = hmWins.get(currCard);
								value++;
								hmWins.put(currCard, value);
							} else {
								hmWins.put(currCard, 1);
							}
						}

						// if I lost with this card! means that i decrement the moves
						if (score == 1) {
							cardsAlreadyUsed[currentCardValueIndex] = -1;
							if (hmLoss.containsKey(currCard)) {
								int value = hmLoss.get(currCard);
								value++;
								hmLoss.put(currCard, value);
							} else {
								hmLoss.put(currCard, 1);
							}
						}

						// Undo the move
						node.myhand.add(i, currCard);
						node.playedCards.remove(currCard);
						node.numOnTable--;
						node.onTable -= node.getValueOfCard(currCard);
						node.numOnTable = oldNumOnTable;
						node.tricks1 = oldTricks1;
						node.tricks2 = oldTricks2;
					}

				}

			}
			int bestCount = Integer.MIN_VALUE;
			Card bestCard = null;

			for (int i = 0; i < myhand.size(); i++) {

				Card currC = myhand.get(i);
				int thisCount = getWinningCount(hmWins, hmLoss, currC);
				if (thisCount > bestCount) {
					bestCard = currC;
					bestCount = thisCount;
				}

			}
			// in case there is no winning card, play first card in hand.
			if (bestCard == null) {
				bestCard = myhand.get(0);
			}

			return bestCard;
		}

		public int minimax(Node node, int depth, boolean isMaximizingPlayer, int alpha, int beta) {
			// returns 1 for a win of maxPlayer, and -1 for a win of MinPla.
			if (node.gameIsOver() || depth == 0) {
				if (node.tricks1 > node.tricks2) {
					return 1;
				} else if (node.tricks2 > node.tricks1) {
					return -1;
				}
				return 0;
			}

			else if (isMaximizingPlayer) {

				int bestVal = Integer.MIN_VALUE;

				int[] cardsAlreadyComputed = new int[10];

				for (int i = 0; i < node.myhand.size(); i++) {
					boolean cardsAlreadyComputedBefore = false;

					Card currCard = node.myhand.get(i);

					int currentCardValueIndex = getValueOfCard(currCard) - 1;

					if (cardsAlreadyComputed[currentCardValueIndex] != 0) {
						cardsAlreadyComputedBefore = true;
					}

					int oldNumOnTable = node.numOnTable;
					int oldTricks1 = node.tricks1;
					int oldTricks2 = node.tricks2;

					node.myhand.remove(i);
					node.playedCards.add(currCard);
					node.numOnTable++;
					node.onTable += node.getValueOfCard(currCard);

					node.updateTricksandNumTable(true);

					int value;
					if (cardsAlreadyComputedBefore) {
						value = cardsAlreadyComputed[currentCardValueIndex];
					} else {
						value = minimax(node, depth - 1, false, alpha, beta);
						cardsAlreadyComputed[currentCardValueIndex] = value;
					}

					bestVal = Math.max(bestVal, value);
					alpha = Math.max(alpha, bestVal);

					node.myhand.add(i, currCard);
					node.playedCards.remove(currCard);
					node.numOnTable--;
					node.onTable -= node.getValueOfCard(currCard);
					node.numOnTable = oldNumOnTable;
					node.tricks1 = oldTricks1;
					node.tricks2 = oldTricks2;

					if (beta <= alpha) {
						break;
					}
				}
				return bestVal;
			} else {

				int bestVal = Integer.MAX_VALUE;

				int[] cardsAlreadyComputed = new int[10];

				for (int i = 0; i < node.otherhandsCards.size(); i++) {
					boolean cardsAlreadyComputedBefore = false;

					Card currCard = node.otherhandsCards.get(i);

					int currentCardValueIndex = getValueOfCard(currCard) - 1;

					if (cardsAlreadyComputed[currentCardValueIndex] != 0) {

						cardsAlreadyComputedBefore = true;
					}

					int oldNumOnTable = node.numOnTable;
					int oldTricks1 = node.tricks1;
					int oldTricks2 = node.tricks2;

					node.otherhandsCards.remove(i);
					node.playedCards.add(currCard);
					node.numOnTable++;
					node.onTable += node.getValueOfCard(currCard);
					node.updateTricksandNumTable(false);

					int value;
					if (cardsAlreadyComputedBefore) {
						value = cardsAlreadyComputed[currentCardValueIndex];
					} else {
						value = minimax(node, depth - 1, true, alpha, beta);
						cardsAlreadyComputed[currentCardValueIndex] = value;

					}

					bestVal = Math.min(bestVal, value);
					beta = Math.min(beta, bestVal);

					node.otherhandsCards.add(i, currCard);
					node.playedCards.remove(currCard);
					node.numOnTable--;
					node.onTable -= node.getValueOfCard(currCard);
					node.numOnTable = oldNumOnTable;
					node.tricks1 = oldTricks1;
					node.tricks2 = oldTricks2;

					if (beta <= alpha) {
						break;
					}

				}
				return bestVal;
			}
		}
	}
}

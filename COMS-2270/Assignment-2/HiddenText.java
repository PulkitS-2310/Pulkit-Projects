package hw2;

public class HiddenText {
    private String word;
    private char[] displayedText;

    public HiddenText(String phrase) {
        word = phrase;
        displayedText = new char[word.length()];
        for (int i = 0; i < word.length(); i++) {
            if (isLetter(word.charAt(i))) {
                displayedText[i] = '*';
            } else {
                displayedText[i] = word.charAt(i);
            }
        }
    }

    public void update(char letter) {
        for (int i = 0; i < displayedText.length; i++) {
            if (Character.toUpperCase(word.charAt(i)) == Character.toUpperCase(letter)) {
                displayedText[i] = letter;
            }
        }
    }

    public int countHiddenConsonants() {
        int consonants = 0;
        for (int i = 0; i < displayedText.length; i++) {
            if (displayedText[i] == '*' && "AEIOUaeiou".indexOf(word.charAt(i)) == -1){
                consonants++;
            }
        }
        return consonants;
    }

    protected boolean isLetter(char letter) {
        return (letter >= 'A' && letter <= 'Z') || (letter >= 'a' && letter <= 'z');
    }
//
//    protected boolean isVowel(char letter) {
//       
//    }

    public char[] getDisplayedText() {
        return displayedText;
    }

    public String getHiddenText() {
        return word;
    }

    public int countHiddenLetters() {
        int count = 0;
        for (char c : displayedText) {
            if (c == '*') {
                count++;
            }
        }
        return count;
    }

    public int letterCount(char ch) {
        int letterCount = 0;
        for(int i=0;i<displayedText.length;i++)
        {
        	if (Character.toUpperCase(word.charAt(i))==Character.toUpperCase(ch))
        	{
        		letterCount++;
        	}
        }
       
        
        return letterCount;
    }

    public void updateAllRemaining() {
        for (int i = 0; i < displayedText.length; i++) {
            if (displayedText[i] == '*') {
                displayedText[i] = Character.toUpperCase(word.charAt(i));
            }
        }
    }
}

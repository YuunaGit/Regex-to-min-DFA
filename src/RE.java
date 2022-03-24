import java.util.ArrayList;
import java.util.Stack;

public class RE {

    private boolean isLetter(char c) {
        return (c != '(' && c != ')' && c != '*' && c != '|' && c != '·');
    }

    private int getPriority(char c) {
        return switch (c) {
            case '(' -> 0;
            case '|' -> 1;
            case '·' -> 2;
            case '*' -> 3;
            default -> c;
        };
    }

    public ArrayList<Character> toRE(char[] input) {
        ArrayList<Character> RE = new ArrayList<>();
        RE.add(input[0]);
        for (int i = 1; i < input.length; i++) {
            char leftChar = RE.get(RE.size() - 1);
            char rightChar = input[i];
            if (
                (isLetter(leftChar) && (isLetter(rightChar) || rightChar == '('))
                || (leftChar == '*' && (isLetter(rightChar) || rightChar == '('))
                || (leftChar == ')' && isLetter(rightChar))
            ) {
                RE.add('·');
            }
            RE.add(rightChar);
        }
        return RE;
    }

    public ArrayList<Character> toRPN(ArrayList<Character> RE) {
        ArrayList<Character> RPN = new ArrayList<>();
        Stack<Character> symStack = new Stack<>();
        for (char c : RE) {
            if (isLetter(c)) {
                RPN.add(c);
            } else {
                if (c == '(') {
                    symStack.push(c);
                } else if (c == ')') {
                    while (!(symStack.peek() == '(')) {
                        RPN.add(symStack.pop());
                    }
                    symStack.pop();
                } else {
                    while (!symStack.isEmpty() && getPriority(symStack.peek()) >= getPriority(c)) {
                        RPN.add(symStack.pop());
                    }
                    symStack.push(c);
                }
            }
        }
        while (!symStack.isEmpty()) {
            RPN.add(symStack.pop());
        }
        return RPN;
    }
}
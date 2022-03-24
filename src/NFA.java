import java.util.ArrayList;
import java.util.Stack;
import java.util.TreeSet;

public class NFA {
    // An NFA is a 5-tuple (Q, ∑, F, S, Z)
    // a finite set of states
    public int states;
    // a finite set of input symbols
    public ArrayList<Character> alphabet = new ArrayList<>();
    // a transition function
    public ArrayList<Integer>[][] func;
    // a set of start states
    public TreeSet<Integer> startStates = new TreeSet<>();
    // a set of accept states
    public TreeSet<Integer> acceptStates = new TreeSet<>();

    private boolean isLetter(char c) {
        return (c != '(' && c != ')' && c != '*' && c != '|' && c != '·');
    }

    @SuppressWarnings("unchecked")
    public NFA(ArrayList<Character> RPN) {
        // init alphabet, alphabet[0] = $ = empty string
        alphabet.add('$');
        for (char c : RPN) {
            if (isLetter(c) && !alphabet.contains(c)) {
                alphabet.add(c);
            }
        }
        // get NFA states count, init states
        for (char c : RPN) {
            if (isLetter(c) || c == '|' || c == '*') {
                states += 2;
            }
        }
        // init function
        func = new ArrayList[states][alphabet.size()];
        for (int i = 0; i < states; i++) {
            for (int j = 0; j < alphabet.size(); j++) {
                func[i][j] = new ArrayList<>(2);
            }
        }
        // generate NFA
        Stack<Integer> startStatesStack = new Stack<>();
        Stack<Integer> endStatesStack = new Stack<>();
        int newState = 0;
        for (char c : RPN) {
            if (isLetter(c)) {
                int start = newState++;
                int end = newState++;
                func[start][alphabet.indexOf(c)].add(end);
                startStatesStack.push(start);
                endStatesStack.push(end);
            } else if (c == '·') {
                int temp = endStatesStack.pop();
                int start = endStatesStack.pop();
                endStatesStack.push(temp);
                int end = startStatesStack.pop();
                func[start][0].add(end);
            } else if (c == '|') {
                int start = newState++;
                int end = newState++;
                func[start][0].add(startStatesStack.pop());
                func[start][0].add(startStatesStack.pop());
                func[endStatesStack.pop()][0].add(end);
                func[endStatesStack.pop()][0].add(end);
                startStatesStack.push(start);
                endStatesStack.push(end);
            } else if (c == '*') {
                int start = newState++;
                int end = newState++;
                func[start][0].add(end);
                func[endStatesStack.peek()][0].add(startStatesStack.peek());
                func[start][0].add(startStatesStack.pop());
                func[endStatesStack.pop()][0].add(end);
                startStatesStack.push(start);
                endStatesStack.push(end);
            }
        }
        startStates.add(startStatesStack.pop());
        acceptStates.add(endStatesStack.pop());
    }

    public void print() {
        System.out.print("NFA: (Q, ∑, F, S, Z)\nQ = [0 ~ " + (states - 1) + "]\n∑ = " + alphabet + "\nS = "
                + startStates + "\nZ = " + acceptStates + "\nF = │ State\\Input │ ");
        for (char c : alphabet) {
            System.out.printf("%8s │", c);
        }
        for (int from = 0; from < states; from++) {
            System.out.printf("\n    │%8d     │ ", from);
            for (int input = 0; input < alphabet.size(); input++) {
                System.out.printf("%8s │", func[from][input].isEmpty() ? "" : func[from][input]);
            }
        }
        System.out.println();
    }
}
import java.util.ArrayList;
import java.util.Stack;
import java.util.TreeSet;

public class NFA {
    // count of states, [0, states)
    public int states;
    // set of input symbols
    public ArrayList<Character> alphabet = new ArrayList<>();
    // transition function
    public ArrayList<Integer>[][] func;
    // set of start states
    public TreeSet<Integer> startStates = new TreeSet<>();
    // set of accept states
    public TreeSet<Integer> acceptStates = new TreeSet<>();

    private boolean isLetter(char c) {
        return (c != '(' && c != ')' && c != '*' && c != '|' && c != '·');
    }

    /**
     * Construct NFA from postfix regex
     * @param regex postfix regex
     */
    @SuppressWarnings("unchecked")
    public NFA(ArrayList<Character> regex) {
        // alphabet[0] = $ = empty string
        alphabet.add('$');
        for (char c : regex) {
            // init alphabet
            if (isLetter(c) && !alphabet.contains(c)) {
                alphabet.add(c);
            }
            // get NFA states count, init states
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

        // construct NFA
        Stack<Integer> S1 = new Stack<>();
        Stack<Integer> S2 = new Stack<>();
        int newState = 0;
        for (char c : regex) {
            if (isLetter(c)) {
                int start = newState++;
                int end = newState++;
                func[start][alphabet.indexOf(c)].add(end);
                S1.push(start);
                S2.push(end);
            } else if (c == '·') {
                int temp = S2.pop();
                int start = S2.pop();
                S2.push(temp);
                int end = S1.pop();
                func[start][0].add(end);
            } else if (c == '|') {
                int start = newState++;
                int end = newState++;
                func[start][0].add(S1.pop());
                func[start][0].add(S1.pop());
                func[S2.pop()][0].add(end);
                func[S2.pop()][0].add(end);
                S1.push(start);
                S2.push(end);
            } else if (c == '*') {
                int start = newState++;
                int end = newState++;
                func[start][0].add(end);
                func[S2.peek()][0].add(S1.peek());
                func[start][0].add(S1.pop());
                func[S2.pop()][0].add(end);
                S1.push(start);
                S2.push(end);
            }
        }
        startStates.add(S1.pop());
        acceptStates.add(S2.pop());
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("\nNFA: (Q, ∑, F, S, Z)\nQ = [0 ~ ");
        s.append(states - 1);
        s.append("]\n∑ = ");
        s.append(alphabet);
        s.append("\nS = ");
        s.append(startStates);
        s.append("\nZ = ");
        s.append(acceptStates);
        s.append("\nF = │ State\\Input │ ");
        for (char c : alphabet) {
            s.append(String.format("%8s │", c));
        }
        for (int from = 0; from < states; from++) {
            s.append(String.format("\n    │%8d     │ ", from));
            for (int input = 0; input < alphabet.size(); input++) {
                s.append(String.format("%8s │", func[from][input].isEmpty() ? "" : func[from][input]));
            }
        }
        return s.toString();
    }
}
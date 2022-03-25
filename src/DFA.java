import java.util.ArrayList;
import java.util.Stack;
import java.util.TreeSet;

public class DFA {

    private NFA NFA;

    private final ArrayList<TreeSet<Integer>> newOldStatesFunc = new ArrayList<>();

    // count of states, [0, states)
    public int states;
    // set of input symbols
    public ArrayList<Character> alphabet = new ArrayList<>();
    // transition function
    public Integer[][] func;
    // start state
    public int startState;
    // set of accept states
    public TreeSet<Integer> acceptStates = new TreeSet<>();

    /**
     * Construct DFA from NFA
     * @param nfa NFA
     */
    public DFA(NFA nfa) {
        NFA = nfa;

        // init alphabet, remove alphabet[0] = empty string
        alphabet.addAll(NFA.alphabet);
        alphabet.remove(0);

        // init function
        func = new Integer[NFA.states][alphabet.size()];

        // temp stack
        Stack<Integer> flagStatesStack = new Stack<>();
        flagStatesStack.add(0);

        // start state
        newOldStatesFunc.add(getClosure(NFA.startStates));
        startState = 0;

        // generate new function
        while (!flagStatesStack.isEmpty()) {
            int from = flagStatesStack.pop();
            for (int input = 0; input < alphabet.size(); input++) {
                TreeSet<Integer> J = new TreeSet<>();
                for (int oldFrom : newOldStatesFunc.get(from)) {
                    J.addAll(NFA.func[oldFrom][input + 1]);
                }
                if (!J.isEmpty()) {
                    TreeSet<Integer> U = getClosure(J);
                    if (!newOldStatesFunc.contains(U)) {
                        newOldStatesFunc.add(U);
                        flagStatesStack.add(newOldStatesFunc.indexOf(U));
                    }
                    func[from][input] = newOldStatesFunc.indexOf(U);
                }
            }
        }

        for (TreeSet<Integer> s : newOldStatesFunc) {
            // get NFA states count, init states
            states += 1;

            // generate new accept states
            if (s.containsAll(NFA.acceptStates)) {
                acceptStates.add(newOldStatesFunc.indexOf(s));
            }
        }

        // destroy NFA
        NFA = null;
    }

    private TreeSet<Integer> getClosure(TreeSet<Integer> someStates) {
        TreeSet<Integer> newStates = new TreeSet<>();
        newStates.addAll(someStates);
        Stack<Integer> flagStatesStack = new Stack<>();
        flagStatesStack.addAll(someStates);
        while (!flagStatesStack.isEmpty()) {
            if (NFA.func[flagStatesStack.peek()][0].isEmpty()) {
                flagStatesStack.pop();
            } else {
                int from = flagStatesStack.pop();
                ArrayList<Integer> to = NFA.func[from][0];
                newStates.addAll(to);
                flagStatesStack.addAll(to);
            }
        }
        return newStates;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("\nNew states old states function:\n");
        for (int i = 0; i < states; i++) {
            s.append(i);
            s.append(": ");
            s.append(newOldStatesFunc.get(i));
            s.append("\n");
        }
        s.append("\nDFA: (Q, ∑, F, S, Z)\nQ = [0 ~ ");
        s.append(states - 1);
        s.append("]\n∑ = ");
        s.append(alphabet);
        s.append("\nS = [");
        s.append(startState);
        s.append("]\nZ = ");
        s.append(acceptStates);
        s.append("\nF = │ State\\Input │ ");
        for (char c : alphabet) {
            s.append(String.format("%4s │", c));
        }
        for (int from = 0; from < states; from++) {
            s.append(String.format("\n\t│%8d     │ ", from));
            for (int input = 0; input < alphabet.size(); input++) {
                s.append(String.format("%4s │", func[from][input] == null ? "" : func[from][input]));
            }
        }
        return s.toString();
    }
}
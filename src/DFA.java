import java.util.ArrayList;
import java.util.Stack;
import java.util.TreeSet;

public class DFA {

    private final NFA NFA;

    ArrayList<TreeSet<Integer>> newOldStatesFunc = new ArrayList<>();

    // An NFA is a 5-tuple (Q, ∑, F, S, Z)
    // a finite set of states
    public int states;
    // a finite set of input symbols
    public ArrayList<Character> alphabet = new ArrayList<>();
    // a transition function
    public Integer[][] func;
    // a start state
    public int startState;
    // a set of accept states
    public TreeSet<Integer> acceptStates = new TreeSet<>();

    public DFA(NFA nfa) {
        NFA = nfa;
        // init alphabet, remove alphabet[0] = empty string
        alphabet.addAll(NFA.alphabet);
        alphabet.remove(0);
        // init function
        func = new Integer[NFA.states][alphabet.size()];

        Stack<Integer> flagStatesStack = new Stack<>();

        TreeSet<Integer> newStartState = getClosure(NFA.startStates);
        newOldStatesFunc.add(newStartState);
        flagStatesStack.add(0);

        while (!flagStatesStack.isEmpty()) {
            int aState = flagStatesStack.pop();
            for (int input = 0; input < alphabet.size(); input++) {
                TreeSet<Integer> J = new TreeSet<>();
                for (int from : newOldStatesFunc.get(aState)) {
                    J.addAll(NFA.func[from][input + 1]);
                }
                if (!J.isEmpty()) {
                    TreeSet<Integer> U = getClosure(J);
                    if (!newOldStatesFunc.contains(U)) {
                        newOldStatesFunc.add(U);
                        flagStatesStack.add(newOldStatesFunc.indexOf(U));
                    }
                    func[aState][input] = newOldStatesFunc.indexOf(U);
                }
            }
        }
        for (TreeSet<Integer> s : newOldStatesFunc) {
            states += 1;
            if (s.containsAll(nfa.acceptStates)) {
                acceptStates.add(newOldStatesFunc.indexOf(s));
            }
        }
    }

    public TreeSet<Integer> getClosure(TreeSet<Integer> someStates) {
        TreeSet<Integer> DFAnewStates = new TreeSet<>();
        Stack<Integer> flagStatesStack = new Stack<>();
        DFAnewStates.addAll(someStates);
        flagStatesStack.addAll(someStates);
        while (!flagStatesStack.isEmpty()) {
            if (NFA.func[flagStatesStack.peek()][0].isEmpty()) {
                flagStatesStack.pop();
            } else {
                int from = flagStatesStack.pop();
                DFAnewStates.addAll(NFA.func[from][0]);
                flagStatesStack.addAll(NFA.func[from][0]);
            }
        }
        return DFAnewStates;
    }

    public void print() {
        System.out.println("\nNew states old states function: ");
        for (int i = 0; i < states; i++) {
            System.out.println(i + ": " + newOldStatesFunc.get(i));
        }
        System.out.print("\nDFA: (Q, ∑, F, S, Z)\nQ = [0 ~ " + states + "]\n∑ = " + alphabet + "\nS = [" + startState
                + "]\nZ = " + acceptStates + "\nF = │ State\\Input │ ");
        for (char c : alphabet) {
            System.out.printf("%4s │", c);
        }
        for (int from = 0; from < states; from++) {
            System.out.printf("\n    │%8d     │ ", from);
            for (int input = 0; input < alphabet.size(); input++) {
                System.out.printf("%4s │", func[from][input] == null ? "" : func[from][input]);
            }
        }
    }
}
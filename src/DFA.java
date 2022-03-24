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
    public Integer func[][];
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

        TreeSet<Integer> flagStates = new TreeSet<>();

        TreeSet<Integer> newStartState = getClosure(NFA.startStates);

        newOldStatesFunc.add(newStartState);
        flagStates.add(0);

        while (!flagStates.isEmpty()) {
            int aState = flagStates.pollFirst();
            for (int input = 0; input < alphabet.size(); input++) {
                TreeSet<Integer> J = new TreeSet<>();
                for (int from : newOldStatesFunc.get(aState)) {
                    J.addAll(NFA.func[from][input + 1]);
                }
                if (!J.isEmpty()) {
                    TreeSet<Integer> U = getClosure(J);
                    if (!newOldStatesFunc.contains(U)) {
                        newOldStatesFunc.add(U);
                        flagStates.add(newOldStatesFunc.indexOf(U));
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
        TreeSet<Integer> DFAnewStateCount = new TreeSet<>();
        Stack<Integer> flagState = new Stack<>();
        for (int s : someStates) {
            DFAnewStateCount.add(s);
            flagState.push(s);
        }
        while (!flagState.isEmpty()) {
            if (NFA.func[flagState.peek()][0].isEmpty()) {
                flagState.pop();
            } else {
                for (int from : NFA.func[flagState.pop()][0]) {
                    DFAnewStateCount.add(from);
                    flagState.push(from);
                }
            }
        }
        return DFAnewStateCount;
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
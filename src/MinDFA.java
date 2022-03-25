import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

public class MinDFA {

    private DFA DFA;

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

    public MinDFA(DFA dfa) {
        DFA = dfa;

        // init alphabet
        alphabet.addAll(DFA.alphabet);

        // init function
        func = new Integer[DFA.states][alphabet.size()];

        // temp stack
        ArrayList<TreeSet<Integer>> W = new ArrayList<>();

        // accept states and other states
        TreeSet<Integer> otherStates = new TreeSet<>();
        for (int i = 0; i < DFA.states; i++) {
            if (!DFA.acceptStates.contains(i)) {
                otherStates.add(i);
            }
        }

        newOldStatesFunc.add(DFA.acceptStates);
        newOldStatesFunc.add(otherStates);
        W.add(DFA.acceptStates);
        W.add(otherStates);

        // partition the DFA states into groups
        while (!W.isEmpty()) {
            // get A from W and remove it
            TreeSet<Integer> A = W.get(W.size() - 1);
            W.remove(W.size() - 1);

            for (int input = 0; input < alphabet.size(); input++) {
                // generate X
                // X is the set of states for which a transition on input leads to a state in A
                TreeSet<Integer> X = new TreeSet<>();
                for (int from = 0; from < DFA.states; from++) {
                    if (DFA.func[from][input] != null && A.contains(DFA.func[from][input])) {
                        X.add(from);
                    }
                }

                // P = a copy of newOldStatesFunc
                ArrayList<TreeSet<Integer>> P = new ArrayList<>();

                for (TreeSet<Integer> Y : newOldStatesFunc) {
                    // S1 = X ∩ Y
                    TreeSet<Integer> S1 = new TreeSet<>(X);
                    S1.retainAll(Y);

                    // S2 = Y - X
                    TreeSet<Integer> S2 = new TreeSet<>(Y);
                    S2.removeAll(X);

                    if (!S1.isEmpty() && !S2.isEmpty()) {
                        P.add(S1);
                        P.add(S2);

                        if (W.contains(Y)) {
                            W.remove(Y);
                            W.add(S1);
                            W.add(S2);
                        } else {
                            if (S1.size() <= S2.size()) {
                                W.add(S1);
                            } else {
                                W.add(S2);
                            }
                        }
                    } else {
                        P.add(Y);
                    }
                }

                newOldStatesFunc.clear();
                newOldStatesFunc.addAll(P);
            }
        }

        // get NFA states count, init states
        states = newOldStatesFunc.size();

        // start state
        startState = 0;

        // init new function
        func = new Integer[newOldStatesFunc.size()][alphabet.size()];

        // generate new function
        newOldStatesFunc.sort(Comparator.comparingInt(TreeSet::first));
        for(int from = 0; from < states; from++) {
            for(int input = 0; input < alphabet.size(); input++) {
                int oldFrom = newOldStatesFunc.get(from).first();
                Integer oldTo = DFA.func[oldFrom][input];
                if(oldTo != null) {
                    for (TreeSet<Integer> to : newOldStatesFunc) {
                        if (to.contains(oldTo)) {
                            func[from][input] = newOldStatesFunc.indexOf(to);
                            break;
                        }
                    }
                }
            }
        }

        // generate new accept states
        for(TreeSet<Integer> s : newOldStatesFunc) {
            for(Integer a : DFA.acceptStates) {
                if(s.contains(a)) {
                    acceptStates.add(newOldStatesFunc.indexOf(s));
                    break;
                }
            }
        }

        // destroy DFA
        DFA = null;
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

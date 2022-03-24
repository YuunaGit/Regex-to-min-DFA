public class REtoDFA {
    public static void main(String[] args) {
        String regex = "(ab)*(a*|b*)(ba)*";

        var RE = new RE();

        System.out.println("Complete Regex");
        var completeRegex = RE.addJoinSym(regex.toCharArray());
        completeRegex.forEach(System.out::print);
        System.out.println();

        System.out.println("RPN Regex");
        var reversePolishNotationRegex = RE.toRPN(completeRegex);
        reversePolishNotationRegex.forEach(System.out::print);
        System.out.println();

        var NFA = new NFA(reversePolishNotationRegex);
        NFA.print();

        var DFA = new DFA(NFA);
        DFA.print();
    }
}
public class REtoDFA {
    public static void main(String[] args) {
        char[] regex = "(ab)*(a*|b*)(ba)*".toCharArray();

        var RE = new RE();

        System.out.println("Complete Regex:");
        var completeRegex = RE.addJoinSym(regex);
        completeRegex.forEach(System.out::print);
        System.out.println();

        System.out.println("Postfix Regex:");
        var postfixRegex = RE.toPostfix(completeRegex);
        postfixRegex.forEach(System.out::print);
        System.out.println();

        var NFA = new NFA(postfixRegex);
        NFA.print();

        var DFA = new DFA(NFA);
        DFA.print();
    }
}
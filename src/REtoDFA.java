import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class REtoDFA {
    public static void main(String[] args) throws FileNotFoundException {
        char[] regex = "(a|b)*abb(a|b)*ab".toCharArray();

        var RE = new RE();

        // 1
        var completeRegex = RE.addJoinSym(regex);
        // 2
        var postfixRegex = RE.toPostfix(completeRegex);
        // 3
        var NFA = new NFA(postfixRegex);
        // 4
        var DFA = new DFA(NFA);
        // 5
        var MinDFA = new MinDFA(DFA);

        File f = new File("Regex-to-min-DFA\\src\\result.txt");

        try (PrintWriter pw = new PrintWriter(f)) {
            pw.println("Complete Regex:");
            completeRegex.forEach(pw::print);
            pw.println("\n\nPostfix Regex:");
            postfixRegex.forEach(pw::print);
            pw.println();
            pw.println(NFA);
            pw.println(DFA);
            pw.println(MinDFA);
        }
    }
}
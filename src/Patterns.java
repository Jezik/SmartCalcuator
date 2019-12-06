import java.util.regex.Pattern;

/** Patterns storage
 * Contains Patterns for regexps, which are used to modify input strings
 * according to a proper operation
 */
class Patterns {
    final static Pattern removeSpaces = Pattern.compile("\\s+");
    final static Pattern checkIdentifier = Pattern.compile("[^a-zA-Z]+");
    final static Pattern onlyDigits = Pattern.compile("[0-9]+");
    final static Pattern onlyLetters = Pattern.compile("[a-zA-Z]+");
    final static Pattern minusToMinusPtn = Pattern.compile("\\s-{11}\\s|\\s-{9}\\s|\\s-{7}\\s|\\s-{5}\\s|\\s-{3}\\s"); //The odd number of minuses gives a minus
    final static Pattern minusToPlusPtn = Pattern.compile("\\s-{10}\\s|\\s-{8}\\s|\\s-{6}\\s|\\s-{4}\\s|\\s-{2}\\s"); //The even number of minuses gives a plus
    final static Pattern plusToPlusPtn = Pattern.compile("\\+{2,}"); //Converts '++++++++' to a single '+'
    final static Pattern tooMuchAsterics = Pattern.compile("\\*{2,}");
    final static Pattern tooMuchSlashes = Pattern.compile("\\/{2,}");
}
package plc.homework;

import com.sun.org.apache.xpath.internal.Arg;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Contains JUnit tests for {@link Regex}. Test structure for steps 1 & 2 are
 * provided, you must create this yourself for step 3.
 *
 * To run tests, either click the run icon on the left margin, which can be used
 * to run all tests or only a specific test. You should make sure your tests are
 * run through IntelliJ (File > Settings > Build, Execution, Deployment > Build
 * Tools > Gradle > Run tests using <em>IntelliJ IDEA</em>). This ensures the
 * name and inputs for the tests are displayed correctly in the run window.
 */
public class RegexTests {

    /**
     * This is a parameterized test for the {@link Regex#EMAIL} regex. The
     * {@link ParameterizedTest} annotation defines this method as a
     * parameterized test, and {@link MethodSource} tells JUnit to look for the
     * static method {@link #testEmailRegex()}.
     *
     * For personal preference, I include a test name as the first parameter
     * which describes what that test should be testing - this is visible in
     * IntelliJ when running the tests (see above note if not working).
     */
    @ParameterizedTest
    @MethodSource
    public void testEmailRegex(String test, String input, boolean success) {
        test(input, Regex.EMAIL, success);
    }

    /**
     * This is the factory method providing test cases for the parameterized
     * test above - note that it is static, takes no arguments, and has the same
     * name as the test. The {@link Arguments} object contains the arguments for
     * each test to be passed to the function above.
     */
    public static Stream<Arguments> testEmailRegex() {
        return Stream.of(
                Arguments.of("Alphanumeric", "thelegend27@gmail.com", true),
                Arguments.of("UF Domain", "otherdomain@ufl.edu", true),
                Arguments.of("Missing Domain Dot", "missingdot@gmailcom", false),
                Arguments.of("Symbols", "symbols#$%@gmail.com", false),
                //5 correct
                Arguments.of("No address", "augustocohn@.com", true),
                Arguments.of("Gmail", "guscohn@gmail.com", true),
                Arguments.of("All caps", "HELLO@WORLD.com", true),
                Arguments.of("Crazy", "xX_CODgamer_Xx@yahoo.net", true),
                Arguments.of("Reverse", "com@gmail.me", true),
                //5 incorrect
                Arguments.of("Missing @", "testingatgmail.com", false),
                Arguments.of("Too many chars in domain", "testingdomain@hotmail.comm", false),
                Arguments.of("Invalid @ address", "testingaddress@aol_.net", false),
                Arguments.of("Capital Domain", "poopoo@toilet.COM", false),
                Arguments.of("Too many @", "hello@@world.com", false)
        );
    }

    @ParameterizedTest
    @MethodSource
    public void testEvenStringsRegex(String test, String input, boolean success) {
        test(input, Regex.EVEN_STRINGS, success);
    }

    public static Stream<Arguments> testEvenStringsRegex() {
        return Stream.of(
                //what has ten letters and starts with gas?
                Arguments.of("10 Characters", "automobile", true),
                Arguments.of("14 Characters", "i<3pancakes10!", true),
                Arguments.of("6 Characters", "6chars", false),
                Arguments.of("13 Characters", "i<3pancakes9!", false),

                //5 correct
                Arguments.of("20 Characters", "qwertyuiopasdfghjklf", true),
                Arguments.of("Special Characters", "!@##$%^&*()_+=-%", true),
                Arguments.of("12 Characters", "123456789123", true),
                Arguments.of("16 Characters", "alskdjfhgpqowier", true),
                Arguments.of("18 Characters", "zxcvbnmlkjhgfdsaqw", true),
                //5 incorrect
                Arguments.of("9 Characters", "zxcvbnmas", false),
                Arguments.of("21 Characters", "asdfghjklpoiuytrewqzx", false),
                Arguments.of("8 Characters", "poiuytre", false),
                Arguments.of("22 Characters", "qqqqqqqqqqqqqqqqqqqqqq", false),
                Arguments.of("15 Characters", "azsxdcfvgbhnjml", false)

        );
    }

    @ParameterizedTest
    @MethodSource
    public void testIntegerListRegex(String test, String input, boolean success) {
        test(input, Regex.INTEGER_LIST, success);
    }

    public static Stream<Arguments> testIntegerListRegex() {
        return Stream.of(
                Arguments.of("Single Element", "[1]", true),
                Arguments.of("Multiple Elements", "[1,2,3]", true),
                Arguments.of("Missing Brackets", "1,2,3", false),
                Arguments.of("Missing Commas", "[1 2 3]", false),

                //5 correct
                Arguments.of("One whitespace", "[1,2, 3]", true),
                Arguments.of("All Whitespace","[1, 2, 3, 4]",true),
                Arguments.of("Empty","[]",true),
                Arguments.of("Reverse order","[3, 2, 1]",true),
                Arguments.of("Same digit","[1,1,1,1]",true),
                //5 wrong
                Arguments.of("Too many brackets","[[1]]",false),
                Arguments.of("Open brackets","[1",false),
                Arguments.of("Closed bracket","1]",false),
                Arguments.of("Missing a comma","[1, 2, 3 4]",false),
                Arguments.of("Letters/Symbols","[A, +, 3]",false)
        );
    }

    @ParameterizedTest
    @MethodSource
    public void testNumberRegex(String test, String input, boolean success) {
        //throw new UnsupportedOperationException(); //TODO
        test(input, Regex.NUMBER, success);
    }

    public static Stream<Arguments> testNumberRegex() {
        //throw new UnsupportedOperationException(); //TODO
        return Stream.of(
                Arguments.of("Single Digit", "1", true),
                Arguments.of("Decimal", "123.456", true),
                Arguments.of("Negative", "-1.0", true),
                Arguments.of("Trailing Decimal", "1.", false),
                Arguments.of("Leading Decimal", ".5", false),

                //5 passing
                Arguments.of("Leading 0", "05", true),
                Arguments.of("Trailing 0", "50.00", true),
                Arguments.of("Large number","9999999999999",true),
                Arguments.of("Large Decimal","999.999999999999",true),
                Arguments.of("Positive","+5",true),
                //5 failing
                Arguments.of("Alphabetical", "A10", false),
                Arguments.of("Symbols", "_10", false),
                Arguments.of("Just decimal",".",false),
                Arguments.of("Multiple decimals","5.1.2",false),
                Arguments.of("Positive and Negative","-+55",false)
        );
    }

    @ParameterizedTest
    @MethodSource
    public void testStringRegex(String test, String input, boolean success) {
        //throw new UnsupportedOperationException(); //TODO
        test(input, Regex.STRING, success);
    }

    public static Stream<Arguments> testStringRegex() {
        //throw new UnsupportedOperationException(); //TODO
        return Stream.of(
                //5 correct
                Arguments.of("Empty","\"\"",true),
                Arguments.of("Hello world","\"Hello, World!\"",true),
                Arguments.of("Escape Sequence","\"1\\t2\"",true),
                Arguments.of("Many escape sequence", "\"a\\t\\n\\f\"", true),
                Arguments.of("Numbers and symbols", "\"[]}{124392\"", true),
                //5 incorrect
                Arguments.of("Missing close","\"unterminated",false),
                Arguments.of("Invalid Escape","\"invalid\\escape\"",false),
                Arguments.of("Missing open", "undetermined\"", false),
                Arguments.of("Too many opening", "\"\"hello there\"", false),
                Arguments.of("Too many closing", "\"hello again\"\"", false)
        );
    }

    /**
     * Asserts that the input matches the given pattern. This method doesn't do
     * much now, but you will see this concept in future assignments.
     */
    private static void test(String input, Pattern pattern, boolean success) {
        Assertions.assertEquals(success, pattern.matcher(input).matches());
    }

}

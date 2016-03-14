import com.sine_x.regexp.Pattern;

public class TestRunner {

    public static void main(String[] args) {
        Pattern pattern = Pattern.compile("a+");
        System.out.println(pattern.match("aaaa"));
    }
}

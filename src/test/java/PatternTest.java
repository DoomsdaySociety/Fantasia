import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternTest {
    public static void main(String[] args) {
        String text = "§7[§b§l‣§是§7] §f<§7[§6开荒者§7]§bLittleCatX后缀§f> §fa.a";
        Pattern pattern = Pattern.compile("§7\\[(.*)§7\\] §f<(.*)§.([A-Za-z0-9_]+)(.*)?§f> §?f?(.*)");
        Matcher m = pattern.matcher(text);
        if (!m.find()) {
            System.out.println("未找到结果");
            return;
        }
        for (int i = 0; i <= m.groupCount(); i++) {
            System.out.println(m.group(i));
        }
    }
}

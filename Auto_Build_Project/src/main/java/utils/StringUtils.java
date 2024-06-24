package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static boolean containsChineseCharacters(String input) {
        String regex = ".*[\\u4e00-\\u9fa5]+.*"; // 正则表达式匹配包含中文字符的字符串
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

}

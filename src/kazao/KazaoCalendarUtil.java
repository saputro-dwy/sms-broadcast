package kazao;

public final class KazaoCalendarUtil {    
    public static String replace(String data, String expresion, String replace) {
        StringBuffer res = new StringBuffer(data);
        int x = 0;
        while (true) {
            int y = data.indexOf(expresion, x);
            if (y != -1) {
                res = new StringBuffer("");
                res.append(data.substring(0, y));
                res.append(replace);
                res.append(data.substring(y+expresion.length()));
                x = y+replace.length();
                data = res.toString();
            } else {
                break;
            }
        } 
        return res.toString();
    }
    
    public static String lpad(String data, int len, String add) {
        String tmp = "";
        for (int i=0; i < len-data.length(); i++) {
            tmp += add;
        }
        return tmp+data;
    }
    
    public static boolean inArray(char[] array, char value) {
        boolean result = false;
        for (int i=0; i < array.length; i++) {
            if (array[i] == value) {
                result = true;
                break;
            }
        }
        return result;
    }
    public static boolean inArray(String[] array, String value) {
        boolean result = false;
        for (int i=0; i < array.length; i++) {
            if (array[i].equals(value)) {
                result = true;
                break;
            }
        }
        return result;
    }
}

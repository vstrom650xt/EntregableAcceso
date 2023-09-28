import controller.Logic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        Logic a = new Logic();
        a.readFile();

//
//        String linea = "1,Barbie,\"4,337\",\"$594,254,460\",2023-07-21 00:00:00,Warner Bros.";
//        String regex = "([^,\"]+)|\"([^\"]*)\"";
//
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(linea);
//
//        while (matcher.find()) {
//            // Si el grupo 1 está vacío, significa que es un campo con comillas dobles
//            if (matcher.group(1) == null) {
//                System.out.println("Campo con comillas dobles: " + matcher.group(2));
//            } else {
//                System.out.println("Campo sin comillas dobles: " + matcher.group(1));
//            }
//        }
    }
}
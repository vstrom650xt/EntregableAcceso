package controller;

import model.LineObj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Logic {
    List<LineObj> outPutData;

    public void readFile() {
        Path path = Paths.get("peliculas.csv");
        String regex = "([^,\"]+)|\"([^\"]*)\"";

        Function<String, LineObj> convertidor = line -> {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(line);
            List<String> otra = new ArrayList<>();

            while (matcher.find()) {
                // Si el grupo 1 está vacío, significa que es un campo con comillas dobles
                if (matcher.group(1) == null) {
                    otra.add(matcher.group(2));
                } else {
                    otra.add(matcher.group(1));
                }
            }

            System.out.println(otra);
            // Aquí debes construir tu objeto LineObj con los datos de 'otra'
            // ...
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try {
                // Convierte la cadena a un objeto Date
                Date date = dateFormat.parse(otra.get(4));

                // Imprime el objeto Date
                System.out.println("Fecha en formato Date: " + date);
            } catch (ParseException e) {
                e.printStackTrace();
                System.err.println("No se pudo parsear la fecha: " + e.getMessage());
            }

            try {
                return new LineObj(Integer.parseInt(otra.get(0)), otra.get(1),Float.parseFloat(otra.get(2)),Float.parseFloat(otra.get(3)), dateFormat.parse(otra.get(4)) ,otra.get(5) );  // Debes devolver el objeto LineObj construido
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        };

        try {
            if (fileExist(path.toFile())) {
                outPutData = Files.lines(path).skip(1).map(convertidor).toList();
                System.out.println(outPutData);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Resto del código

    private static boolean fileExist(File file) {
        boolean exist;
        exist = Files.exists(Paths.get(file.toURI()));
        return exist;
    }


}




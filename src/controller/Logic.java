package controller;

import model.LineObj;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.*;
import java.util.stream.Collectors;
//IMPORTAR SIEMPRE EL DATE DE SQL

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
                if (matcher.group(1) == null) {
                    otra.add(matcher.group(2));
                } else {
                    otra.add(matcher.group(1));
                }
            }
          //  System.out.println(otra);
//            System.out.println(otra.get(0));
//            System.out.println(otra.get(1));
//            System.out.println(otra.get(2).replace(",","."));
//            System.out.println(otra.get(3).replace(",","."));
//            System.out.println(otra.get(4));
//            System.out.println(otra.get(5));
//            System.out.println("------------------------------");
//             System.out.println(otra);

            //  return (LineObj) outPutData;
                return new LineObj(Integer.parseInt(otra.get(0)), 
                        otra.get(1),
                        Integer.parseInt(otra.get(2).replace(",","").replace("'-","-1")),
                        Integer.parseInt(otra.get(3).replace(",","").replace("$","")),
                        Date.valueOf(otra.get(4).replace(" 00:00:00","")) ,
                        otra.get(5));
        };

        try {
            if (fileExist(path.toFile())) {
                outPutData = Files.lines(path).skip(1).map(convertidor).toList();
//               System.out.println(outPutData);
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




//
// agrupar por mes las que mas recaudacion tienen
//        public Date getByMonth(){
//
//       outPutData.stream().filter().collect(groupingBY(LineObj::));
//
//
//        return  null;
//    }


//) Indica cuantas películas se estrenaron en cada mes  YA VA

    public void FilmsPerMonth(){
        System.out.println(  outPutData.stream().collect(Collectors.groupingBy(p->p.getReleaseDate().getMonth()+1,Collectors.counting())));
    }
// Indica cual es la película que tuvo la mayor recaudación habiéndose estrenado
//en el menor número de cines.

    public void lessCinemaMoreGross() {
        // Filtrar las películas que se estrenaron en menos de 50 cines
        List<LineObj> filteredMovies = outPutData.stream()
                .filter(p -> p.getTheaters() < 50)
                .toList();

        // Encontrar la película con la mayor recaudación entre las filtradas
        Optional<LineObj> maxGrossMovie = filteredMovies.stream()
                .max(Comparator.comparing(LineObj::getTotalGross));

        if (maxGrossMovie.isPresent()) {
            System.out.println("La película con la mayor recaudación en menos de 50 cines es: " + maxGrossMovie.get().getTitle() + " con  " + maxGrossMovie.get().getTotalGross());
        } else {
            System.out.println("No se encontraron películas que cumplan con el criterio.");
        }
    }


}




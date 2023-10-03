package controller;

import model.LineObj;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.*;
import java.util.stream.Collectors;
//IMPORTAR SIEMPRE EL DATE DE SQL

public class Logic {
    static List<LineObj> outPutData;

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
                    Integer.parseInt(otra.get(2).replace(",", "").replace("'-", "-1")),
                    Integer.parseInt(otra.get(3).replace(",", "").replace("$", "")),
                    Date.valueOf(otra.get(4).replace(" 00:00:00", "")),
                    otra.get(5));
        };

        try {
            if (fileExist(path.toFile())) {
                outPutData = Files.lines(path).skip(1).map(convertidor).toList();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static boolean fileExist(File file) {
        boolean exist;
        exist = Files.exists(Paths.get(file.toURI()));
        return exist;
    }


    //
// agrupar por mes las que mas recaudacion tienen
    public static void groupMoviesByMonthWithHighestGross() {

        Map<String, List<LineObj>> moviesPerMonth = outPutData.stream()
                .collect(Collectors.groupingBy(movie -> new SimpleDateFormat("MM").format(movie.getReleaseDate())));


        moviesPerMonth.forEach((month, moviesList) -> {
            System.out.println("Month: " + month);
            System.out.println("Movies with highest gross:");
            moviesList.forEach(movie -> System.out.println(movie.getTitle() + " - Total Gross: " + movie.getTotalGross()));
            System.out.println();
        });
    }

//) Indica cuantas películas se estrenaron en cada mes  YA VA

    public void FilmsPerMonth() {
        System.out.println(outPutData.stream().collect(Collectors.groupingBy(p -> p.getReleaseDate().getMonth() + 1, Collectors.counting())));
    }
// Indica cual es la película que tuvo la mayor recaudación habiéndose estrenado
//en el menor número de cines.

    public static void findMovieWithHighestGrossAndlessTheater() {
        outPutData.stream()
                .min(Comparator.comparingInt(LineObj::getTheaters))  // Find the movie with the fewest theaters
                .filter(movie -> movie.getTotalGross() == outPutData.stream()
                        .mapToInt(LineObj::getTotalGross)
                        .max()
                        .orElse(0));  // Filter by the highest total gross

        outPutData.forEach(movie -> {
            System.out.println("Title: " + movie.getTitle() + " Total Gross: " + movie.getTotalGross() +  "Number of Theaters: " +  movie.getTheaters());

        });

    }


//Indica cuantas películas de la lista pertenecen a cada distribuidor
    public static void countMoviesPerDistributor() {
        Map<String, Long> moviesPerDistributor = outPutData.stream()
                .collect(Collectors.groupingBy(LineObj::getDistributor, Collectors.counting()));

        System.out.println("Number of movies per distributor:");
        moviesPerDistributor.forEach((distributor, count) ->
                System.out.println(distributor + ": " + count));
    }






//    Indica cual de las distribuidoras ha tenido menor recaudación

    public static void findDistributorWithLowestTotalGross() {
        Map<String, Integer> totalGrossPerDistributor = new HashMap<>();

        // Calculate the total gross revenue for each distributor
        outPutData.forEach(movie -> {
            String distributor = movie.getDistributor();
            int totalGross = movie.getTotalGross();

            totalGrossPerDistributor.put(distributor, totalGrossPerDistributor.getOrDefault(distributor, 0) + totalGross);
        });

        // Find the distributor with the lowest total gross revenue
        Map.Entry<String, Integer> distributorWithLowestTotalGross = totalGrossPerDistributor.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .orElse(null);

        if (distributorWithLowestTotalGross != null) {
            System.out.println("Distributor with the lowest total gross revenue: " + distributorWithLowestTotalGross.getKey());
            System.out.println("Total Gross Revenue: " + distributorWithLowestTotalGross.getValue());
        } else {
            System.out.println("No movies found.");
        }
    }

}




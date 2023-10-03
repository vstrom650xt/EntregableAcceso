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
// a) agrupar por mes las que mas recaudacion tienen
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

// b) Indica cuantas películas se estrenaron en cada mes  YA VA

    public void FilmsPerMonth() {
        System.out.println(outPutData.stream().collect(Collectors.groupingBy(p -> p.getReleaseDate().getMonth() + 1, Collectors.counting())));
    }
// c)Indica cual es la película que tuvo la mayor recaudación habiéndose estrenado
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


//d)Indica cuantas películas de la lista pertenecen a cada distribuidor
    public static void countMoviesPerDistributor() {
        Map<String, Long> moviesPerDistributor = outPutData.stream()
                .collect(Collectors.groupingBy(LineObj::getDistributor, Collectors.counting()));

        System.out.println("Number of movies per distributor:");
        moviesPerDistributor.forEach((distributor, count) ->
                System.out.println(distributor + ": " + count));
    }






//e)    Indica cual de las distribuidoras ha tenido menor recaudación

    public static void findDistributorWithLowestTotalGross() {
        Map<String, Integer> totalGrossPerDistributor = new HashMap<>();

        // Calculate the total gross revenue for each distributor
        outPutData.forEach(movie -> {
            String distributor = movie.getDistributor();
            int totalGross = movie.getTotalGross();

            totalGrossPerDistributor.put(distributor, totalGrossPerDistributor.getOrDefault(distributor, 0) + totalGross);
        });

        Map.Entry<String, Integer> distributorWithLowestTotalGross = totalGrossPerDistributor.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .orElse(null);

        if (distributorWithLowestTotalGross != null) {
            System.out.println("Distributor with the lowest total gross revenue: " + distributorWithLowestTotalGross.getKey());
            System.out.println("Total Gross : " + distributorWithLowestTotalGross.getValue());
        } else {
            System.out.println("No movies found.");
        }
    }

//f)Indica de cada distribuidora la película que ha recaudado más y la que ha
  //  recaudado menos

    public void findMinMaxGrossPerDistributor() {
        // Agrupar las películas por distribuidora
        Map<String, List<LineObj>> moviesByDistributor = outPutData.stream()
                .collect(Collectors.groupingBy(LineObj::getDistributor));

        // Iterar sobre cada distribuidora y encontrar la película con mayor y menor recaudación
        for (Map.Entry<String, List<LineObj>> entry : moviesByDistributor.entrySet()) {
            String distributor = entry.getKey();
            List<LineObj> movies = entry.getValue();

            Optional<LineObj> maxGrossMovie = movies.stream()
                    .max(Comparator.comparing(LineObj::getTotalGross));

            Optional<LineObj> minGrossMovie = movies.stream()
                    .min(Comparator.comparing(LineObj::getTotalGross));

            // Imprimir la película que ha recaudado más y menos para esta distribuidora
            maxGrossMovie.ifPresent(movie -> System.out.println("Para la distribuidora " + distributor +
                    ", la película que ha recaudado más es: " + movie.getTitle() +
                    ", con una recaudación de " + movie.getTotalGross()));

            minGrossMovie.ifPresent(movie -> System.out.println("Para la distribuidora " + distributor +
                    ", la película que ha recaudado menos es: " + movie.getTitle() +
                    ", con una recaudación de " + movie.getTotalGross()));
        }
    }


    //g)Indica cual ha sido el peor mes de estreno para cada distribuidora

    public void findWorstReleaseMonthPerDistributor() {
        Map<String, String> worstReleaseMonths = new HashMap<>();

        for (LineObj movie : outPutData) {
            String distributor = movie.getDistributor();
            String currentWorstMonth = worstReleaseMonths.get(distributor);

            // Si es la primera película de la distribuidora o el mes actual tiene menor recaudación
            if (currentWorstMonth == null || movie.getTotalGross() < outPutData.stream()
                    .filter(m -> m.getDistributor().equals(distributor))
                    .min(Comparator.comparing(LineObj::getTotalGross))
                    .orElseThrow(NoSuchElementException::new)
                    .getTotalGross()) {
                worstReleaseMonths.put(distributor, new SimpleDateFormat("MMMMM").format(movie.getReleaseDate()));
            }
        }

        // Imprimir el peor mes de estreno para cada distribuidora
        for (Map.Entry<String, String> entry : worstReleaseMonths.entrySet()) {
            System.out.println("Para la distribuidora " + entry.getKey() +
                    ", el peor mes de estreno fue: " + entry.getValue());
        }
    }

}


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
import java.util.stream.Collectors;

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

    public void run() {
        //EL APARTADO C NO LO TENGO
        readFile();
        groupMoviesByMonthWithHighestGross(); //funciona
        FilmsPerMonth(); //funciona
        countMoviesPerDistributor(); //funciona
        findDistributorWithLowestTotalGross();
        findMinMaxGrossPerDistributor(); //funciona
        findWorstReleaseMonthPerDistributor(); // mas o menos ... por que '- no es una distri


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
                .collect(Collectors.groupingBy(p -> new SimpleDateFormat("MM").format(p.getReleaseDate())));


        moviesPerMonth.forEach((month, moviesList) -> {
            System.out.println("Month: " + month);
            System.out.println("Movies with highest gross:");
            moviesList.forEach(p -> System.out.println(p.getTitle() + " - Total Gross: " + p.getTotalGross()));
            System.out.println();
        });
    }

// b) Indica cuantas películas se estrenaron en cada mes  YA VA

    public void FilmsPerMonth() {
        Map<Integer, Long> moviesPerMonth = outPutData.stream()
                .collect(Collectors.groupingBy(p -> p.getReleaseDate().getMonth() + 1, Collectors.counting()));

        moviesPerMonth.forEach((month, count) -> {
            System.out.println("Month " + month + " -> " + count + " movies");
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

        outPutData.forEach(p -> {
            String distributor = p.getDistributor();
            int totalGross = p.getTotalGross();

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
        Map<String, List<LineObj>> moviesByDistributor = outPutData.stream()
                .collect(Collectors.groupingBy(LineObj::getDistributor));

        for (Map.Entry<String, List<LineObj>> entry : moviesByDistributor.entrySet()) {
            String distributor = entry.getKey();
            List<LineObj> movies = entry.getValue();

            Optional<LineObj> maxGrossMovie = movies.stream()
                    .max(Comparator.comparing(LineObj::getTotalGross));

            Optional<LineObj> minGrossMovie = movies.stream()
                    .min(Comparator.comparing(LineObj::getTotalGross));

            maxGrossMovie.ifPresent(p -> System.out.println("For the Distributor " + distributor +
                    ", the film with the best gross is " + p.getTitle() +
                    ", with this gross " + p.getTotalGross()));

            minGrossMovie.ifPresent(p -> System.out.println("For the Distributor " + distributor +
                    ", the film with the worst gross is : " + p.getTitle() +
                    ", with this gross  " + p.getTotalGross()));
        }
    }




    //g)Indica cual ha sido el peor mes de estreno para cada distribuidora
    public void findWorstReleaseMonthPerDistributor() {
        Map<String, String> worstReleaseMonths = new HashMap<>();

        for (LineObj movie : outPutData) {
            String distributor = movie.getDistributor();
            String currentWorstMonth = worstReleaseMonths.get(distributor);

            if (currentWorstMonth == null || movie.getTotalGross() < outPutData.stream()
                    .filter(m -> m.getDistributor().equals(distributor))
                    .min(Comparator.comparing(LineObj::getTotalGross))
                    .orElseThrow(NoSuchElementException::new)
                    .getTotalGross()) {
                worstReleaseMonths.put(distributor, new SimpleDateFormat("MMMMM").format(movie.getReleaseDate()));
            }
        }

        for (Map.Entry<String, String> entry : worstReleaseMonths.entrySet()) {
            System.out.println("For the Distributor " + entry.getKey() +
                    ", the worst release is : " + entry.getValue());
        }
    }

}


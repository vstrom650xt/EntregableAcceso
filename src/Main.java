import controller.Logic;
import model.LineObj;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {

        Map<String, List<LineObj>> groupMoviesByMonthWithHighestGross;
        Logic a = new Logic();
        a.readFile();
        //     a.FilmsPerMonth(); //funciona
        //a.groupMoviesByMonthWithHighestGross();
       //  a.findMovieWithHighestGrossAndlessTheater();
      //  a.countMoviesPerDistributor();
        a.findDistributorWithLowestTotalGross();


    }
}
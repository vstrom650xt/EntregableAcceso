package model;

import java.util.Date;

public class LineObj {
    private int rank;
    private String title;
    private double Theaters;
    private double TotalGross;
    private Date ReleaseDate;
    private String Distributor;


    public LineObj(int rank, String title, double theaters, double totalGross, Date releaseDate, String distributor) {
        this.rank = rank;
        this.title = title;
        Theaters = theaters;
        TotalGross = totalGross;
        ReleaseDate = releaseDate;
        Distributor = distributor;
    }
}

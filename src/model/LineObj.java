package model;

import java.util.Date;

public class LineObj {
    private int rank;
    private String title;
    private int Theaters;
    private int TotalGross;
    private Date ReleaseDate;
    private String Distributor;


    public LineObj(int rank, String title, int theaters, int totalGross, Date releaseDate, String distributor) {
        this.rank = rank;
        this.title = title;
        this.Theaters = theaters;
        this.TotalGross = totalGross;
        this.ReleaseDate = releaseDate;
        this.Distributor = distributor;
    }

    public int getRank() {
        return rank;
    }

    public String getTitle() {
        return title;
    }

    public int getTheaters() {
        return Theaters;
    }

    public int getTotalGross() {
        return TotalGross;
    }

    public Date getReleaseDate() {
        return ReleaseDate;
    }

    public String getDistributor() {
        return Distributor;
    }

    @Override
    public String toString() {
        return "LineObj{" +
                "rank=" + rank +
                ", title='" + title + '\'' +
                ", Theaters=" + Theaters +
                ", TotalGross=" + TotalGross +
                ", ReleaseDate=" + ReleaseDate +
                ", Distributor='" + Distributor + '\'' +
                '}';
    }
}

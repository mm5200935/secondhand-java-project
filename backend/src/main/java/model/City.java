package model;
import java.util.ArrayList;
import java.util.List;

public class City {

    private int id;
    private String name;
    private String province;
    private List<Advertisement> advertisements;

    public City() {
        advertisements = new ArrayList<>();
    }

    public City(int id, String name, String province) {
        this();
        this.id = id;
        this.name = name;
        this.province = province;
    }


}
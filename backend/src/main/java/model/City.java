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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }


}
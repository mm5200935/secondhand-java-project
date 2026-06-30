package model;
import java.util.ArrayList;
import java.util.List;

public class Category {

    private int id;
    private String name;
    private String description;
    private List<Advertisement> advertisements;

    public Category() {
        advertisements = new ArrayList<>();
    }

    public Category(int id, String name, String description) {
        this();
        this.id = id;
        this.name = name;
        this.description = description;
    }

}
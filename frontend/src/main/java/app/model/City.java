package app.model;

import java.io.Serializable;

public class City implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String province;

    public City() {}

    public City(Long id, String name, String province) {
        this.id = id;
        this.name = name;
        this.province = province;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
}
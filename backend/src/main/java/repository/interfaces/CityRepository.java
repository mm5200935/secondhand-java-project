package app.repository.interfaces;

import app.model.City;

import java.util.List;

public interface CityRepository {

    City save(City city);

    City update(City city);

    void delete(int cityId);

    City findById(int cityId);

    List<City> findAll();

    City findByName(String name);

    boolean existsByName(String name);

}
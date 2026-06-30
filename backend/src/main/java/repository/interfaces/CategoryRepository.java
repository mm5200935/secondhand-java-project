package repository.interfaces;

import model.Category;

import java.util.List;

public interface CategoryRepository {

    Category save(Category category);

    Category update(Category category);

    void delete(int categoryId);

    Category findById(int categoryId);

    List<Category> findAll();

    Category findByName(String name);

    boolean existsByName(String name);

}
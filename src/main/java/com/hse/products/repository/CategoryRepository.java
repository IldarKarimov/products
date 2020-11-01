package com.hse.products.repository;

import com.hse.products.entity.Category;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {

    boolean existsCategoriesByParentId(Long parentId);

    List<Category> findAllByParentIdIsNull();

    List<Category> findCategoriesByParentId(Long parentId);

    @Query(value = "with tree(id, name, parent_id) as(" +
        "    select id, name, parent_id" +
        "    from category" +
        "    where id = :childId" +
        "    union all" +
        "    select c.id, c.name, c.parent_id" +
        "    from category c, tree" +
        "    where c.id = tree.parent_id" +
        ") SELECT * FROM tree;",
        nativeQuery = true)
    List<Category> getCategoryTree(@Param("childId") Long childId);
}

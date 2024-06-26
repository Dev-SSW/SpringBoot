package study.ORM.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.ORM.Entity.Item.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Item findItemById(Long id); //단건 조회용
}

package jpabook.jpashop.Repository;

import jpabook.jpashop.Domain.Item.Item;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final EntityManager em; //final 변수 자동 주입 (RequiredArgsConstructor로)

    public void save(Item item) {
        if (item.getId() == null) { //id가 없으면 신규이므로 persist 실행
            em.persist(item);
        }
        else { //id가 있으면 이미 데이터베이스에 있는 것을 수정함으로 보고 merge 실행
            em.merge(item);
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id); //Item의 Item id에 맞는 Item을 리턴
    }
    public List<Item> findAll() { //Item의 모든 Item들을 리스트로 리턴
        return em.createQuery("select i from Item i", Item.class).getResultList();
    }

}

package Rejpabook.Rejpashop.Repository;

import Rejpabook.Rejpashop.Domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository //스프링 빈으로 등록
@RequiredArgsConstructor //엔티티 매니저도 주입 가능한 어노테이션 (Service에서는 의존성 주입에서 사용함)
public class MemberRepository {
    /*
    @PersistenceContext //영속성 컨텍스트, 엔티티 매니저 주입
    private EntityManager em;
    //@PersistenceUnit 엔티티 매니저 팩토리 주입
    */
    private final EntityManager em;

    public void save(Member member) {
        em.persist(member); //영속
    }

    public Member findOne(Long id){
        return em.find(Member.class, id); //id에 맞는 Member를 찾아서 리턴해 줌
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class).getResultList();
    } //JPQL 쿼리로 Member 안에 있는 모든 Member를 끌어서 List에 넣어 리턴해 줌

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name",name)
                .getResultList();
    } //JPQL 쿼리로 Member 안에 있는 모든 Member들 중 이름이 같은 것을 List에 넣어 리턴해 줌


}

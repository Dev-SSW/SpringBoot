package study.datajpa.Repository.Jpa;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.datajpa.Entity.Member;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {
    private final EntityManager em;
    List<Member> findAllMembers() {
        return em.createQuery("select m from Member m").getResultList();
    }

}

package study.datajpa.Repository.DataJpa;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import study.datajpa.Entity.Member;
import study.datajpa.Repository.DataJpa.MemberRepositoryCustom;

import java.util.List;

//커스텀의 구현 부분 - Spring Data JPA 대신 JPA를 사용해서 구현하는 방법을 제공해준다 (리포지토리 이름과 Impl이라는 단어를 쳐야 한다)
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {
    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }

}

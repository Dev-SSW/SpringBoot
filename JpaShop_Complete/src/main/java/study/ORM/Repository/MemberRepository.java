package study.ORM.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.ORM.Entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member,Long> {
    List<Member> findByname(String username);
    Member findMemberById(Long id);
}

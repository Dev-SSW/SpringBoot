package study.querydsl.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import study.querydsl.Entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCostom, QuerydslPredicateExecutor<Member> {
    //select m from Member m where m.username
    List<Member> findByUsername(String username);
}

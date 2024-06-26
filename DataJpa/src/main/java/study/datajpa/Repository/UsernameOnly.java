package study.datajpa.Repository;

import org.springframework.beans.factory.annotation.Value;

//projections DTO를 편리하게 조회할 때 사용, 전체 엔티티가 아니라 이름만 조회하고 싶다면?
public interface UsernameOnly {
//    @Value("#{target.username + ' ' + target.age}") //오픈 프로젝션
    String getUsername();
}

package org.zerock.mallapi.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.mallapi.domain.Member;

public interface MemberRepository extends JpaRepository<Member,String> {
    
    @EntityGraph(attributePaths = {"memberRoleList"})
    @Query("select m from Member m where m.email = :email")
    Member getWithRoles(@Param("email") String email);

    // p306
    // jpa에서 연관된 엔티티로 한번의 쿼리로 가져오도록 하는 기능
    // 일반적으로 JPA는 연관된 엔티티를 Lazy Loading(지연로딩) 방식으로 가져옴
    // 하지만 @EntityGraph를 사용하면 한번의 쿼리로 연관된 데이터까지 함께 가져올 수 있음
    /*
        SELECT m.*, r.*
        FROM member m
        LEFT JOIN member_role_list r ON m.id = r.member_id
        WHERE m.email = ?;
    */
}

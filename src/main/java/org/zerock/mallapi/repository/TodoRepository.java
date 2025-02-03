package org.zerock.mallapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.mallapi.domain.Todo;


import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long>{

    @Query("select t from Todo t where t.writer = :writer")
    Page<Todo> findByWriter(@Param("writer") String writer, Pageable pageable);

}

package kr.co.morandi_batch.domain.problem;

import kr.co.morandi_batch.domain.problemalgorithm.ProblemAlgorithm;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
    @Query("""
            SELECT p.baekjoonProblemId
            FROM Problem p
            ORDER BY p.problemId DESC
            """)
    List<Long> findLastBaekjoonProblemId(Pageable pageable);

    List<Problem> findAllByProblemIdIn(List<Long> problemIds);
    @Query("SELECT pa FROM ProblemAlgorithm pa WHERE pa.problem.problemId IN :problemIds")
    List<ProblemAlgorithm> findAllByProblemAlgorithmsIdIn(@Param("problemIds") List<Long> problemIds);

}

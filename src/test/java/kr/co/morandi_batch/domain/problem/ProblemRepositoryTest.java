package kr.co.morandi_batch.domain.problem;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"spring.batch.job.enabled=false"})
class ProblemRepositoryTest {

    @Autowired
    ProblemRepository problemRepository;

    @DisplayName("저장되어 있는 마지막 문제번호를 가져올 수 있다.")
    @Test
    void findLastBaekjoonProblemId() {
        // given
        problemRepository.save(Problem.create(5000L, ProblemTier.B1, 2000L));

        // when
        Pageable pageable = PageRequest.of(0, 1);
        List<Long> lastProblemIds = problemRepository.findLastBaekjoonProblemId(pageable);

        // then
        assertThat(lastProblemIds).isNotNull();
    }
}
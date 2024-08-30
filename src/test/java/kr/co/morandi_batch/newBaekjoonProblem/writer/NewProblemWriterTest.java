package kr.co.morandi_batch.newBaekjoonProblem.writer;

import kr.co.morandi_batch.domain.problem.Problem;
import kr.co.morandi_batch.domain.problem.ProblemRepository;
import kr.co.morandi_batch.domain.problem.ProblemTier;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBatchTest
@SpringBootTest(properties = { "spring.batch.job.enabled=false" })
class NewProblemWriterTest {

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private JdbcBatchItemWriter<Problem> problemWriter;

    @DisplayName("JdbcBatchItemWriter가 Problem 객체를 데이터베이스에 올바르게 삽입한다.")
    @Test
    void testProblemWriter() throws Exception {
        // given
        Problem problem = Problem.create(1L, ProblemTier.B1, 2000L);

        // when
        problemWriter.write(new Chunk<>(Collections.singletonList(problem)));

        // then
        List<Problem> problems = problemRepository.findAll();
        assertEquals(1, problems.size()); // 데이터베이스에 삽입된 Problem의 수가 1인지 확인
        Problem savedProblem = problems.get(0);
        assertEquals(problem.getBaekjoonProblemId(), savedProblem.getBaekjoonProblemId());
        assertEquals(problem.getProblemTier(), savedProblem.getProblemTier());
        assertEquals(problem.getSolvedCount(), savedProblem.getSolvedCount());
    }
}
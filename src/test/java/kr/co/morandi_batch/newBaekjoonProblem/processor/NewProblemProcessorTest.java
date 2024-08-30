package kr.co.morandi_batch.newBaekjoonProblem.processor;

import kr.co.morandi_batch.domain.problem.Problem;
import kr.co.morandi_batch.updateBaekjoonProblem.reader.dto.ProblemDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBatchTest
@SpringBootTest(properties = { "spring.batch.job.enabled=false" })
class NewProblemProcessorTest {

    @Autowired
    private NewProblemProcessor newProblemProcessor;

    @DisplayName("ItemReader에서 들어온 데이터를 Problem으로 변환할 수 있다.")
    @Test
    void testProcess() {
        // given
        ProblemDTO problemDTO = ProblemDTO.create(1L, "테스트 문제", 1, 2000L);

        // when
        Problem problem = newProblemProcessor.process(problemDTO);

        // then
        assertEquals(problemDTO.getProblemId(), problem.getBaekjoonProblemId());
        assertEquals(problemDTO.getLevel(), problem.getProblemTier().getTier());
        assertEquals(problemDTO.getAcceptedUserCount(), problem.getSolvedCount());
    }

}
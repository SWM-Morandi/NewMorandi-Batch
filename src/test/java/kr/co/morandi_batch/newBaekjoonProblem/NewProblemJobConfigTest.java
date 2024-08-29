package kr.co.morandi_batch.newBaekjoonProblem;

import kr.co.morandi_batch.domain.problem.ProblemRepository;
import kr.co.morandi_batch.newBaekjoonProblem.processor.NewProblemProcessor;
import kr.co.morandi_batch.newBaekjoonProblem.reader.NewProblemPagingReader;
import kr.co.morandi_batch.updateBaekjoonProblem.reader.dto.ProblemDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBatchTest
@SpringBootTest(properties = { "spring.batch.job.enabled=false" })
class NewProblemJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private Job newBaekjoonProblemJob;

    @Autowired
    private ProblemRepository problemRepository;

    @SpyBean // @MockBean 처리시 에러
    private NewProblemProcessor mockProcessor;

    @SpyBean // @MockBean 처리시 에러
    private NewProblemPagingReader newProblemPagingReader;

    @BeforeEach
    void setUp() {
        jobLauncherTestUtils.setJob(newBaekjoonProblemJob);
    }

    @DisplayName("DB에 저장된 백준 문제 이후의 데이터가 저장되어야 한다.")
    @Test
    public void testProblemJobConfig() throws Exception {
        // given
        List<ProblemDTO> problemDTOList = new ArrayList<>();
        for (long i = 1; i <= 500; i++)
            problemDTOList.add(ProblemDTO.create(i, "Test Problem " + i, 1, 2000));

        when(newProblemPagingReader.read()).thenAnswer(invocation -> {
            if (problemDTOList.isEmpty()) {
                System.out.println("not found!");
                return null;
            } else {
                ProblemDTO problemDTO = problemDTOList.remove(0);
                return problemDTO;
            }
        });

        // when
        JobExecution execution = jobLauncherTestUtils.launchJob();

        // then
        assertEquals(BatchStatus.COMPLETED, execution.getStatus());
        assertThat(problemRepository.findAll()).hasSize(500);
    }

    @DisplayName("배치 중 에러가 발생하면 롤백되어야 한다.")
    @Test
    public void testRollbackOnError() throws Exception {
        // when
        doThrow(new RuntimeException("Test Exception")).when(mockProcessor).process(any());
        JobExecution execution = jobLauncherTestUtils.launchJob();

        // then
        assertEquals(BatchStatus.FAILED, execution.getStatus());
        assertThat(problemRepository.findAll()).hasSize(0);  // 롤백되어 DB 상태가 초기 상태와 동일
    }

    @DisplayName("첫 번째 청크는 성공적으로 처리되고 두 번째 청크의 첫 번째 아이템에서 예외가 발생해야 한다.")
    @Test
    public void testRollbackOnSecondError() throws Exception {
        // given
        List<ProblemDTO> problemDTOList = new ArrayList<>();
        for (long i = 1; i <= 500; i++)
            problemDTOList.add(ProblemDTO.create(i, "Test Problem " + i, 1, 2000));

        when(newProblemPagingReader.read()).thenAnswer(invocation -> {
            if (problemDTOList.isEmpty()) {
                System.out.println("not found!");
                return null;
            } else {
                ProblemDTO problemDTO = problemDTOList.remove(0);
                return problemDTO;
            }
        });

        doAnswer(new Answer<Object>() {
            private int count = 0;

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                if (++count > 50) { // 첫 번째 청크 (50개) 이후에 예외 발생
                    throw new RuntimeException("Test Exception in second chunk");
                }
                return invocation.callRealMethod(); // 실제 메서드 호출
            }
        }).when(mockProcessor).process(any(ProblemDTO.class));

        // when
        JobExecution execution = jobLauncherTestUtils.launchJob();

        // then
        assertEquals(BatchStatus.FAILED, execution.getStatus());
        assertThat(problemRepository.findAll()).hasSize(50);  // 롤백되어 DB 상태가 초기 상태와 동일
    }
}
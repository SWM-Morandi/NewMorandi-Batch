package kr.co.morandi_batch.newBaekjoonProblem.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.morandi_batch.domain.problem.ProblemRepository;
import kr.co.morandi_batch.updateBaekjoonProblem.reader.dto.ProblemDTO;
import kr.co.morandi_batch.updateBaekjoonProblem.reader.dto.ProblemsResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBatchTest
@SpringBootTest(properties = { "spring.batch.job.enabled=false" })
class NewProblemPagingReaderTest {
    private NewProblemPagingReader newProblemPagingReader;
    private ExchangeFunction exchangeFunction;

    @Autowired
    private ProblemRepository problemRepository;

    @BeforeEach
    void setUp() {
        exchangeFunction = Mockito.mock(ExchangeFunction.class);
        WebClient webClient = WebClient.builder()
                .exchangeFunction(exchangeFunction)
                .build();
        newProblemPagingReader = new NewProblemPagingReader(webClient, problemRepository);
    }

    @DisplayName("ItemReader는 API로 호출되는 모든 Problem을 올바르게 읽어와야 한다.")
    @Test
    void ReaderSuccessTest() {
        // given
        List<ProblemDTO> problemDTOList = new ArrayList<>();
        for (long i = 1; i <= 50; i++)
            problemDTOList.add(ProblemDTO.create(i, "테스트 문제 " + i, 1, 2000));

        when(exchangeFunction.exchange(any(ClientRequest.class)))
                .thenAnswer(invocation -> {
                    if (problemDTOList.isEmpty()) {
                        return Mono.just(ClientResponse.create(HttpStatus.OK)
                                .header("Content-Type", "application/json")
                                .body("{}")
                                .build());
                    } else {
                        ProblemsResponse response = ProblemsResponse.create(problemDTOList.size(), new ArrayList<>(problemDTOList));
                        problemDTOList.clear(); // Clear the list after the first call
                        String jsonResponse = new ObjectMapper().writeValueAsString(response);
                        return Mono.just(ClientResponse.create(HttpStatus.OK)
                                .header("Content-Type", "application/json")
                                .body(jsonResponse)
                                .build());
                    }
                });

        // When
        List<ProblemDTO> readProblems = new ArrayList<>();
        ProblemDTO result;

        while ((result = newProblemPagingReader.read()) != null) {
            readProblems.add(result);
        }

        // Then
        assertEquals(50, readProblems.size());

        for (long i = 1; i <= 50; i++)
            assertEquals("테스트 문제 " + i, readProblems.get((int)i - 1).getTitleKo());
    }
}
package kr.co.morandi_batch.newBaekjoonProblem.reader;

import kr.co.morandi_batch.domain.problem.Problem;
import kr.co.morandi_batch.domain.problem.ProblemTier;
import kr.co.morandi_batch.updateBaekjoonProblem.reader.dto.ProblemDTO;
import kr.co.morandi_batch.updateBaekjoonProblem.reader.dto.ProblemsResponse;
import kr.co.morandi_batch.domain.problem.ProblemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayDeque;
import java.util.List;

@Component
@Slf4j
public class NewProblemPagingReader implements ItemReader<ProblemDTO> {
    private final WebClient webClient;
    private final ProblemRepository problemRepository;
    private Long lastBaekjoonProblemId = 0L;
    private int nextPage = 1;
    private final ArrayDeque<ProblemDTO> problemsQueue = new ArrayDeque<>();

    public NewProblemPagingReader(WebClient.Builder webClientBuilder, ProblemRepository problemRepository) {
        this.webClient = webClientBuilder.baseUrl("https://solved.ac/api/v3").build();
        this.problemRepository = problemRepository;
    }

    @Override
    public ProblemDTO read() {
        if (problemsQueue.isEmpty()) {
            fetchProblems();
        }
        return problemsQueue.poll();
    }
    private void fetchProblems() {
        if (lastBaekjoonProblemId == 0L)
            getLastBaekjoonProblemId();

        log.info("Fetching problems for page: {} and lastBaekjoonProblemId: {}", nextPage, lastBaekjoonProblemId);
        Mono<ProblemsResponse> problemsResponseMono = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/search/problem")
                        .queryParam("query", "id:" + (lastBaekjoonProblemId + 1) + "..")
                        .queryParam("page", nextPage++)
                        .build())
                .retrieve()
                .bodyToMono(ProblemsResponse.class);

        ProblemsResponse problemsResponse = problemsResponseMono.block();
        if (problemsResponse != null && problemsResponse.getItems() != null) {
            problemsQueue.addAll(problemsResponse.getItems());
        }
    }

    private void getLastBaekjoonProblemId() {
        Pageable pageable = PageRequest.of(0, 1);
        List<Long> lastProblemIds = this.problemRepository.findLastBaekjoonProblemId(pageable);
        if (lastProblemIds.isEmpty()) {
            lastBaekjoonProblemId = 0L;
        }
        else {
            lastBaekjoonProblemId = lastProblemIds.get(0);
        }
    }
}
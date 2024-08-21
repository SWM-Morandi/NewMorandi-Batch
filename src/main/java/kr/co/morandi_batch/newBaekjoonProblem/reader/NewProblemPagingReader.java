package kr.co.morandi_batch.newBaekjoonProblem.reader;

import kr.co.morandi_batch.updateBaekjoonProblem.reader.dto.ProblemDTO;
import kr.co.morandi_batch.updateBaekjoonProblem.reader.dto.ProblemsResponse;
import kr.co.morandi_batch.domain.problem.ProblemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayDeque;

@Component
@Slf4j
public class NewProblemPagingReader implements ItemReader<ProblemDTO>, InitializingBean {
    private final WebClient webClient;
    private final ProblemRepository problemRepository;
    private Long lastBaekjoonProblemId;
    private int nextPage = 1;
    private final ArrayDeque<ProblemDTO> problemsQueue = new ArrayDeque<>();

    public NewProblemPagingReader(WebClient.Builder webClientBuilder, ProblemRepository problemRepository) {
        this.webClient = webClientBuilder.baseUrl("https://solved.ac/api/v3").build();
        this.problemRepository = problemRepository;
    }
    @Override
    public void afterPropertiesSet() {
        this.lastBaekjoonProblemId = this.problemRepository.findLastBaekjoonProblemId();
        if (lastBaekjoonProblemId == null) {
            lastBaekjoonProblemId = 0L;
        }
    }

    @Override
    public ProblemDTO read() {
        if (problemsQueue.isEmpty()) {
            fetchProblems();
        }
        return problemsQueue.poll();
    }
    private void fetchProblems() {
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
}
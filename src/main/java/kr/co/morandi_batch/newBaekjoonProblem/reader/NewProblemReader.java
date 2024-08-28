package kr.co.morandi_batch.newBaekjoonProblem.reader;

import kr.co.morandi_batch.updateBaekjoonProblem.reader.dto.ProblemDTO;
import kr.co.morandi_batch.updateBaekjoonProblem.reader.dto.ProblemsResponse;
import kr.co.morandi_batch.domain.problem.ProblemRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Deprecated(
        forRemoval = true
)
public class NewProblemReader implements ItemReader<ProblemsResponse> {

    private final WebClient webClient;
    private final ProblemRepository problemRepository;
    private Long lastBaekjoonProblemId;
    private int nextPage = 1;

    @Autowired
    public NewProblemReader(WebClient.Builder webClientBuilder, ProblemRepository problemRepository) {
        Pageable pageable = PageRequest.of(0, 1);
        this.webClient = WebClient.builder()
                .baseUrl("https://solved.ac/api/v3")
                .build();
        this.problemRepository = problemRepository;
        List<Long> lastProblemIds = this.problemRepository.findLastBaekjoonProblemId(pageable);
        if (lastProblemIds.isEmpty()) {
            lastBaekjoonProblemId = 0L;
        }
        else {
            lastBaekjoonProblemId = lastProblemIds.get(0);
        }
    }

    @Override
    public ProblemsResponse read()  {
        Mono<ProblemsResponse> problemsResponseMono = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/search/problem")
                        .queryParam("query", "id:" + (lastBaekjoonProblemId + 1) + "..")
                        .queryParam("page", nextPage)
                        .build())
                .retrieve()
                .bodyToMono(ProblemsResponse.class);

        //가져온 내용으로 ProblemResponseDto로 역직렬화 한다.
        ProblemsResponse problemsResponse = problemsResponseMono.block();

        List<ProblemDTO> items = problemsResponse.getItems();
        if (items == null || items.isEmpty()) {
            return null;
        }
        nextPage++;
        return problemsResponse;
    }

}

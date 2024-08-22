package kr.co.morandi_batch.batch.reader;

import kr.co.morandi_batch.updateBaekjoonProblem.reader.dto.ProblemsResponse;
import kr.co.morandi_batch.domain.problem.ProblemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@SpringBootTest
class NewProblemReaderTest {

    @Autowired
    ProblemRepository problemRepository;

    @DisplayName("")
    @Test
    void test() {
        // given
        //문제 번호를 가져온다
        Long findProblemId = problemRepository.findLastBaekjoonProblemId();

        //문제 번호가 null이면 0L로 바꾸고, 아니면 ++한다.
        final long lastBaekjoonProblemId = findProblemId == null ? 0L : findProblemId + 1;

        //webflux client를 만든다.
        WebClient webClient = WebClient.builder()
                .baseUrl("https://solved.ac/api/v3")
                .build();

        //webflux client로 문제를 가져온다.
        Mono<ProblemsResponse> problemsResponseMono = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/search/problem")
                        .queryParam("query", "id:" + (lastBaekjoonProblemId + 1) + "..")
                        .queryParam("page", 1)
                        .build())
                .retrieve()
                .bodyToMono(ProblemsResponse.class);

        //가져온 내용으로 ProblemResponseDto로 역직렬화 한다.
        ProblemsResponse problemsResponse = problemsResponseMono.block();

        System.out.println("problemsResponse = " + problemsResponse);


        // when

        // then


    }

//    private void fetchNextBatch() {
//        Mono<ProblemsResponse> problemsResponseMono = this.webClient.get()
//                .uri(uriBuilder -> uriBuilder.path("/search/problem")
//                        .queryParam("query", "id:" + (lastProblemId + 1) + "..")
//                        .queryParam("page", nextPage)
//                        .build())
//                .retrieve()
//                .bodyToMono(ProblemsResponse.class);
//
//        ProblemsResponse problemsResponse = problemsResponseMono.block();
//        if (problemsResponse != null && problemsResponse.getItems() != null) {
//            currentBatch.addAll(problemsResponse.getItems());
//            nextPage++;
//        }
//    }
}
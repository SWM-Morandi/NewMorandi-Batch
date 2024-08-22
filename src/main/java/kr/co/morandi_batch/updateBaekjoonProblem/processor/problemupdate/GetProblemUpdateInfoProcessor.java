package kr.co.morandi_batch.updateBaekjoonProblem.processor.problemupdate;

import kr.co.morandi_batch.updateBaekjoonProblem.processor.problemupdate.dto.ProblemInfoProcessorDTO;
import kr.co.morandi_batch.updateBaekjoonProblem.processor.problemupdate.dto.response.BojProblemInfoList;
import kr.co.morandi_batch.domain.problem.Problem;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class GetProblemUpdateInfoProcessor implements ItemProcessor<List<Problem>, ProblemInfoProcessorDTO> {
    private final WebClient webClient;
    public GetProblemUpdateInfoProcessor(WebClient.Builder webClientBuilder) {
        String prefix = "https://solved.ac/api/v3/search/problem";
        this.webClient = webClientBuilder.baseUrl(prefix).build();
    }

    @Override
    public ProblemInfoProcessorDTO process(List<Problem> item) throws Exception {
//        if(item.get(0).getBaekjoonProblemId()>2500) {
//            return null;
//        }
        BojProblemInfoList problemInfo = getProblemUpdated(item);

        return ProblemInfoProcessorDTO.builder()
                .problems(item)
                .bojProblemInfoList(problemInfo)
                .build();

    }

    private BojProblemInfoList getProblemUpdated(List<Problem> problems) {
        Mono<BojProblemInfoList> bojProblemInfoListMono = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("query", getAPIURI(problems))
                        .build())
                .retrieve()
                .bodyToMono(BojProblemInfoList.class);

        return bojProblemInfoListMono.block();
    }
    private String getAPIURI(List<Problem> problems) {
        StringBuffer sb = new StringBuffer();
        problems.forEach(problem -> {
            sb.append("id:");
            sb.append(problem.getBaekjoonProblemId());
            sb.append("|");
        });

        return sb.toString();
    }
}

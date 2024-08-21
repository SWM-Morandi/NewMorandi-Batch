package kr.co.morandi_batch.updateBaekjoonProblem.writer;

import jakarta.persistence.EntityManager;
import kr.co.morandi_batch.domain.problem.Problem;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProblemUpdateWriter implements ItemWriter<List<Problem>> {
    private final EntityManager em;
    @Override
    public void write(Chunk<? extends List<Problem>> chunk) throws Exception {
        final List<Problem> list = chunk.getItems().stream()
                .flatMap(List::stream)
                .peek(em::merge)
                .toList();

        em.flush();
        em.clear();
    }
}

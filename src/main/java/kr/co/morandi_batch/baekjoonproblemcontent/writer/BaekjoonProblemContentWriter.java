package kr.co.morandi_batch.baekjoonproblemcontent.writer;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.morandi_batch.baekjoonproblemcontent.dto.ProblemContent;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Component
@RequiredArgsConstructor
public class BaekjoonProblemContentWriter implements ItemWriter<ProblemContent> {

    private final AmazonS3 s3Client;
    private final ObjectMapper objectMapper;
    private static final String bucketName = "morandi-baekjoon-problems";


    @Override
    public void write(Chunk<? extends ProblemContent> chunk) throws Exception {
        for (ProblemContent item : chunk) {
            String json = objectMapper.writeValueAsString(item);
            byte[] contentAsBytes = json.getBytes("UTF-8");
            ByteArrayInputStream inputStream = new ByteArrayInputStream(contentAsBytes);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(contentAsBytes.length);

            // 'baekjoon/' 폴더 안에 문제 ID를 이름으로 하는 객체 키를 생성합니다.
            String objectKey = "baekjoon/problems/" + item.getBaekjoonProblemId() + ".json";

            s3Client.putObject(new PutObjectRequest(bucketName, objectKey, inputStream, metadata));
        }
    }


}

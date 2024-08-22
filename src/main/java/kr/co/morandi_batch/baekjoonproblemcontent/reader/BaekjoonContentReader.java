package kr.co.morandi_batch.baekjoonproblemcontent.reader;

import kr.co.morandi_batch.baekjoonproblemcontent.dto.ProblemContent;
import kr.co.morandi_batch.baekjoonproblemcontent.dto.SampleData;
import kr.co.morandi_batch.baekjoonproblemcontent.dto.Subtask;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Deprecated
@RequiredArgsConstructor
public class BaekjoonContentReader implements ItemReader<ProblemContent> {

    private final String baseUrl = "https://www.acmicpc.net/problem/";
    private long currentProblemId = 1000L;
    private final long maxProblemId = 31750L;

    @Override
    public ProblemContent read() throws IOException {
        if (currentProblemId > maxProblemId) return null; // 모든 문제를 읽었다면 null 반환

        String url = baseUrl + currentProblemId;
        Document doc = Jsoup.connect(url).get();



        // 시간 제한
        String timeLimit = doc.select("table#problem-info tbody tr td").get(0).text().trim();

        // 메모리 제한
        String memoryLimit = doc.select("table#problem-info tbody tr td").get(1).text().trim();

        /*
        * 아래는 혹시 모르니깐 남겨둔거
        */
//        String submissions = doc.select("table#problem-info tbody tr td").get(2).text().trim();
//        String accepted = doc.select("table#problem-info tbody tr td").get(3).text().trim();
//        String correctPeople = doc.select("table#problem-info tbody tr td").get(4).text().trim();
//        String correctRate = doc.select("table#problem-info tbody tr td").get(5).text().trim();

        // 제목
        String title = doc.select("#problem_title").text();

        // 문제 설명
        String description = replaceRelativeUrls(doc.select("#problem_description").html());

        // 입력
        String input = doc.select("#problem_input").html();

        // 출력
        String output = doc.select("#problem_output").html();


        // 예제
        List<SampleData> samples = new ArrayList<>();
        for (int i = 1; doc.select("#sampleinput" + i).size() > 0; i++) {

            String inputSample = doc.select("#sample-input-" + i).text();
            String outputSample = doc.select("#sample-output-" + i).text();
            String explanation = doc.select("#sample_explain_" + i + " p").stream()
                    .map(Element::text)
                    .reduce("", (acc, text) -> acc + text + "\n");
            explanation = explanation.isEmpty() ? null : explanation;


            samples.add(SampleData.builder()
                    .input(inputSample)
                    .output(outputSample)
                    .explanation(explanation)
                    .build());
        }

        // 힌트
        String hint = doc.select("#problem_hint").html();
        hint = hint.isEmpty() ? null : hint;

        // 서브태스크
        List<Subtask> subtasks = extractSubtasks(doc);

        // 제한
        String problemLimit = doc.select("#problem_limit").html();
        problemLimit = problemLimit.isEmpty() ? null : problemLimit;

        // 시간 제한
        String additionalTimeLimit = extractAdditionalTimeLimit(doc);

        // 채점 및 기타 정보
        String additionalJudgeInfo = extractAdditionalJudgeInfo(doc);

        ProblemContent problemDetails = ProblemContent.builder()
                .baekjoonProblemId(currentProblemId)
                .title(title)
                .timeLimit(timeLimit)
                .memoryLimit(memoryLimit)
                .description(description)
                .input(input)
                .output(output)
                .samples(samples)
                .hint(hint)
                .subtasks(subtasks)
                .problemLimit(problemLimit)
                .additionalTimeLimit(additionalTimeLimit)
                .additionalJudgeInfo(additionalJudgeInfo)
                .build();



        currentProblemId++;
        return problemDetails;
    }

    private String replaceRelativeUrls(String htmlContent) {
        if (htmlContent == null) return "";
        return htmlContent
                .replace("\"/JudgeOnline", "\"https://www.acmicpc.net/JudgeOnline")
                .replace("\"/upload", "\"https://www.acmicpc.net/upload");
    }

    private List<Subtask> extractSubtasks(Document doc) {
        List<Subtask> subtasks = new ArrayList<>();

        Elements subtaskSections = doc.select("section[id^=subtask], div[id^=problem_subtask], section[id=subtask]");

        for (Element section : subtaskSections) {
            String title = section.select("h2").text();
            List<String> conditionsList = new ArrayList<>();
            String tableConditionsHtml = "";

            // 테이블 형태의 조건 정보가 있는지 확인
            Elements table = section.select("table");
            if (!table.isEmpty()) {
                // 테이블 형태의 조건 정보가 있으면 HTML 형태로 저장
                tableConditionsHtml = table.outerHtml();
            } else {
                // 리스트 형태와 단락 형태의 조건 정보를 추출
                section.select("ul li, p:not(:has(table))").forEach(element -> conditionsList.add(element.text()));
            }

            // Subtask 객체를 생성하고 조건 정보가 있는 경우에만 추가
            if ((!conditionsList.isEmpty() || !tableConditionsHtml.isEmpty()) && !title.isEmpty()) {
                subtasks.add(Subtask.builder()
                        .title(title)
                        .conditions(conditionsList.isEmpty() ? null : conditionsList)
                        .tableConditionsHtml(tableConditionsHtml.isEmpty() ? null : tableConditionsHtml)
                        .build());
            }
        }

        return subtasks.isEmpty() ? null : subtasks;
    }

    private String extractAdditionalTimeLimit(Document doc) {
        Elements timeLimitElements = doc.select("#problem-time-limit ul li");
        StringBuilder timeLimitsBuilder = new StringBuilder();

        if (!timeLimitElements.isEmpty()) { // "시간 제한" 정보가 있을 경우
            for (Element element : timeLimitElements) {
                if (timeLimitsBuilder.length() > 0) {
                    timeLimitsBuilder.append("\n"); // 이전 항목이 있다면 줄 바꿈 추가
                }
                timeLimitsBuilder.append(element.text().trim());
            }
            return timeLimitsBuilder.toString(); // StringBuilder를 String으로 변환
        }
        return null; // "시간 제한" 정보가 없을 경우, null 반환
    }

    // BaekjoonContentReader 클래스 내부에 추가
    private String extractAdditionalJudgeInfo(Document doc) {
        Elements judgeInfoElements = doc.select("#problem-judge-info ul li");
        StringBuilder judgeInfoBuilder = new StringBuilder();

        for (Element element : judgeInfoElements) {
            String text = element.html(); // HTML을 추출하여 JavaScript에 의해 변경될 수 있는 부분을 포함

            // 동적 콘텐츠가 포함된 span 태그를 식별하고 대체합니다.
            text = text.replace("<span class=\"result-ac-text\"></span>", "맞았습니다!!");

            if (judgeInfoBuilder.length() > 0) {
                judgeInfoBuilder.append("\n");
            }
            judgeInfoBuilder.append(text.trim());
        }

        return judgeInfoBuilder.isEmpty() ? null : judgeInfoBuilder.toString();
    }



}

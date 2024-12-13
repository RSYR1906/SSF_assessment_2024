package vttp.batch5.ssf.noticeboard.services;

import java.io.StringReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp.batch5.ssf.noticeboard.models.Notice;
import vttp.batch5.ssf.noticeboard.repositories.NoticeRepository;

@Service
public class NoticeService {

	@Autowired
	private NoticeRepository noticeRepository;

	@Value("${noticeboard.apiurl}")
	private String apiEndpoint;

	private final RestTemplate restTemplate = new RestTemplate();

	public ResponseEntity<String> postToNoticeServer(Notice notice) {

		// String apiEndpoint =
		// "https://publishing-production-d35a.up.railway.app/notice";
		JsonObject noticeJson = notice.toJson();
		System.out.println("Generated JSON: " + noticeJson.toString());

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		HttpEntity<String> request = new HttpEntity<>(noticeJson.toString(), headers);

		if (notice.getTitle() == null || notice.getCategories() == null || notice.getPostDate() == null
				|| notice.getPoster() == null) {
			return ResponseEntity.badRequest().body("Validation failed: All fields must be provided.");
		}

		try {
			ResponseEntity<String> response = restTemplate.exchange(
					apiEndpoint,
					HttpMethod.POST,
					request,
					String.class);

			if (response.getStatusCode() == HttpStatus.OK) {
				JsonReader jReader = Json.createReader(new StringReader(response.getBody()));
				JsonObject responseBody = jReader.readObject();

				String uniqueId = responseBody.getString("id");
				Long timeStamp = System.currentTimeMillis();

				String successResponse = String.format("{\"id\":\"%s\", \"timestamp\":%d}", uniqueId, timeStamp);

				noticeRepository.insertNotices(uniqueId, noticeJson.toString());

				return ResponseEntity.ok(successResponse);
			} else {
				JsonReader jReader = Json.createReader(new StringReader(response.getBody()));
				JsonObject responseBody = jReader.readObject();
				String message = responseBody.getString("message", "Unknown error occurred");

				return ResponseEntity.status(response.getStatusCode())
						.body("Error: " + message);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500)
					.body("Error communicating with the notice server: " + e.getMessage());
		}
	}
}
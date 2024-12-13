package vttp.batch5.ssf.noticeboard.controllers;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;
import vttp.batch5.ssf.noticeboard.models.Notice;
import vttp.batch5.ssf.noticeboard.services.NoticeService;

@Controller
@RequestMapping("")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("")
    public String showNoticeBoard(Model model) {
        model.addAttribute("notice", new Notice());
        return "notice";
    }

    @PostMapping("/notice")
    public String processNotice(
            @Valid @ModelAttribute Notice notice,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("error", "Validation failed. Please correct the errors and try again.");
            return "notice";
        }

        try {
            ResponseEntity<String> response = noticeService.postToNoticeServer(notice);

            if (response.getStatusCode().is2xxSuccessful()) {
                ObjectMapper mapper = new ObjectMapper();
                String uniqueId = mapper.readTree(response.getBody()).get("id").asText();

                model.addAttribute("uniqueId", uniqueId);
                model.addAttribute("notice", notice);
                return "success";
            } else {
                model.addAttribute("error", "Failed to process notice. Server responded with: " + response.getBody());
                return "error";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/status")
    public ResponseEntity<String> checkHealth() {
        try {
            Set<String> keys = redisTemplate.keys("*"); // Fetch all keys
            if ((!keys.isEmpty()) && redisTemplate.randomKey() == null) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("");
            } else {
                return ResponseEntity.status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{}");
        }
    }
}
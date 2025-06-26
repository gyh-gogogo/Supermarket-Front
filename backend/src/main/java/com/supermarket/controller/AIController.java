package com.supermarket.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.alibaba.dashscope.aigc.generation.*;
import com.alibaba.dashscope.common.*;
import com.alibaba.dashscope.exception.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/ai")
public class AIController {

    private static final Logger log = LoggerFactory.getLogger(AIController.class);
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(@RequestParam String question) {;
        log.info("Received question: {}", question);
        SseEmitter emitter = new SseEmitter(60_000L); // 60秒超时
        
        executor.execute(() -> {
            try {
                Generation gen = new Generation();
                Message userMsg = Message.builder()
                    .role(Role.USER.getValue())
                    .content("中文回答我的问题"+question)
                    .build();
                GenerationParam param = GenerationParam.builder()
                    .apiKey("sk-6f2aa154bff64b45a817c839f640714d")
                    .model("qwen-plus")
                    .messages(Arrays.asList(userMsg))
                    .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                    .incrementalOutput(true)
                    .build();
                
                gen.streamCall(param).blockingForEach(result -> {
                    result.getOutput().getChoices().forEach(choice -> {
                        String content = choice.getMessage().getContent();
                        if (content != null && !content.isEmpty()) {
                            try {
                                System.out.println(content);
                                emitter.send(SseEmitter.event()
                                    .data(content)
                                    .id(choice.getFinishReason())
                                    .name("ai-message"));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                });
                
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        
        return emitter;
    }
}
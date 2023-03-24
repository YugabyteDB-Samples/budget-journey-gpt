package com.yugabyte.com;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

import jakarta.annotation.PostConstruct;

@Service
public class TripsAdvisorService {

    private static OpenAiService openAiService;

    private static final String GPT_MODEL = "gpt-3.5-turbo";

    private static final String SYSTEM_TASK_MESSAGE = """
            You are an API server that responds in a JSON format.
            Don't say anything else! Respond only with the JSON!
            The user will provide you a city name and available budget.
            Considering the budget limit, you must suggest a list of places to visit.
            Allocate 30% of the budget to restaurants and bars.
            Another 30% allocate on shows, amuzement parks, and other sightseeing.
            And the remainder of the budget allocate on shopping.
            The user must spend 90-100% of the budget.
            Your JSON response must include a JSON array named 'places'.
            Each array item is another JSON object that includes 'place_name' as a text, 'place_short_info' as a text, and 'place_visit_cost' as a number.
            Don't add anything else in the end after you responded with the JSON!
            """;

    @PostConstruct
    public void initGptService() {
        openAiService = new OpenAiService("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",
                Duration.ofSeconds(30));

        System.out.println("Connected to the OpenAI API");
    }

    public Optional<List<PointOfInterest>> suggestPointsOfInterest(String city, int cost) {
        String request = String.format("I want to visit %s and have a budget of %d dollars", city, cost);

        String response = sendMessage(request);

        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray places = jsonResponse.getJSONArray("places");

            List<PointOfInterest> poiList = new ArrayList<>(places.length());

            for (int i = 0; i < places.length(); i++) {
                JSONObject place = places.getJSONObject(i);

                PointOfInterest poi = new PointOfInterest(
                        place.getString("place_name"),
                        place.getString("place_short_info"),
                        place.getInt("place_visit_cost"));

                System.out.println(poi);
                poiList.add(poi);
            }

            return Optional.of(poiList);
        } catch (JSONException e) {
            System.err.println("Failed to parse: " + response);
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private String sendMessage(String message) {
        List<ChatMessage> messages = List.of(new ChatMessage("system", SYSTEM_TASK_MESSAGE),
                new ChatMessage("user", message));

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .messages(messages)
                .model(GPT_MODEL)
                .temperature(0.8)
                .build();

        StringBuilder builder = new StringBuilder();

        openAiService.createChatCompletion(chatCompletionRequest).getChoices().forEach(choice -> {
            builder.append(choice.getMessage().getContent());
        });

        return builder.toString();
    }
}
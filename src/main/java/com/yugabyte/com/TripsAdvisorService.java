package com.yugabyte.com;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import com.yugabyte.com.jpa.CityTrip;
import com.yugabyte.com.jpa.CityTripRepository;

import jakarta.annotation.PostConstruct;

@Service
public class TripsAdvisorService {

    @Autowired
    private CityTripRepository cityTripsRepository;

    private static OpenAiService openAiService;

    @Value("${openai.key}")
    private String apiKey;

    @Value("${openai.timeout}")
    private int apiTimeout;

    private static final String GPT_MODEL = "gpt-3.5-turbo";

    private static final String SYSTEM_TASK_MESSAGE = """
            You are an API server that responds in a JSON format.
            Don't say anything else. Respond only with the JSON.

            The user will provide you with a city name and available budget. Considering the budget limit, you must suggest a list of places to visit.
            Allocate 30% of the budget to restaurants and bars.
            Allocate another 30% to shows, amusement parks, and other sightseeing.
            And dedicate the remainder of the budget to shopping. Remember, the user must spend 90-100% of the budget.

            Respond in a JSON format, including an array named 'places'. Each item of the array is another JSON object that includes 'place_name' as a text,
            'place_short_info' as a text, and 'place_visit_cost' as a number.

            Don't add anything else in the end after you respond with the JSON.
            """;

    @PostConstruct
    public void initGptService() {
        openAiService = new OpenAiService(apiKey,
                Duration.ofSeconds(apiTimeout));

        System.out.println("Connected to the OpenAI API");
    }

    public PointsOfInterestResponse suggestPointsOfInterest(String city, int budget) {
        String poi = cityTripsRepository.findPointsOfInterest(city, budget);

        try {
            List<PointOfInterest> poiList;

            if (poi != null) {
                poiList = generaPointsOfInterest(poi);
            } else {
                String request = String.format("I want to visit %s and have a budget of %d dollars", city, budget);
                poi = sendMessage(request);

                poiList = generaPointsOfInterest(poi);

                cityTripsRepository.save(new CityTrip(city, budget, poi));
            }

            PointsOfInterestResponse response = new PointsOfInterestResponse();
            response.setPointsOfInterest(poiList);

            return response;
        } catch (Exception e) {
            System.err.println("Failed to parse: " + poi);
            e.printStackTrace();

            PointsOfInterestResponse response = new PointsOfInterestResponse();
            response.setError(e.getMessage());

            return response;
        }
    }

    private String sendMessage(String message) {
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model(GPT_MODEL)
                .temperature(0.8)
                .messages(
                        List.of(
                                new ChatMessage("system", SYSTEM_TASK_MESSAGE),
                                new ChatMessage("user", message)))
                .build();

        StringBuilder builder = new StringBuilder();

        openAiService.createChatCompletion(chatCompletionRequest).getChoices().forEach(choice -> {
            builder.append(choice.getMessage().getContent());
        });

        return builder.toString();
    }

    private List<PointOfInterest> generaPointsOfInterest(String json) {
        JSONObject jsonResponse = new JSONObject(json);
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

        return poiList;
    }

}
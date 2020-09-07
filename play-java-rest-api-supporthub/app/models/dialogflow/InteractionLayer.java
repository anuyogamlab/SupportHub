package models.dialogflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.cloud.dialogflow.v2.Context;
import com.google.cloud.dialogflow.v2.Intent;
import com.google.cloud.dialogflow.v2.Intent.Message;
import com.google.cloud.dialogflow.v2.Intent.Message.Text;
import com.google.cloud.dialogflow.v2.Intent.TrainingPhrase;
import com.google.cloud.dialogflow.v2.Intent.TrainingPhrase.Part;
import com.google.cloud.dialogflow.v2.IntentName;
import com.google.cloud.dialogflow.v2.IntentsClient;
import com.google.cloud.dialogflow.v2.ProjectAgentName;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.cloud.dialogflow.v2.TextInput.Builder;
import com.google.auth.ServiceAccountSigner;
import com.google.auth.oauth2.ServiceAccountCredentials;

public class InteractionLayer {
    public static Map<String, String> listIntents(String projectId) throws Exception {
        // Instantiates a client
        Map<String, String> result;
        try (IntentsClient intentsClient = IntentsClient.create()) {
            // Set the project agent name using the projectID (my-project-id)
            ProjectAgentName parent = ProjectAgentName.of(projectId);
            result = new HashMap<String, String>();
            // Performs the list intents request
            for (Intent intent : intentsClient.listIntents(parent).iterateAll()) {
                System.out.println("====================");
                System.out.format("Intent name: '%s'\n", intent.getName());
                result.put("Intent name", intent.getName());
                System.out.format("Intent display name: '%s'\n", intent.getDisplayName());
                result.put("Intent display name:", intent.getDisplayName());
                System.out.format("Action: '%s'\n", intent.getAction());
                result.put("Action:", intent.getAction());
                System.out.format("Root followup intent: '%s'\n", intent.getRootFollowupIntentName());
                result.put("Root followup intent:", intent.getRootFollowupIntentName());
                System.out.format("Parent followup intent: '%s'\n", intent.getParentFollowupIntentName());
                result.put("Parent followup intent:", intent.getParentFollowupIntentName());

                System.out.format("Input contexts:\n");
                int i = 0;
                for (String inputContextName : intent.getInputContextNamesList()) {
                    System.out.format("\tName: %s\n", inputContextName);
                    result.put("Input Context" + i, inputContextName);
                    i = i + 1;
                }
                System.out.format("Output contexts:\n");
                for (Context outputContext : intent.getOutputContextsList()) {
                    System.out.format("\tName: %s\n", outputContext.getName());
                    result.put("Output Context" + i, outputContext.getName());
                    i = i + 1;
                }
            }
        }
        return result;
    }
    // [END dialogflow_list_intents]

    // [START dialogflow_create_intent]
    /**
     * Create an intent of the given intent type
     * @param displayName The display name of the intent.
     * @param projectId Project/Agent Id.
     * @param trainingPhrasesParts Training phrases.
     * @param messageTexts Message texts for the agent's response when the intent is detected.
     */
    public static void createIntent(String displayName, String projectId,
                                    List<String> trainingPhrasesParts, List<String> messageTexts)
            throws Exception {
        // Instantiates a client
        try (IntentsClient intentsClient = IntentsClient.create()) {
            // Set the project agent name using the projectID (my-project-id)
            ProjectAgentName parent = ProjectAgentName.of(projectId);

            // Build the trainingPhrases from the trainingPhrasesParts
            List<TrainingPhrase> trainingPhrases = new ArrayList<>();
            for (String trainingPhrase : trainingPhrasesParts) {
                trainingPhrases.add(
                        TrainingPhrase.newBuilder().addParts(
                                Part.newBuilder().setText(trainingPhrase).build())
                                .build());
            }

            // Build the message texts for the agent's response
            Message message = Message.newBuilder()
                    .setText(
                            Text.newBuilder()
                                    .addAllText(messageTexts).build()
                    ).build();

            // Build the intent
            Intent intent = Intent.newBuilder()
                    .setDisplayName(displayName)
                    .addMessages(message)
                    .addAllTrainingPhrases(trainingPhrases)
                    .build();

            // Performs the create intent request
            Intent response = intentsClient.createIntent(parent, intent);
            System.out.format("Intent created: %s\n", response);
        }
    }
    // [END dialogflow_create_intent]

    // [START dialogflow_delete_intent]
    /**
     * Delete intent with the given intent type and intent value
     * @param intentId The id of the intent.
     * @param projectId Project/Agent Id.
     */
    public static void deleteIntent(String intentId, String projectId) throws Exception {
        // Instantiates a client
        try (IntentsClient intentsClient = IntentsClient.create()) {
            IntentName name = IntentName.of(projectId, intentId);
            // Performs the delete intent request
            intentsClient.deleteIntent(name);
        }
    }
    // [END dialogflow_delete_intent]

    /**
     * Helper method for testing to get intentIds from displayName.
     */
    public static List<String> getIntentIds(String displayName, String projectId) throws Exception {
        List<String> intentIds = new ArrayList<>();

        // Instantiates a client
        try (IntentsClient intentsClient = IntentsClient.create()) {
            ProjectAgentName parent = ProjectAgentName.of(projectId);
            for (Intent intent : intentsClient.listIntents(parent).iterateAll()) {
                if (intent.getDisplayName().equals(displayName)) {
                    String[] splitName = intent.getName().split("/");
                    intentIds.add(splitName[splitName.length - 1]);
                }
            }
        }

        return intentIds;
    }
    public static String detectIntentTexts(String projectId, List<String> texts, String sessionId,
                                         String languageCode) throws Exception {
        QueryResult queryResult=null;
        // Instantiates a client
        try (SessionsClient sessionsClient = SessionsClient.create()) {
            // Set the session name using the sessionId (UUID) and projectID (my-project-id)
            SessionName session = SessionName.of(projectId, sessionId);
            System.out.println("Session Path: " + session.toString());

            // Detect intents for each text input
            for (String text : texts) {
                // Set the text (hello) and language code (en-US) for the query
                Builder textInput = TextInput.newBuilder().setText(text).setLanguageCode(languageCode);

                // Build the query with the TextInput
                QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

                // Performs the detect intent request
                DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);

                // Display the query result
                queryResult = response.getQueryResult();

                System.out.println("====================");
                System.out.format("Query Text: '%s'\n", queryResult.getQueryText());
                System.out.format("Detected Intent: %s (confidence: %f)\n",
                        queryResult.getIntent().getDisplayName(), queryResult.getIntentDetectionConfidence());
                System.out.format("Fulfillment Text: '%s'\n", queryResult.getFulfillmentText());
            }
        }
        return queryResult.getFulfillmentText();
    }
}
package models.dialogflow;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import models.dialogflow.InteractionLayer;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.cloud.dialogflow.v2.TextInput.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;

@Singleton
public class Integrator
{
    // static variable single_instance of type Singleton
    private static Integrator single_instance = null;
    public static String displayName = "";
    public static String intentId = "";
    public static List<String> messageTexts = new ArrayList<>();
    public static List<String> trainingPhrasesParts = new ArrayList<>();
    private static String projectId = "";

    // variable of type String
    public String s;
    public Map<String,String> result;

    // private constructor restricted to this class itself
    @Inject
    private Integrator()
    {
        s = "to be updated later";
        //to insert project id and other private info
    }

    // static method to create instance of Singleton class
    public static Integrator getInstance(String[] args) throws Exception {
        String method = "";
        System.out.println("arguments"+args[0]+","+args[1]+","+args[2]);
        InteractionLayer intl=new InteractionLayer();
        try {
            method = args[0];
            String command = args[1];
            if (method.equals("list")) {
                if (command.equals("--projectId")) {
                    projectId = args[2];
                }
            } else if (method.equals("create")) {
                displayName = args[1];
                command = args[2];
                if (command.equals("--projectId")) {
                    projectId = args[3];
                }
                System.out.println(args.length);
                for (int i = 4; i < args.length; i += 1) {
                    if (args[i].equals("--messageTexts")) {
                        while ((i + 1) < args.length && !args[(i + 1)].contains("--")) {
                            messageTexts.add(args[++i]);
                            System.out.println(args[i]);
                        }
                        // input.put("messageTexts",messageTexts.);
                    } else if (args[i].equals("--trainingPhrasesParts")) {
                        while ((i + 1) < args.length && !args[(i + 1)].contains("--")) {
                            trainingPhrasesParts.add(args[++i]);
                            System.out.println(args[i]);
                        }
                    }
                }
            } else if (method.equals("delete")) {
                intentId = args[1];
                command = args[2];
                if (command.equals("--projectId")) {
                    projectId = args[3];
                }
            }
        } catch (Exception e) {
            System.out.println("Usage:");
            System.out.println("mvn exec:java -DIntentManagement "
                    + "-Dexec.args='list --projectId PROJECT_ID'\n");

            System.out.println("mvn exec:java -DIntentManagement "
                    + "-Dexec.args='create \"room.cancellation - yes\" --projectId PROJECT_ID "
                    + "--trainingPhrasesParts \"cancel\" \"cancellation\""
                    + "--messageTexts \"Are you sure you want to cancel?\" \"Cancelled.\"'\n");

            System.out.println("mvn exec:java -DIntentManagement "
                    + "-Dexec.args='delete INTENT_ID --projectId PROJECT_ID'\n");

            System.out.println("Commands: list");
            System.out.println("\t--projectId <projectId> - Project/Agent Id");

            System.out.println("Commands: create");
            System.out.println("\t--projectId <projectId> - Project/Agent Id");
            System.out.println("\t<displayName> - [For create] The display name of the intent.");
            System.out.println("\nOptional Commands [For create]:");
            System.out.println("\t--trainingPhrasesParts <trainingPhrasesParts> - Training phrases.");
            System.out.println("\t--messageTexts <messageTexts> - Message texts for the agent's "
                    + "response when the intent is detected.");

            System.out.println("Commands: delete");
            System.out.println("\t--projectId <projectId> - Project/Agent Id");
            System.out.println("\t<intentId> - [For delete] The id of the intent.");
        }
        System.out.println("method"+method);
        if (method.equals("list")) {
            System.out.println(intl.listIntents(projectId));
            //intl.doQuery("list_intents",inpr);
        } else if (method.equals("create")) {
            System.out.println("create");
            intl.createIntent(displayName, projectId, trainingPhrasesParts, messageTexts);
        } else if (method.equals("delete")) {
            System.out.println("delete");
            intl.deleteIntent(intentId, projectId);
        }
        if (single_instance == null)
            single_instance = new Integrator();
        return single_instance;
    }
    public static String getIntent(String[] args) throws Exception {
        InteractionLayer intl=new InteractionLayer();
        ArrayList<String> texts = new ArrayList<>();
        String projectId = "";
        String sessionId = UUID.randomUUID().toString();
        String languageCode = "en-US";

        try {
            String command = args[0];
            if (command.equals("--projectId")) {
                projectId = args[1];
            }

            for (int i = 2; i < args.length; i++) {
                switch (args[i]) {
                    case "--sessionId":
                        sessionId = args[++i];
                        break;
                    case "--languageCode":
                        languageCode = args[++i];
                        break;
                    default:
                        texts.add(args[i]);
                        break;
                }
            }

        } catch (Exception e) {
            System.out.println("Usage:");
            System.out.println("mvn exec:java -DDetectIntentTexts "
                    + "-Dexec.args=\"--projectId PROJECT_ID --sessionId SESSION_ID "
                    + "'hello' 'book a meeting room' 'Mountain View' 'tomorrow' '10 am' '2 hours' "
                    + "'10 people' 'A' 'yes'\"\n");

            System.out.println("Commands: text");
            System.out.println("\t--projectId <projectId> - Project/Agent Id");
            System.out.println("\tText: \"hello\" \"book a meeting room\" \"Mountain View\" \"tomorrow\" "
                    + "\"10am\" \"2 hours\" \"10 people\" \"A\" \"yes\"");
            System.out.println("Optional Commands:");
            System.out.println("\t--languageCode <languageCode> - Language Code of the query (Defaults "
                    + "to \"en-US\".)");
            System.out.println("\t--sessionId <sessionId> - Identifier of the DetectIntent session "
                    + "(Defaults to a random UUID.)");
        }

        String result=intl.detectIntentTexts(projectId, texts, sessionId, languageCode);
        if (single_instance == null)
            single_instance = new Integrator();
       // single_instance=result;
       // return single_instance;
        return result;
    }
}
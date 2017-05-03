package com.escobar.pable.snake;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import ai.api.http.HttpClient;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class MainActivity extends ActionBarActivity implements AIListener{

    private ImageButton listenButton;
    private TextView resultTextView;
    private EditText queryText;
    private ActionController actionController;
    private BluetoothArduinoHelper mBlue;
    private Action lastAction;
    private String insertedMessage;
    final AIConfiguration config = new AIConfiguration("60f6a9c5d4674ffb87c8221e19082eaa",
            AIConfiguration.SupportedLanguages.English,
            AIConfiguration.RecognitionEngine.System);
    private AIService aiService;
    public static ArrayList<ChatMessage> chatlist;
    public static ChatAdapter chatAdapter;
    ListView msgListView;
    private String user1 = "khushi", user2 = "khushi1";
    private Random random;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listenButton = (ImageButton) findViewById(R.id.listenButton);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
        queryText = (EditText) findViewById(R.id.queryText);
        msgListView = (ListView)    findViewById(R.id.msgListView);
        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);
        chatlist = new ArrayList<ChatMessage>();
        chatAdapter = new ChatAdapter(this, chatlist);
        msgListView.setAdapter(chatAdapter);
        random = new Random();
        queryText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(queryText.getText().toString().length() == 0){
                    listenButton.setBackground(getResources().getDrawable(R.drawable.microphone));
                }
                else{
                    listenButton.setBackground(getResources().getDrawable(R.drawable.send_button));
                }
            }
        });
        try{
            Thread th = new Thread(){
                public void run(){
                    try {
                        bluetoothConnection();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
            th.start();
        }
        catch (Exception e){
            Log.i("Snake",e.toString());
        }
        actionController = new ActionController();
        createActions();
        aiService = AIService.getService(this, config);
        aiService.setListener(this);
    }
    public void listenButtonOnClick(final View view) {

        String q = queryText.getText().toString();
        if(q.length() == 0){
            micButtonOnClick(view);
            return;
        }
        sendTextMessage();

        Log.i("Snake",q);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit.Builder builder =
                new Retrofit.Builder().baseUrl("https://api.github.com/")
                        .addConverterFactory(
                                GsonConverterFactory.create()
                        );

        Retrofit retrofit =
                builder
                        .client(
                                httpClient.build()
                        )
                        .build();
        ApiClient apiClient = retrofit.create(ApiClient.class);
        Call<ApiResponse> call = apiClient.askApi(q,"20150910","en",1234567890);
        call.enqueue(apiResponseCallbackGenerator());
    }

    public void createActions(){

        Action ac = new Action();
        ac.setId(1);
        ac.setName("turning on light");
        String[] messages = {"light is turned on","light was already on"};
        ac.setMessages(messages);

        Action ac2 = new Action();
        ac2.setId(2);
        ac2.setName("turning off light");
        String[] messages2 = {"light was already off","light is turned off"};
        ac2.setMessages(messages2);

        Action ac3 = new Action(){
            public String toArduinoMessage(){
                return (new Integer(this.getId())).toString() + "x" +
                        (new Integer(this.getParameterToSend())).toString();
            }
        };
        ac3.setId(3);
        ac3.setName("turning tv on after some timer");
        String[] messages3 = {"tv will be turned on in the right time %0%","tv was already on"};
        ac3.setRequireParam(true);
        ac3.setMessages(messages3);

        Action ac4 = new Action(){
            public String toArduinoMessage(){
                return (new Integer(this.getId())).toString() + "x" +
                        (new Integer(this.getParameterToSend())).toString();
            }
        };
        ac4.setId(4);
        ac4.setName("turning tv off after some timer");
        String[] messages4 = {"tv will be turned off in the right time , after %0% minutes","tv was already off"};
        ac4.setRequireParam(true);
        ac4.setMessages(messages4);

        Action ac5 = new Action();
        ac5.setId(5);
        ac5.setName("temperature in room");
        String[] message5 = {"temperature right now is %0% Degree Celsuis","something wrong with our sensor"};
        ac5.setMessages(message5);

        actionController.add_action(ac);
        actionController.add_action(ac2);
        actionController.add_action(ac3);
        actionController.add_action(ac4);
        actionController.add_action(ac5);
    }
    private void bluetoothConnection() throws Exception{
        mBlue = BluetoothArduinoHelper.getInstance("HC-06");
        mBlue.setDelimiter('#');
        boolean connected = false;
        for(int i = 0;i<3;i++){
            if(mBlue.Connect()){
                connected = true;
                sendTextMessage("the app is connected to arduino you can work on it right now",false);
                break;
            }
            sendTextMessage("couldn't connect to the arduino through bluetooth , another trial is in progress",false);
        }
        if(!connected){
            sendTextMessage("couldn't connect to the arduino through bluetooth , please restart app and try again",false);
            return;
        }
        ;
//        String msg = mBlue.getLastMessage();
    }
    public void insertMessage(String message){
        Log.i("Snake","message is : " + message );
        sendTextMessage(lastAction.renderReceivedMessage(message),false);
//        resultTextView.setText(lastAction.renderReceivedMessage(message));
        lastAction = null;
    }
    private Thread waitForArduinoThread(){
        return new Thread(){
            public void run(){
                Log.i("Snake","waiting for a response blueTooth");
                while(mBlue.countMessages() == 0);
                Log.i("Snake","message received");
                insertedMessage = mBlue.getMenssage(0);
                Log.i("Snake","messages array has " + mBlue.getMenssage(mBlue.countMessages()-1) + " elements");
                runOnUiThread(new Runnable() {
                    public void run() {
                        // your code to update the UI thread here
                        insertMessage(insertedMessage);
                    }
                });

                mBlue.clearMessages();

            }
        };
    };
    private Callback<ApiResponse> apiResponseCallbackGenerator(){
        return new Callback<ApiResponse>(){
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Log.i("Snake",response.toString());
                if(response.isSuccessful()){
                    String resolvedQuery = response.body().getResult().getResolvedQuery();
                    String resolvedAction = response.body().getResult().getAction();
                    Log.i("Snake","Respons Received" + resolvedAction);
//                    sendTextMessage("remove" + resolvedAction,false);
//                    resultTextView.setText(resolvedAction);
                    Action resultAction;
                    try {
                        resultAction = actionController.getAction(Integer.parseInt(resolvedAction));
                        lastAction = resultAction;
                    }
                    catch (Exception e){
                        Log.e("Snake",e.toString());
                        //tell him here we can't relate what you want
                        return;
                    }
                    lastAction = resultAction;
                    if(resultAction.isRequireParam()){
                        try {
                            resultAction.setParameterToSend(0);
                            Log.i("Snake","response TimeX is " + response.body().getResult().getTimeXAmount());
                            resultAction.setParameterToSend(new Integer(response.body().getResult().getTimeXAmount()));
                        }
                        catch (Exception e){
                            Log.e("Snake",e.toString());
                        }
                    }

                    mBlue.sendMessage(resultAction.toArduinoMessage() + '#');

                    Thread blueThread = waitForArduinoThread();
                    blueThread.start();
                }
                else{
                    ResponseBody errorBody = response.errorBody();
                    Log.i("Snake","error body" + errorBody.toString());
                }

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.i("Snake","Resonse Error" + t.toString());
            }
        };
    }

    public void micButtonOnClick(final View view) {
        aiService.startListening();
    }

    public void onResult(final AIResponse response) {
        Result result = response.getResult();
        Log.i("Snake", "Speech: " + result.getResolvedQuery());
        // Get parameters
        String parameterString = "";

        if (result.getParameters() != null && !result.getParameters().isEmpty()) {
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
            }
            result.getParameters().get("timex").getAsJsonObject().get("amount").getAsString();
        }

        // Show results in TextView.
//        resultTextView.setText("Query:" + result.getResolvedQuery() +
//                "\nAction: " + result.getAction() +
//                "\nParameters: " + parameterString);
//        sendTextMessage("Query:" + result.getResolvedQuery() +
//                "\nAction: " + result.getAction() +
//                "\nParameters: " + parameterString,false);
        sendTextMessage(result.getResolvedQuery(),true);
        String resolvedQuery = response.getResult().getResolvedQuery();
        String resolvedAction = response.getResult().getAction();
        Log.i("Snake","Respons Received" + resolvedAction);
//        resultTextView.setText(resolvedAction);
        sendTextMessage(resolvedAction,false);
        Action resultAction;
        try {
            resultAction = actionController.getAction(Integer.parseInt(resolvedAction));
            lastAction = resultAction;
        }
        catch (Exception e){
            Log.e("Snake",e.toString());
            //tell him here we can't relate what you want
            return;
        }
        lastAction = resultAction;
        if(resultAction.isRequireParam()){
            try {
                Log.i("Snake","response TimeX is " +
                        result.getParameters().get("timex").getAsJsonObject().get("amount").getAsString());
                resultAction.setParameterToSend
                        (new Integer(result.getParameters().get("timex").getAsJsonObject().get("amount").getAsString()));
            }
            catch (Exception e) {
                Log.e("Snake", e.toString());
            }
        }
        mBlue.sendMessage(resultAction.toArduinoMessage() + '#');
        Thread blueThread = waitForArduinoThread();
        blueThread.start();
    }

    @Override
    public void onListeningStarted() {}

    @Override
    public void onListeningCanceled() {}

    @Override
    public void onListeningFinished() {}

    @Override
    public void onAudioLevel(final float level) {}
    @Override
    public void onError(final AIError error) {
        sendTextMessage(error.toString(),false);
//        resultTextView.setText(error.toString());
    }
    public void sendTextMessage() {
        runOnUiThread(new Runnable() {
            public void run() {
                // your code to update the UI thread here
                String message = queryText.getText().toString();
                if (!message.equalsIgnoreCase("")) {
                    final ChatMessage chatMessage = new ChatMessage(user1, user2,
                            message, "" + random.nextInt(1000), true);
                    chatMessage.setMsgID();
                    chatMessage.body = message;
                    chatMessage.Date = CommonMethods.getCurrentDate();
                    chatMessage.Time = CommonMethods.getCurrentTime();
                    queryText.setText("");
                    chatAdapter.add(chatMessage);
                    chatAdapter.notifyDataSetChanged();
                }
            }
        });

    }
    public void sendTextMessage(String message,boolean mine){
        final boolean mineFinal = mine;
        final String messageFinal = message;
        runOnUiThread(new Runnable() {
            public void run() {
                // your code to update the UI thread here
//                String message = queryText.getText().toString();
                if (!messageFinal.equalsIgnoreCase("")) {
                    final ChatMessage chatMessage = new ChatMessage(user1, user2,
                            messageFinal, "" + random.nextInt(1000), mineFinal);
                    chatMessage.setMsgID();
                    chatMessage.body = messageFinal;
                    chatMessage.Date = CommonMethods.getCurrentDate();
                    chatMessage.Time = CommonMethods.getCurrentTime();
                    queryText.setText("");
                    chatAdapter.add(chatMessage);
                    chatAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}

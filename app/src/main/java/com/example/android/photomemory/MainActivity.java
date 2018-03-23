package com.example.android.photomemory;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

/**
 * This app displays some pictures for a certain amount of time and then checks your photographic/short-term memory with questions to the pictures.
 */
public class MainActivity extends AppCompatActivity {

    static final int countdown_timer = 10000;
    ArrayList<Integer> questionsList;
    ArrayList<Integer> answerOrder;
    String answerType;
    int counter = 0;
    int questionNumber;
    int score = 0;
    String activeLayout;
    long timer = countdown_timer;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            activeLayout = "main_layout";
            setContentView(R.layout.activity_main);
            questionsList = createRandomList(7);
        } else {
            activeLayout = savedInstanceState.getString("layout");
            questionsList = savedInstanceState.getIntegerArrayList("questions");
            score = savedInstanceState.getInt("score");
            counter = savedInstanceState.getInt("counter");
            if (counter <= questionsList.size() - 1) questionNumber = questionsList.get(counter);
            else questionNumber = savedInstanceState.getInt("questionNumber");
            switch (activeLayout) {
                case "picture_layout":
                    timer = savedInstanceState.getLong("timer");
                    question(view);
                    break;
                case "checkbox_layout":
                case "edittext_layout":
                case "question_layout":
                    answerOrder = savedInstanceState.getIntegerArrayList("answers");
                    answerOrder = changeToQuestion(questionNumber);
                    break;
                case "picture_check_layout":
                    if (counter <= questionsList.size() - 1)
                        questionNumber = questionsList.get(counter - 1);
                    setContentView(R.layout.picture_check_layout);
                    ImageView pictureView = (ImageView) findViewById(R.id.quiz_image);
                    pictureView.setImageResource(getResources().getIdentifier("born" + String.format("%02d", questionNumber), "drawable", getPackageName()));
                    TextView pictureTitle = (TextView) findViewById(R.id.picture_title);
                    pictureTitle.setText(getString(getResources().getIdentifier("title" + String.format("%02d", questionNumber), "string", getPackageName())));
                    break;
                case "score_layout":
                    question(view);
                    break;
                case "instructions":
                    setContentView(R.layout.instructions);
                    activeLayout = "instructions";
                    break;
                default:
                    setContentView(R.layout.activity_main);
                    break;
            }
        }
    }

    /**
     * It saves the instance state for oriantation change. There's only one instance for all the views (picture, question, answer check...)
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("layout", activeLayout);
        savedInstanceState.putInt("score", score);
        savedInstanceState.putIntegerArrayList("questions", questionsList);
        savedInstanceState.putIntegerArrayList("answers", answerOrder);
        savedInstanceState.putInt("counter", counter);
        savedInstanceState.putInt("questionNumber", questionNumber);
        savedInstanceState.putLong("timer", timer);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * This method is called to display the quiz questions - first part is the picture (displayed for certain amount of time) and second part is the question to the picture.
     */
    public void question(View view) {
        if (counter <= questionsList.size() - 1) questionNumber = questionsList.get(counter);
        else if (questionNumber == 99 && timer == countdown_timer) {
            setContentView(R.layout.score_layout);
            activeLayout = "score_layout";
            View toastView = getLayoutInflater().inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout));
            TextView score_toast = (TextView) toastView.findViewById(R.id.score);
            score_toast.setText(getString(R.string.score) + " " + score + " " + getString(R.string.from) + " " + (questionsList.size() + 1));
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(toastView);
            toast.show();
            TextView scoreView = (TextView) findViewById(R.id.score);
            scoreView.setText(getString(R.string.score) + " " + (score * 100 / (questionsList.size() + 1)) + "%");
              return;
        } else questionNumber = 99;
        setContentView(R.layout.picture_layout);
        activeLayout = "picture_layout";
        ImageView pictureView = (ImageView) findViewById(R.id.quiz_image);
        pictureView.setImageResource(getResources().getIdentifier("born" + String.format("%02d", questionNumber), "drawable", getPackageName()));
        TextView pictureTitle = (TextView) findViewById(R.id.picture_title);
        pictureTitle.setText(getString(getResources().getIdentifier("title" + String.format("%02d", questionNumber), "string", getPackageName())));

        new CountDownTimer(timer, 100) {

            public void onTick(long millisUntilFinished) {
                TextView countdown = (TextView) findViewById(R.id.countdown);
                timer = millisUntilFinished;
                countdown.setText(getString(R.string.remaining_time) + " " + String.valueOf(millisUntilFinished / 1000) + "." + String.valueOf((millisUntilFinished - ((millisUntilFinished / 1000) * 1000)) / 100) + " s");
            }

            public void onFinish() {
                timer = countdown_timer;
                answerOrder = changeToQuestion(questionNumber);
            }
        }.start();
    }

    /**
     * This method is called to build the view with the question and answer(s). There are different types of answer(s) - radio button (rb), checkbox (cb) and edit text (et).
     *
     * @param number defines the number of the question
     * @return list of the answer numbers (the order of the answers is random)
     */
    private ArrayList<Integer> changeToQuestion(int number) {
        ArrayList<Integer> answersList;

        answerType = getString(getResources().getIdentifier("answer_type" + String.format("%02d", number), "string", getPackageName()));
        switch (answerType) {
            case "rb":
                answersList = createRandomList(4);
                if (activeLayout.equals("question_layout"))
                    answersList = answerOrder;
                setContentView(R.layout.question_layout);
                activeLayout = "question_layout";

                for (int i = 0; i <= 3; i++) {
                    RadioButton answerView = (RadioButton) findViewById(getResources().getIdentifier("answer" + (i + 1), "id", getPackageName()));
                    answerView.setText(getString(getResources().getIdentifier("answer" + String.format("%02d", number) + "_" + answersList.get(i), "string", getPackageName())));
                }
                break;
            case "cb":
                answersList = createRandomList(6);
                if (activeLayout.equals("checkbox_layout"))
                    answersList = answerOrder;
                setContentView(R.layout.checkbox_layout);
                activeLayout = "checkbox_layout";

                for (int i = 0; i <= 5; i++) {
                    CheckBox answerView = (CheckBox) findViewById(getResources().getIdentifier("answer" + (i + 1), "id", getPackageName()));
                    answerView.setText(getString(getResources().getIdentifier("answer" + String.format("%02d", number) + "_" + answersList.get(i), "string", getPackageName())));
                }
                break;
            case "et":
                setContentView(R.layout.edittext_layout);
                activeLayout = "edittext_layout";
                answersList = null;
                break;
            default:
                answersList = answerOrder;
                break;
        }
        TextView questionView = (TextView) findViewById(R.id.question);
        questionView.setText(getString(getResources().getIdentifier("question" + String.format("%02d", number), "string", getPackageName())));

        return answersList;
    }

    /**
     * This method is called to check the answers of the user - it counts the score and display a toast whether the answer is correct or wrong.
     */
    public void checkAnswer(final View view) {
        String rightAnswer = getString(getResources().getIdentifier("right_answer" + String.format("%02d", questionNumber), "string", getPackageName()));
        boolean rightAnswered = false;
        counter++;

        switch (answerType) {
            case "rb":
                RadioGroup answerGroup = (RadioGroup) findViewById(R.id.radioGroup);

                for (int i = 0; i <= answerOrder.size() - 1; i++) {
                    if (Integer.parseInt(rightAnswer) == answerOrder.get(i)) {
                        if (answerGroup.indexOfChild(answerGroup.findViewById(answerGroup.getCheckedRadioButtonId())) == i)
                            rightAnswered = true;
                    }
                }
                break;
            case "cb":
                ArrayList<Integer> answersList = new ArrayList<Integer>();
                for (int i = 0; i <= answerOrder.size() - 1; i++) {
                    CheckBox checkBox = (CheckBox) findViewById(getResources().getIdentifier("answer" + (i + 1), "id", getPackageName()));
                    if (checkBox.isChecked()) {
                        answersList.add(answerOrder.get(i));
                    }
                }
                Collections.sort(answersList);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i <= answersList.size() - 1; i++) sb.append(answersList.get(i));
                if (sb.toString().equals(rightAnswer)) rightAnswered = true;
                break;
            case "et":
                EditText editText = (EditText) findViewById(R.id.answer_input);
                if (editText.getText().toString().equalsIgnoreCase(rightAnswer))
                    rightAnswered = true;
                break;
        }
        if (rightAnswered) {
            score++;
            Toast toast = Toast.makeText(MainActivity.this, getString(R.string.right_answer), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
            toast.show();
            activeLayout = "picture_layout";
            new CountDownTimer(1000, 1000) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    question(view);
                }
            }.start();

        } else {
            Toast toast = Toast.makeText(MainActivity.this, getString(R.string.wrong_answer), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
            toast.show();
            setContentView(R.layout.picture_check_layout);
            activeLayout = "picture_check_layout";
            ImageView pictureView = (ImageView) findViewById(R.id.quiz_image);
            pictureView.setImageResource(getResources().getIdentifier("born" + String.format("%02d", questionNumber), "drawable", getPackageName()));
            TextView pictureTitle = (TextView) findViewById(R.id.picture_title);
            pictureTitle.setText(getString(getResources().getIdentifier("title" + String.format("%02d", questionNumber), "string", getPackageName())));
        }
    }

    /**
     * This method is called to display the instructions.
     */
    public void instructions(View view) {
        setContentView(R.layout.instructions);
        activeLayout = "instructions";
    }

    /**
     * This method is called to generate a random list of integers.
     *
     * @param size defines the size of the list
     * @return random list of integers
     */
    private ArrayList<Integer> createRandomList(int size) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 1; i <= size; i++) list.add(i);
        Collections.shuffle(list);
        return list;
    }
}

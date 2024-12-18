package org.finance.ui.myId;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.google.gson.JsonObject;

import org.finance.data.service.ApiCallback;
import org.finance.data.service.MyIdService;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import uz.myid.android.sdk.capture.MyIdClient;
import uz.myid.android.sdk.capture.MyIdConfig;
import uz.myid.android.sdk.capture.MyIdException;
import uz.myid.android.sdk.capture.MyIdResult;
import uz.myid.android.sdk.capture.MyIdResultListener;
import uz.myid.android.sdk.capture.model.MyIdBuildMode;
import uz.myid.android.sdk.capture.model.MyIdGraphicFieldType;


/** @noinspection ALL*/
public class MyIdFragment extends BaseFragment implements MyIdResultListener {
    private final MyIdClient client = new MyIdClient();

    // Dialogs
    CustomDialog successDialog;
    CustomDialog errorDialog;
    CustomDialog loadingDialog;

    @Override
    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        super.onActivityResultFragment(requestCode, resultCode, data);
        client.handleActivityResult(resultCode, this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View createView(Context context) {

        actionBar.setBackButtonImage(R.drawable.msg_arrow_back);
        actionBar.setTitle("Идентификация");
        actionBar.setAllowOverlayTitle(true);
        actionBar.setBackgroundColor(Color.parseColor("#50A7EA"));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment(true);
                }
                super.onItemClick(id);
            }
        });

        LinearLayout rootLayout = new LinearLayout(getContext());
        rootLayout.setBackgroundColor(Color.parseColor("#F5F5F5"));

        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setPadding(AndroidUtilities.dp(18), 16, AndroidUtilities.dp(18), 16);


        // "Введите свои данные" TextView
        TextView headerText = new TextView(getContext());
        headerText.setText("Введите свои данные");
        headerText.setTextSize(20);
        headerText.setGravity(Gravity.CENTER);
        headerText.setTextColor(Color.BLACK);
        LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        headerParams.setMargins(0, 30, 0, 30);
        headerText.setLayoutParams(headerParams);
        rootLayout.addView(headerText);

        //
        LinearLayout.LayoutParams inputLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, AndroidUtilities.dp(42));
        inputLayoutParams.setMargins(0, AndroidUtilities.dp(10), 0, AndroidUtilities.dp(10));

        //  "ПИНФЛ или данные паспорта" TextView
        TextView pinflLabel = new TextView(getContext());
        pinflLabel.setText("ПИНФЛ или данные паспорта");
        pinflLabel.setTextSize(16);
        pinflLabel.setTextColor(Color.BLACK);

        rootLayout.addView(pinflLabel);

        //  PINFL EditText
        EditText pinflInput = new EditText(getContext());
        pinflInput.setHint("AA0000000 / JSHIR");
        pinflInput.setLayoutParams(inputLayoutParams);
        pinflInput.setBackgroundResource(R.drawable.rounded_edittext);

        pinflInput.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

        pinflInput.setPadding(AndroidUtilities.dp(12), 12, 12, 12);
        pinflInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    pinflInput.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                } else if (s.length() == 1) {
                    char firstChar = s.charAt(0);
                    if (Character.isDigit(firstChar)) {
                        pinflInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                    }

                } else if (s.length() >= 2) {
                    pinflInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
            }
        });


        rootLayout.addView(pinflInput);

        // "Дата рождения" TextView
        TextView dobLabel = new TextView(getContext());
        dobLabel.setText("Дата рождения");
        dobLabel.setTextSize(16);
        dobLabel.setTextColor(Color.BLACK);
        dobLabel.setPadding(0, 20, 0, 0);
        rootLayout.addView(dobLabel); // Add Date of Birth label

        // Date of Birth EditText
        EditText birthDateInput = new EditText(getContext());
        birthDateInput.setLayoutParams(inputLayoutParams);
        birthDateInput.setBackgroundResource(R.drawable.rounded_edittext);
        birthDateInput.setHint("ДД/ММ/ГГГГ");
        birthDateInput.setInputType(InputType.TYPE_CLASS_DATETIME);
        birthDateInput.setPadding(AndroidUtilities.dp(12), 12, 12, 12);
        birthDateInput.addTextChangedListener(new DateTextWatcher(birthDateInput));
        rootLayout.addView(birthDateInput);

        // Button
        Button submitButton = new Button(getContext());
        submitButton.setText("Далее");
        submitButton.setBackgroundResource(R.drawable.rounded_button);
        submitButton.setTextColor(Color.WHITE);

        FrameLayout.LayoutParams buttonParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM);
        buttonParams.setMargins(0, 50, 0, 0);
        submitButton.setLayoutParams(buttonParams);

        // Handle button click event
        submitButton.setOnClickListener(v -> {


            String birthDateInputText = birthDateInput.getText().toString().trim();

            if (pinflInput.getText().toString().isEmpty()) {
                pinflInput.setError("Пустое поле не разрешено");
                return;
            }

            if (isValidDateOfBirth(birthDateInputText)) {
                // Date of Birth is valid, proceed with the form submission

                startMyId(pinflInput.getText().toString(), birthDateInputText.replace("/", "."));
            } else {
                // Date of Birth is invalid, show an error
                birthDateInput.setError("Инвалид дата рождения");
            }
        });

        rootLayout.addView(submitButton); // Add button


        successDialog = new CustomDialog(getContext(), R.drawable.success_myid_icon, v -> finishFragment(), "Идентификация прошла успешно");
        errorDialog = new CustomDialog(getContext(), R.drawable.error_in_my_id, v -> errorDialog.dismiss(), "Идентификация в процессе");
        loadingDialog = new CustomDialog(getContext(), R.drawable.loading_in_my_id, v -> loadingDialog.dismiss(), "Идентификация не удалась!");

        return rootLayout;
    }

    private boolean isValidDateOfBirth(String dob) {
        // Define a strict regex for DD/MM/YYYY format
        String regex = "^\\d{2}/\\d{2}/\\d{4}$";

        // First check if the input matches the date format using regular expression
        if (!dob.matches(regex)) {
            return false;  // Date format is incorrect
        }

        // Define the expected date format strictly
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sdf.setLenient(false);  // Disable leniency to prevent incorrect date parsing

        try {
            // Try to parse the input date
            Date parsedDate = sdf.parse(dob);

            // Date is in the future
            return parsedDate != null && parsedDate.before(new Date());  // Valid date
        } catch (ParseException e) {
            // If date parsing fails, return false (invalid date)
            return false;
        }
    }


    private void startMyId(final String passportData, final String dateOfBirth) {
        String clientId = "unired_sdk-e2DKUgi4eO7PfPUsZz8MdtkvT0fh4ODrvxUkEvG9";
        String clientHash = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1bnr26n62HvzNFpS2uU+KV3E1D7EMeiCd0q97EzVrwTjKcxCWh2T/qbhm7827zwO4YdF/ge3QniwMf8WK+gEXv7tLhjIGfu84K3P6wLViMAtg3aD4vp4CzG95bDg64ibEBh9DKTb6j8ekZDPuhW5RZh0n/Ff94g0tz9K+S9A3tTslC74yHShzuQYqTkjAFc3EPK5NFt1Uz8tpKVLipP+l9XfR1WNYgCmquxb74WMA9ZKhnLTjim2aSTeQ/8AUmKXh1Pv7Uv15yPBO5/k5BkhUsyJTZikDB1UZF69+wgHyAEZ6jRks7t0+2uPbMA4oKYqondizYOVVhycZud6wqd/zwIDAQAB";
        String clientHashId = "42bab444-56fb-4892-9f44-4f84183650f5";


        MyIdConfig config = new MyIdConfig.Builder(clientId).withClientHash(clientHash, clientHashId).withPassportData(passportData).withBirthDate(dateOfBirth).withBuildMode(MyIdBuildMode.PRODUCTION).build();
        client.startActivityForResult(getParentActivity(), 1, config);
    }


    @Override
    public void onError(MyIdException e) {
        showDialog(errorDialog);
    }

    @Override
    public void onSuccess(MyIdResult myIdResult) {
        showDialog(loadingDialog);
        Bitmap bitmap = myIdResult.getGraphicFieldImageByType(MyIdGraphicFieldType.FACE_PORTRAIT);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        String base64_img = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        String code = myIdResult.getCode();
        JsonObject requestObject = new JsonObject();
        requestObject.addProperty("code", code);
        requestObject.addProperty("image", base64_img);
        MyIdService myIdService = new MyIdService(getContext());
        myIdService.identifyUser(new ApiCallback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject response) {
                showDialog(successDialog);
            }

            @Override
            public void onFailure(String errorMessage) {
                showDialog(errorDialog);
            }
        }, requestObject);
    }

    @Override
    public void onUserExited() {
        Toast.makeText(getContext(), "Пользователь вышел", Toast.LENGTH_SHORT).show();
    }

    public static class DateTextWatcher implements TextWatcher {
        private final EditText editText;
        private String currentText = "";

        public DateTextWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            // Avoid recursive calls with TextWatcher
            if (!charSequence.toString().equals(currentText)) {
                String cleanInput = charSequence.toString().replaceAll("\\D", ""); // Remove any non-numeric characters

                // Mask the clean input
                StringBuilder maskedInput = new StringBuilder();
                int maxLength = Math.min(cleanInput.length(), 8); // Limit to DDMMYYYY format
                int[] inputIndexes = {2, 4};  // Positions for '/', i.e., DD/MM/YYYY

                int inputIndex = 0;
                for (int i = 0; i < maxLength; i++) {
                    if (inputIndexes[0] == inputIndex || inputIndexes[1] == inputIndex) {
                        maskedInput.append("/");
                    }
                    maskedInput.append(cleanInput.charAt(i));
                    inputIndex++;
                }

                currentText = maskedInput.toString();
                editText.removeTextChangedListener(this);  // Temporarily remove listener
                editText.setText(currentText);
                editText.setSelection(currentText.length());  // Place cursor at the end
                editText.addTextChangedListener(this);  // Add listener back
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }


    public static class CustomDialog extends Dialog {

        private final int drawableResourceId; // Drawable resource ID
        private final String message; // message for subtile
        // message for subtile
        private final View.OnClickListener onClickListener; // Click listener for the button

        public CustomDialog(Context context, int drawableResourceId, View.OnClickListener onClickListener, String message) {
            super(context);
            this.message = message;
            this.drawableResourceId = drawableResourceId; // Set drawable resource ID
            this.onClickListener = onClickListener; // Set click listener
        }


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Set no title for the dialog
            requestWindowFeature(Window.FEATURE_NO_TITLE);

            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(50, 50, 50, 50); // Internal padding for content

            // Create LayoutParams with margins for the dialog layout
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            // Set margins to prevent the dialog from sticking to the edges
            params.setMargins(80, 50, 80, 50); // Left, Top, Right, Bottom margins
            layout.setLayoutParams(params);
            layout.setBackgroundColor(Color.TRANSPARENT);

            // ImageView for the success image
            ImageView image = new ImageView(getContext());
            image.setImageResource(drawableResourceId); // Use passed drawable resource ID
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(300, // width (you can adjust this size)
                    300  // height (you can adjust this size)
            );
            imageParams.gravity = Gravity.CENTER; // Center the image
            image.setLayoutParams(imageParams);
            image.setPadding(0, 0, 0, AndroidUtilities.dp(20));
            layout.addView(image);

            // TextView for the success message
            TextView title = new TextView(getContext());
            title.setText(message);
            title.setGravity(Gravity.LEFT);
            title.setTextSize(18);
            title.setPadding(0, 20, 0, AndroidUtilities.dp(28));


            // Button
            TextView button = new TextView(getContext());
            button.setText("Все понятно, закрыть");
            button.setTextColor(Color.parseColor("#50A7EA"));
            button.setGravity(Gravity.RIGHT);
            button.setPadding(0, 0, 0, AndroidUtilities.dp(16));

            // Set the passed onClickListener to the button
            button.setOnClickListener(onClickListener);


            CardView cardView = new CardView(getContext());
            cardView.setPadding(0, AndroidUtilities.dp(16), 0, 0);
            cardView.setRadius(24);
            cardView.setCardBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));


            LinearLayout cardLayout = new LinearLayout(getContext());
            cardLayout.setOrientation(LinearLayout.VERTICAL);
            cardLayout.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(4), AndroidUtilities.dp(8), AndroidUtilities.dp(4));
            cardLayout.addView(title);
            cardLayout.addView(button);

            cardView.addView(cardLayout);

            layout.addView(cardView);

            // Set the layout for the dialog
            setContentView(layout);

            // Customize the dialog window properties (size, position, etc.)
            if (getWindow() != null) {
                // Get the window metrics
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(getWindow().getAttributes());

                // Get display metrics to set the dialog width
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

                // Set the width to 80% of the screen width
                layoutParams.width = (int) (displayMetrics.widthPixels * 0.8);

                // Center the dialog on the screen
                layoutParams.gravity = Gravity.CENTER;

                // Apply the attributes
                getWindow().setAttributes(layoutParams);

                // Set a transparent background for the window
                getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }
    }
}



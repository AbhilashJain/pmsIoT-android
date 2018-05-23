package com.hcl.pmsiot;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {

    private EditText  inputSapId, inputPassword;
    private TextInputLayout inputLayoutSapId, inputLayoutPassword;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputLayoutSapId = (TextInputLayout) findViewById(R.id.input_layout_sapId);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputSapId = (EditText) findViewById(R.id.input_sapId);
        inputPassword = (EditText) findViewById(R.id.input_password);
        btnSignUp = (Button) findViewById(R.id.btn_signin);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });


    }


    private void submitForm(){
        boolean isValid = true;
        inputLayoutSapId.setErrorEnabled(false);
        inputLayoutPassword.setErrorEnabled(false);

        if(!isSapIdValid(inputSapId.getText().toString())){
            isValid = false;
            inputLayoutSapId.setError("Enter valid Sap-Id");
            requestFocus(inputSapId);
        }
        if(!isPasswordValid(inputPassword.getText().toString())){
            isValid = false;
            inputLayoutPassword.setError("Enter valid password");
            requestFocus(inputPassword);
        }

        if(isValid){
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("sapId", inputSapId.getText().toString()); //Optional parameters
            this.startActivity(intent);
        }
    }

    private boolean isSapIdValid(String sapId) {

        return !sapId.trim().isEmpty() && sapId.length() == 8;
    }

    private boolean isPasswordValid(String password) {

        return !password.trim().isEmpty() && password.length() > 4;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}


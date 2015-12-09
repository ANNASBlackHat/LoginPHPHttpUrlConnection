package com.sdg.annasblackhat.loginphphttpurlconnection;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    private final String url = "http://192.168.137.1/android/login.php";
    private final String PARAM_USERNAME = "username";
    private final String PARAM_PASSWORD = "password";
    private EditText username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText)findViewById(R.id.uname);
        password = (EditText)findViewById(R.id.pswd);
    }

    public void btnLogin_click(View v){
        new LoginProcess().execute(username.getText().toString(), password.getText().toString());
    }

    class LoginProcess extends AsyncTask<String, Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... params) {
            BufferedReader in = null;
            String result = null;
            try {
                URL urlServer = new URL(url);
                HttpURLConnection urlConnection = (HttpURLConnection)urlServer.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter(PARAM_USERNAME,params[0])
                        .appendQueryParameter(PARAM_PASSWORD,params[1]);
                String query = builder.build().getEncodedQuery();

                OutputStream stream = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream,"UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                stream.close();

                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                in = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer("");
                String line = "";

                while((line = in.readLine())!= null){
                    stringBuffer.append(line);
                }
                in.close();
                result = stringBuffer.toString();
            }catch (Exception ex){
                Log.e("ERROR",ex.getMessage());
            }
            return result.equals("1");
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result){
                startActivity(new Intent(MainActivity.this, SuccessActivity.class));
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Message")
                        .setMessage("Username or password invalid, please retry!")
                        .setPositiveButton("OK",null)
                        .show();
            }
        }
    }

}

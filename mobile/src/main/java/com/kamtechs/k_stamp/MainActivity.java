package com.kamtechs.k_stamp;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.text.Html;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    public static TextView title;
    public static TextView usernameLabel;
    public static TextView passwordLabel;
    public static TextView companyLabel;
    public static TextView message;

    public static Button toStamps;
    public static Button stamp;
    public static Button donePicker;

    public static EditText username;
    public static EditText password;
    public static EditText company;

    public static NumberPicker numberPicker;
    public static ProgressBar spinner;
    public static AdView adBanner;

    public static ArrayList<String> companyNames;
    public static ArrayList<String> companyURLs;
    public static String companyURL;
    public static String suffixURL;
    public static String postResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setArrayData();
        setUIElements();
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


   private void setUIElements() {
       adBanner = (AdView)findViewById(R.id.adView);

       title = (TextView)findViewById(R.id.textView);
       String text = "<font color='#ffa500'>K</font>" +
                      "<font color='#696969'>-</font>" +
                      "<font color='#009EFB'>Stamp</font>";
       title.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
       title.setTypeface(null, Typeface.BOLD);

       usernameLabel = (TextView)findViewById(R.id.textView2);
       usernameLabel.setTextColor(Color.parseColor("#FFFFFF"));

       passwordLabel = (TextView)findViewById(R.id.textView3);
       passwordLabel.setTextColor(Color.parseColor("#FFFFFF"));

       companyLabel = (TextView)findViewById(R.id.textView4);
       companyLabel.setTextColor(Color.parseColor("#FFFFFF"));

       message = (TextView)findViewById(R.id.textView5);
       message.setTextColor(Color.parseColor("#FFFFFF"));
       message.setVisibility(View.INVISIBLE);

       spinner = (ProgressBar)findViewById(R.id.progressBar);
       spinner.setVisibility(View.INVISIBLE);

       toStamps = (Button)findViewById(R.id.button);
       toStamps.setVisibility(View.INVISIBLE);
       stamp = (Button)findViewById(R.id.button2);
       stamp.setOnClickListener(new Button.OnClickListener() {
           public void onClick(View v) {
               stamp();
           }
       });
       donePicker = (Button)findViewById(R.id.button3);
       donePicker.setVisibility(View.INVISIBLE);
       donePicker.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
               numberPicker.setVisibility(View.INVISIBLE);
               donePicker.setVisibility(View.INVISIBLE);
           }
       });

       numberPicker = (NumberPicker)findViewById(R.id.numberPicker);
       Object[] objectList = companyNames.toArray();
       numberPicker.setDisplayedValues(Arrays.copyOf(objectList, objectList.length, String[].class));
       numberPicker.setMinValue(0);
       numberPicker.setMaxValue(objectList.length - 1);
       numberPicker.setVisibility(View.INVISIBLE);
       numberPicker.setWrapSelectorWheel(false);
       numberPicker.setOnScrollListener(new NumberPicker.OnScrollListener() {
           @Override
           public void onScrollStateChange(NumberPicker numberPicker, int scrollState) {
               if (scrollState == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE) {
                   if (numberPicker.getValue() == 0) {
                       company.setText("");
                       companyURL = "";
                   } else {
                       company.setText(companyNames.get(numberPicker.getValue()));
                       companyURL = companyURLs.get(numberPicker.getValue());
                   }
               }
           }
       });


       username = (EditText)findViewById(R.id.editText);
       username.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
       username.setImeOptions(EditorInfo.IME_ACTION_DONE);
       username.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {
               numberPicker.setVisibility(View.INVISIBLE);
               donePicker.setVisibility(View.INVISIBLE);
               message.setVisibility(View.INVISIBLE);
               message.setText("");
               return false;
           }
       });
       username.setText(readFromFile("username.txt"));

       password = (EditText)findViewById(R.id.editText2);
       password.setImeOptions(EditorInfo.IME_ACTION_DONE);
       password.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {
               numberPicker.setVisibility(View.INVISIBLE);
               donePicker.setVisibility(View.INVISIBLE);
               message.setVisibility(View.INVISIBLE);
               message.setText("");
               return false;
           }
       });
       password.setText(readFromFile("password.txt"));

       company = (EditText)findViewById(R.id.editText3);
       company.setInputType(InputType.TYPE_NULL);
       company.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {
               numberPicker.setVisibility(View.VISIBLE);
               donePicker.setVisibility(View.VISIBLE);
               InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
               imm.hideSoftInputFromWindow(username.getWindowToken(), 0);
               imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
               imm.hideSoftInputFromWindow(company.getWindowToken(), 0);
               message.setVisibility(View.INVISIBLE);
               message.setText("");
               return false;
           }
       });

       if (readFromFile("picker.txt").equals("")) {
           numberPicker.setValue(0);
           companyURL = "";
           company.setText("");
       } else {
           int pickerIndex = Integer.parseInt(readFromFile("picker.txt"));
           numberPicker.setValue(pickerIndex);
           companyURL = companyURLs.get(pickerIndex);
           company.setText(companyNames.get(pickerIndex));
       }

   }

    public static void setArrayData() {
        companyNames = new ArrayList<String>();
        companyURLs = new ArrayList<String>();
        companyURL = "";
        postResult = "-1";
        suffixURL = "/wfc/applications/wtk/html/ess/quick-ts-record.jsp";

        companyNames.addAll(Arrays.asList(
        "-Select-", "Allegisgroup", "Calacademy", "Cornell", "COX",
        "Kohls", "University of Georgia", "University of Miami"
        ));

        companyURLs.addAll(Arrays.asList(
                "", "https://timekeeper.allegisgroup.com", "https://kronos.calacademy.org"
                , "https://www.kronos.cornell.edu", "https://wfc.coxenterprises.com"
                , "https://kronos-ess.kohls.com", "https://mytime.uga.edu"
                , "https://timecard.miami.edu"
        ));
    }


    public void stamp() {
        if (companyURL.equals("") || company.getText().toString().equals("")) {
            AlertDialog.Builder noCompanyAlert = new AlertDialog.Builder(this);
            noCompanyAlert.setTitle("Please select a company");
            noCompanyAlert.setMessage("Cox, Kohls, etc...");
            noCompanyAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // do nothing
                }
            });
            noCompanyAlert.show();
        } else {

            try {
                HttpClient client = new DefaultHttpClient();
                String postURL = "https://wfc.coxenterprises.com/wfc/applications/wtk/html/ess/quick-ts-record.jsp";
                HttpPost post = new HttpPost(postURL);
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", "628KTSVETKOV"));
                params.add(new BasicNameValuePair("password", "Donotshare1!"));
                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
                post.setEntity(ent);
                HttpResponse responsePOST = client.execute(post);
                HttpEntity resEntity = responsePOST.getEntity();
                if (resEntity != null) {
                    //return EntityUtils.toString(resEntity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //return "";
            /*Handler handler = new Handler();
            TaskCanceler taskCanceler;
            postAsyncTask task = new postAsyncTask();
            taskCanceler = new TaskCanceler(task);
            handler.postDelayed(taskCanceler, 3 * 1000);
            task.execute();*/
        }
    }

    public String postData() {
        // Create a new HttpClient and Post Header
       /* HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(companyURL + suffixURL);

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
            nameValuePairs.add(new BasicNameValuePair("username", username.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("password", password.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("qtsAction", ""));
            nameValuePairs.add(new BasicNameValuePair("RunningClock", ""));
            /*httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"US-ASCII"));
            httppost.setEntity(new StringEntity("username=" + username.getText().toString()
                                              + "&password=" + password.getText().toString()
                                              + "&qtsAction=&RunningClock="));
            Log.d("Post Looks Like:", EntityUtils.toString(httppost.getEntity()));
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            return EntityUtils.toString(response.getEntity());

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        return "";*/

        /*HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
                System.getProperty("http.agent"));
        HttpPost httppost = new HttpPost(companyURL + suffixURL);
        httppost.addHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));

        try {
            httppost.setEntity(new StringEntity("username=" + username.getText().toString()
                    + "&password=" + password.getText().toString()
                    + "&qtsAction=&RunningClock="));
            HttpResponse resp = httpclient.execute(httppost);
            HttpEntity ent = resp.getEntity();
            System.out.println("Response:" + EntityUtils.toString(ent));
            return EntityUtils.toString(ent);
        } catch (UnsupportedEncodingException e) {
            //TODO
        } catch (ClientProtocolException e) {
            // TODO
        } catch (IOException e) {
            // TODO
        }
        return "";*/
        /*HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
                System.getProperty("http.agent"));
        HttpPost httppost = new HttpPost(companyURL + suffixURL);
        httppost.addHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));

        try {

            httppost.setEntity(new StringEntity("username=" + "628KTSVETKOV"
                    + "&password=" + "Donotshare1!"
                    + "&qtsAction=&RunningClock="));

            HttpResponse resp = httpclient.execute(httppost);
            return EntityUtils.toString(resp.getEntity());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";*/

        /*HttpParams params = new BasicHttpParams();
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
                HttpVersion.HTTP_1_1);
        HttpClient httpClient = new DefaultHttpClient(params);
        HttpPost httppost = new HttpPost(companyURL + suffixURL);
        httppost.addHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));


        try {
            // Add your data
            /*List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
            nameValuePairs.add(new BasicNameValuePair("username", username.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("password", password.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("qtsAction", ""));
            nameValuePairs.add(new BasicNameValuePair("RunningClock", ""));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"utf-8"));
            HttpResponse response = httpClient.execute(httppost);
            return EntityUtils.toString(response.getEntity());*/
            /*httppost.setEntity(new StringEntity("username=" + username.getText().toString()
                    + "&password=" + password.getText().toString()
                    + "&qtsAction=&RunningClock="));

            HttpResponse resp = httpClient.execute(httppost);
            return EntityUtils.toString(resp.getEntity());

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        return "";*/


        /*try {
            String data = URLEncoder.encode("username", "US-ASCII") + "="
                    + URLEncoder.encode(username.getText().toString(), "US-ASCII");
            data += "&" + URLEncoder.encode("password", "US-ASCII") + "="
                    + URLEncoder.encode(password.getText().toString(), "US-ASCII");


            URL url = new URL(companyURL + suffixURL);//SERVER_ADRRESS + restPath);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(
                    conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String line;
            String answer = "";
            while ((line = rd.readLine()) != null) {

                //Log.i("sendPostRequest, " + restPath + ":", line);
                answer += line;

            }
            wr.close();
            rd.close();
            return answer;
        } catch (Exception e) {

        }
        return "";*/

       /* try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(companyURL + suffixURL);
            post.setHeader("Content-type", "application/x-www-form-urlencoded");
            post.setHeader("Accept", "application/x-www-form-urlencoded");
            JSONObject obj = new JSONObject();
            obj.put("username", username.getText().toString());
            obj.put("password", password.getText().toString());
            obj.put("qtsAction" , "");
            obj.put("RunningClock" , "");
            post.setEntity(new StringEntity(obj.toString(), "US-ASCII"));
            HttpResponse response = client.execute(post);
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {

        }
        return "";*/



        /*try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(companyURL + suffixURL);
            List<NameValuePair> pairs = new ArrayList<NameValuePair>();

            pairs.add(new BasicNameValuePair("username", username.getEditableText().toString()));
            pairs.add(new BasicNameValuePair("password", password.getEditableText().toString()));
            pairs.add(new BasicNameValuePair("qtsAction" , ""));
            pairs.add(new BasicNameValuePair("RunningClock" , ""));

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, HTTP.UTF_8);
            post.setEntity(entity);
            HttpResponse response = client.execute(post);
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {

        }
        return "";*/

        /*HttpParams httpParameters = new BasicHttpParams();
        // Set the timeout in milliseconds until a connection is established.
        int timeoutConnection = 10000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        // Set the default socket timeout (SO_TIMEOUT)
        // in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = 10000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        // Instantiate the custom HttpClient
        HttpClient client = new MyHttpClient(httpParameters,
                getApplicationContext());
        HttpPost post = new HttpPost(companyURL + suffixURL);

        try {
            List<NameValuePair> pairs = new ArrayList<>();

            pairs.add(new BasicNameValuePair("username", username.getEditableText().toString()));
            pairs.add(new BasicNameValuePair("password", password.getEditableText().toString()));
            pairs.add(new BasicNameValuePair("qtsAction", ""));
            pairs.add(new BasicNameValuePair("RunningClock", ""));

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, HTTP.UTF_8);
            post.setEntity(entity);
        } catch (Exception e) {

        }

        BufferedReader in = null;
        try
        {
            HttpResponse response = client.execute(post);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null)
            {
                sb.append(line + NL);
            }
            in.close();
            String page = sb.toString();
            //System.out.println(page);

            return page;
        }
        catch (ClientProtocolException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return "";*/

        /*HttpParams httpParameters = new BasicHttpParams();
        // Set the timeout in milliseconds until a connection is established.
        int timeoutConnection = 10000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        // Set the default socket timeout (SO_TIMEOUT)
        // in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = 10000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

*/

       /* try {
            URL url = new URL("https://wfc.coxenterprises.com/wfc/applications/wtk/html/ess/quick-ts-record.jsp");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            //conn.setReadTimeout(10000);
            //conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(42);

            //List<NameValuePair> params = new ArrayList<NameValuePair>();
            //params.add(new BasicNameValuePair("username", "988ABENNETT"));
            //params.add(new BasicNameValuePair("password", "Panthercat69"));
            //params.add(new BasicNameValuePair("thirdParam", paramValue3));

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write("username=988ABENNETT&password=Panthercat69");//getQuery(params));
            writer.flush();
            writer.close();
            os.close();

            conn.connect();
        } catch (Exception e) {

        }
        return "";*/

        // Instantiate the custom HttpClient
        /*HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("https://wfc.coxenterprises.com/wfc/applications/wtk/html/ess/quick-ts-record.jsp");

        try {

            httppost.setEntity(new StringEntity("username=628KTSVETKOV&password=Donotshare1!"));
        */
           // httppost.addHeader(new BasicHeader("Accept", "*/*"));
           /* httppost.addHeader(new BasicHeader("Accept-Encoding", "gzip, deflate"));
            httppost.addHeader(new BasicHeader("Content-Length", "" + httppost.getEntity().getContentLength()));
            httppost.addHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));
            httppost.addHeader(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0"));

            HttpResponse resp = httpclient.execute(httppost);
            return EntityUtils.toString(resp.getEntity());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";*/
        return "";
    }

    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    private class postAsyncTask extends AsyncTask<Editable, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            message.setVisibility(View.INVISIBLE);
            spinner.setVisibility(View.VISIBLE);
            stamp.setEnabled(false);
            username.setEnabled(false);
            password.setEnabled(false);
            company.setEnabled(false);
            toStamps.setEnabled(false);
            message.setText("");
        }

        protected String doInBackground(Editable... params) {
            String response = postData();
            return response;
        }

        protected void onPostExecute(String result) {
            postResult = result;
        }
    }

    public class TaskCanceler implements Runnable{
        private AsyncTask task;

        public TaskCanceler(AsyncTask task) {
            this.task = task;
        }

        @Override
        public void run() {
            if (task.getStatus() == AsyncTask.Status.RUNNING ) {
                task.cancel(true);
            }

            message.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.INVISIBLE);
            stamp.setEnabled(true);
            username.setEnabled(true);
            password.setEnabled(true);
            company.setEnabled(true);
            toStamps.setEnabled(true);

            if (postResult.equals("-1")) {
                message.setText("Timed Out\\n Try Again");
                message.setTextColor(Color.parseColor("#D3D3D3"));
            } else if (postResult.equals("") || postResult.contains("exception") || postResult.contains("incorrect")) {
                if (postResult.contains("incorrect")) {
                    message.setText("Incorrect\n User / Password");
                } else {
                    message.setText("FAILED");
                }
                message.setTextColor(Color.parseColor("#FF0000"));
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                String currentDateandTime = sdf.format(new Date());
                message.setText("SUCCESS\n" + currentDateandTime);
                message.setTextColor(Color.parseColor("#0000FF"));
            }

            writeToFile("username.txt", username.getText().toString());
            writeToFile("password.txt", password.getText().toString());
            writeToFile("picker.txt", numberPicker.getValue() + "");

            postResult = "-1";
        }
    }

    private void writeToFile(String url, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(url, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    private String readFromFile(String url) {

        String ret = "";

        try {
            InputStream inputStream = openFileInput(url);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
}
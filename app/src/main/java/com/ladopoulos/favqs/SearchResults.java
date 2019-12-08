package com.ladopoulos.favqs;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import com.xeoh.android.texthighlighter.TextHighlighter;

public class SearchResults extends AppCompatActivity {
    int j = 1;
    SharedPreferences myPrefs;
    final String apiToken = "69973e8d1208b4f6d35c4d3fff6b3c1c";
    final String urlSearchQuote = "https://favqs.com/api/quotes/?filter=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
    }

    @Override
    public void onResume(){
        super.onResume();
        final ScrollView scroll = findViewById(R.id.scroll_View);
        Button close = findViewById(R.id.close);
        Button next = findViewById(R.id.next);
        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
        final String search_query = myPrefs.getString("SEARCH_QUERY", null);
        RequestQueue secondQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, urlSearchQuote+search_query, (String)null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);
                        lp.setMargins(1, 1, 1, 1);
                        final LinearLayout llay = findViewById(R.id.scrollViewText);
                        final TextView[] listViewText = new TextView[25];
                        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.poly_italic);
                        llay.removeAllViews();

                        if(response.length()==0){
                            Toast toast = Toast.makeText(getApplicationContext(), "Oops..\nEmpty Response", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                        } else {
                            try {
                                JSONArray quotesArray = response.getJSONArray("quotes");
                                for (int i = 0; i < 25; i++) {
                                    JSONObject temp = quotesArray.getJSONObject(i);
                                    String quote = temp.getString("body");
                                    String author = temp.getString("author");
                                    ImageView divider = new ImageView(getApplicationContext());
                                    listViewText[i] = new TextView(getApplicationContext());
                                    listViewText[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    listViewText[i].setText("\""+quote+"\""+" -"+author);
                                    listViewText[i].setTextColor(Color.parseColor("#000000"));
                                    listViewText[i].setTypeface(typeface);
                                    listViewText[i].setTextSize(25);
                                    if(i%2==0){
                                        listViewText[i].setBackgroundColor(Color.parseColor("#ededed"));
                                    }
                                    divider.setLayoutParams(lp);
                                    divider.setBackgroundColor(Color.parseColor("#61b3ff"));
                                    llay.addView(listViewText[i]);
                                    llay.addView(divider);
                                    new TextHighlighter()
                                            .setBackgroundColor(Color.parseColor("#FFFF00"))
                                            .setForegroundColor(Color.RED)
                                            .addTarget(listViewText[i])
                                            .highlight(search_query,TextHighlighter.BASE_MATCHER);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                        Log.e("VOLLEY ERROR",error.toString());
                    }
                }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Token token="+apiToken);
                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        secondQueue.add(jsonObjectRequest);

        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                j++;
                RequestQueue secondQueue = Volley.newRequestQueue(getApplicationContext());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, urlSearchQuote+search_query+"&page="+j, (String)null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);
                                lp.setMargins(1, 1, 1, 1);
                                final LinearLayout llay = findViewById(R.id.scrollViewText);
                                final TextView[] listViewText = new TextView[25];
                                Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.poly_italic);
                                llay.removeAllViews();

                                if(response.length()==0){
                                    Toast toast = Toast.makeText(getApplicationContext(), "Oops..\nEmpty Response", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                                    toast.show();
                                } else {
                                    try {
                                        JSONArray quotesArray = response.getJSONArray("quotes");
                                        for (int i = 0; i < 25; i++) {
                                            JSONObject temp = quotesArray.getJSONObject(i);
                                            String quote = temp.getString("body");
                                            String author = temp.getString("author");
                                            ImageView divider = new ImageView(getApplicationContext());
                                            listViewText[i] = new TextView(getApplicationContext());
                                            listViewText[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                            listViewText[i].setText("\""+quote+"\""+" -"+author);
                                            listViewText[i].setTextColor(Color.parseColor("#000000"));
                                            listViewText[i].setTypeface(typeface);
                                            listViewText[i].setTextSize(25);
                                            if(i%2==0){
                                                listViewText[i].setBackgroundColor(Color.parseColor("#ededed"));
                                            }
                                            divider.setLayoutParams(lp);
                                            divider.setBackgroundColor(Color.parseColor("#61b3ff"));
                                            llay.addView(listViewText[i]);
                                            llay.addView(divider);
                                            new TextHighlighter()
                                                    .setBackgroundColor(Color.parseColor("#FFFF00"))
                                                    .setForegroundColor(Color.RED)
                                                    .addTarget(listViewText[i])
                                                    .highlight(search_query,TextHighlighter.BASE_MATCHER);
                                        }
                                        (new Thread(new Runnable(){
                                            public void run(){
                                                scroll.fullScroll(View.FOCUS_UP);
                                            }
                                        })).start();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast toast = Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();
                                Log.e("VOLLEY ERROR",error.toString());
                            }
                        }){
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json");
                        headers.put("Authorization", "Token token="+apiToken);
                        return headers;
                    }
                };
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        50000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                secondQueue.add(jsonObjectRequest);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()){
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

    }
}

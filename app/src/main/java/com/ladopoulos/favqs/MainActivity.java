package com.ladopoulos.favqs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
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
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class MainActivity extends AppCompatActivity {
    SharedPreferences myPrefs;
    final String apiToken = "69973e8d1208b4f6d35c4d3fff6b3c1c";
    final String urlRandomQuote = "https://favqs.com/api/qotd";
    final String urlQuotes = "https://favqs.com/api/quotes";
    private ClipboardManager myClipboard;
    private ClipData myClip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
        RequestQueue secondQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, urlQuotes, (String)null, new Response.Listener<JSONObject>() {
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
                                    listViewText[i].setTextSize(15);
                                    if(i%2==0){
                                    listViewText[i].setBackgroundColor(Color.parseColor("#ededed"));
                                    }
                                    divider.setLayoutParams(lp);
                                    divider.setBackgroundColor(Color.parseColor("#61b3ff"));
                                    llay.addView(listViewText[i]);
                                    llay.addView(divider);
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
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onResume() {
        super.onResume();
        final LinearLayout llay = findViewById(R.id.scrollViewText);
        final TextView quoteTextView = findViewById(R.id.quote);
        final Button logIn = findViewById(R.id.login);
        final Button signUp = findViewById(R.id.sign_up);
        final SearchView search = findViewById(R.id.searchBar);
        final ImageView favs = findViewById(R.id.favourite);
        final ImageView share = findViewById(R.id.share);
        final ImageView upvote = findViewById(R.id.like);
        final ImageView downvote = findViewById(R.id.dislike);
        final ScrollView scroll = findViewById(R.id.scroll_View);
        final ScrollView scroll2 = findViewById(R.id.scroll_View2);
        String currentVersionCode = Integer.toString(BuildConfig.VERSION_CODE);
        myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        Log.e("CURRENT VERSION CODE", currentVersionCode);

        if (myPrefs.getBoolean("firstrun", true)) {
            // Do first run stuff here
            ShowcaseConfig config = new ShowcaseConfig();
            config.setDelay(500); // half second between each showcase view
            MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(MainActivity.this, "1");
            sequence.setConfig(config);
            sequence.addSequenceItem(scroll2, "Tap for details\nDouble tap to refresh\nLong press to copy quote", "GOT IT");
            sequence.addSequenceItem(scroll, "Double tap to refresh", "GOT IT");
            sequence.start();
            myPrefs.edit().putBoolean("firstrun", false).apply();
        }

        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
        String versionCode = myPrefs.getString("versionCode", "");
        try {
            if (!versionCode.matches(currentVersionCode)) {
                Log.e("CURRENT VERSION CODE", "MPIKA");
            myPrefs.edit().putString("versionCode", currentVersionCode).apply();
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    //set icon
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    //set title
                    .setTitle("Updates")
                    //set message
                    .setMessage("-Added \"Share\" button")
                    //set positive button
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //set what would happen when positive button is clicked
                        }
                    })
                    .show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        RequestQueue firstQueue = Volley.newRequestQueue(getApplicationContext());
        //HIDE ELEMENTS
        findViewById(R.id.myList).setVisibility(View.GONE);

        favs.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "Login required", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        });
        upvote.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "Login required", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        });
        downvote.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "Login required", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "Login required", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        });
        logIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent loginIntent = new Intent(getApplicationContext(), Login.class);
                startActivity(loginIntent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent loginIntent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(loginIntent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, urlRandomQuote, (String)null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response.length()==0){
                            Toast toast = Toast.makeText(getApplicationContext(), "Oops..\nEmpty Response", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                        } else {
                            try {
                                JSONObject quote = response.getJSONObject("quote");
                                String quoteLine = quote.getString("body");
                                String author = quote.getString("author");
                                myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = myPrefs.edit();
                                editor.putString("FAVS", quote.getString("favorites_count"));
                                editor.putString("UPVOTES", quote.getString("upvotes_count"));
                                editor.putString("DOWNVOTES", quote.getString("downvotes_count"));
                                editor.putString("QUOTELINE", quoteLine);
                                editor.putString("QUOTEAUTHOR", author);
                                editor.apply();
                                JSONArray tagsTemp = quote.getJSONArray("tags");
                                String tags = (tagsTemp.toString()).replaceAll("\\[|]|\"", "");
                                quoteTextView.setText("\""+quoteLine+"\""+"\n\n"+author+"\n"+"tags: "+tags);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyError errorCode = (VolleyError) error.getCause();
                        Toast toast = Toast.makeText(getApplicationContext(),errorCode.toString(), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                        Log.e("VOLLEY ERROR",error.toString());
                    }
                }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        firstQueue.add(jsonObjectRequest);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = myPrefs.edit();
                editor.putString("SEARCH_QUERY", query);
                editor.apply();
                Intent searchIntent = new Intent(getApplicationContext(), SearchResults.class);
                startActivity(searchIntent);
                search.setQuery("",false);
                search.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


        final GestureDetector gd = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener(){
            //here is the method for double tap
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                //your action here for double tap e.g.
                RequestQueue thirdQueue = Volley.newRequestQueue(getApplicationContext());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, urlRandomQuote, (String)null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if(response.length()==0){
                                    Toast toast = Toast.makeText(getApplicationContext(), "Oops..\nEmpty Response", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                                    toast.show();
                                } else {
                                    try {
                                        JSONObject quote = response.getJSONObject("quote");
                                        String quoteLine = quote.getString("body");
                                        String author = quote.getString("author");
                                        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = myPrefs.edit();
                                        editor.putString("FAVS", quote.getString("favorites_count"));
                                        editor.putString("UPVOTES", quote.getString("upvotes_count"));
                                        editor.putString("DOWNVOTES", quote.getString("downvotes_count"));
                                        editor.putString("QUOTELINE", quoteLine);
                                        editor.putString("QUOTEAUTHOR", author);
                                        editor.apply();
                                        JSONArray tagsTemp = quote.getJSONArray("tags");
                                        String tags = (tagsTemp.toString()).replaceAll("\\[|]|\"", "");
                                        quoteTextView.setText("\""+quoteLine+"\""+"\n\n"+author+"\n"+"tags: "+tags);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                VolleyError errorCode = (VolleyError) error.getCause();
                                Toast toast = Toast.makeText(getApplicationContext(),errorCode.toString(), Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();
                                Log.e("VOLLEY ERROR",error.toString());
                            }
                        }){
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }
                };
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        50000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                thirdQueue.add(jsonObjectRequest);
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
                String quoteLine = myPrefs.getString("QUOTELINE", null);
                myClip = ClipData.newPlainText("text", quoteLine);
                myClipboard.setPrimaryClip(myClip);
                Toast toast = Toast.makeText(getApplicationContext(), "Text Copied", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e){
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
                String favsCount = myPrefs.getString("FAVS", null);
                String upvotesCount = myPrefs.getString("UPVOTES", null);
                String downvotesCount = myPrefs.getString("DOWNVOTES", null);
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Details");
                alertDialog.setMessage("Favorited by: "+favsCount+"\n"+"Upvoted by: "+upvotesCount+"\n"+"Downvoted by: "+downvotesCount);
                alertDialog.setNegativeButton("OK!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = alertDialog.create();
                alert.show();
                return true;
            }


        });
        quoteTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.performClick();
                return gd.onTouchEvent(event);
            }
        });

        final GestureDetector gd1 = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener(){
            //here is the method for double tap
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                //your action here for double tap e.g.
                RequestQueue fourthQueue = Volley.newRequestQueue(getApplicationContext());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, urlQuotes, (String)null, new Response.Listener<JSONObject>() {
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
                                            listViewText[i].setTextSize(15);
                                            if(i%2==0){
                                                listViewText[i].setBackgroundColor(Color.parseColor("#ededed"));
                                            }
                                            divider.setLayoutParams(lp);
                                            divider.setBackgroundColor(Color.parseColor("#61b3ff"));
                                            llay.addView(listViewText[i]);
                                            llay.addView(divider);
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
                fourthQueue.add(jsonObjectRequest);
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);

            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e){
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                return true;
            }


        });
        llay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.performClick();
                return gd1.onTouchEvent(event);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
package com.remedia.ucusbilgileri;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.Time;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.webkit.URLUtil;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AnalogClock;
import android.widget.DigitalClock;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import java.net.MalformedURLException;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {

    private final String TAG =  "Ucus Bilgileri";

    private String iata_datas = "";

    private String sect_datas = "";

    public String aa = "" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);


        try {

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            String formattedDate = df.format(c.getTime());

            Log.d(TAG,"md5 : " + formattedDate);
            String date_string = md5(formattedDate);

            final ListView listview = (ListView) findViewById(R.id.listview);
            final ListView listview_sect = (ListView) findViewById(R.id.listview1);
            final ListView listview_details = (ListView) findViewById(R.id.listview2);
            listview_details.setVisibility(ListView.INVISIBLE);
            listview_sect.setVisibility(ListView.INVISIBLE);


            URL url = new URL("http://abc.com/api/airports/?token=" + date_string + "&format=xml");
            Log.d(TAG,"http://abc.com/api/airports/?token=" + date_string + "&format=xml");

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("item");

            final ArrayList<String> list_iata = new ArrayList<String>();
            final ArrayList<String> list_name = new ArrayList<String>();
            final ArrayList<String> list_sect = new ArrayList<String>();

            list_sect.add("Ic Hatlar Gelen");
            list_sect.add("Ic Hatlar Giden");
            list_sect.add("Dis Hatlar Gelen");
            list_sect.add("Dis Hatlar Giden");

            for (int i = 0; i < nodeList.getLength(); i++) {

                Node node = nodeList.item(i);

                Element fstElmnt = (Element) node;
                NodeList nameList = fstElmnt.getElementsByTagName("iata");
                Element nameElement = (Element) nameList.item(0);
                nameList = nameElement.getChildNodes();
                Log.d(TAG,"iata = " + ((Node) nameList.item(0)).getNodeValue());

                NodeList websiteList = fstElmnt.getElementsByTagName("name");
                Element websiteElement = (Element) websiteList.item(0);
                websiteList = websiteElement.getChildNodes();
                Log.d(TAG,"name = " + ((Node) websiteList.item(0)).getNodeValue());

                list_name.add(((Node) websiteList.item(0)).getNodeValue().toUpperCase());
                list_iata.add(((Node) nameList.item(0)).getNodeValue());

            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, list_name);
            listview.setAdapter(adapter);

            final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, list_sect);



            listview.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    // ListView Clicked item index
                    int itemPosition     = position;

                    // ListView Clicked item value
                    String  itemValue    = (String) listview.getItemAtPosition(position);

                    // Show Alert
                    Toast.makeText(getApplicationContext(),
                            "Position :" + itemPosition + "  List name : " + itemValue + " List iata : " + list_iata.get(itemPosition), Toast.LENGTH_LONG)
                            .show();

                    Log.d(TAG,"Position :" + itemPosition + "  List name : " + itemValue + " List iata : " + list_iata.get(itemPosition));

                    aa = list_iata.get(itemPosition);

                    listview_sect.setAdapter(adapter1);

                    listview_sect.destroyDrawingCache();
                    listview.setVisibility(ListView.INVISIBLE);
                    listview_sect.setVisibility(ListView.VISIBLE);

                }

            });





            listview_sect.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {

                        // ListView Clicked item index
                        int itemPosition     = position;

                        // ListView Clicked item value
                        String  itemValue    = (String) listview_sect.getItemAtPosition(position);

                        // Show Alert
                        Toast.makeText(getApplicationContext(),
                                "Position :" + itemPosition + "  List name : " + itemValue.toLowerCase().replace(" ","_"), Toast.LENGTH_LONG)
                                .show();


                        Log.d(TAG,"Position :" + itemPosition + "  List name : " + sect_cevir(itemValue));

                        aa = aa + "," + sect_cevir(itemValue);

                        getflightdatas();



                    }

            });


        } catch (Exception e) {
            System.out.println("XML Pasing Excpetion = " + e);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void getflightdatas() {

        final ListView listview_sect = (ListView) findViewById(R.id.listview1);
        final ListView listview_details = (ListView) findViewById(R.id.listview2);

        final ArrayList<String> list_flights = new ArrayList<String>();


        try {

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            String formattedDate = df.format(c.getTime());

            Log.d(TAG,"md5 : " + formattedDate);
            String date_string = md5(formattedDate);

            String[] separated = aa.split(",");

            URL url = new URL("http://abc.com/api/" + separated[1] + "/?iata=" + separated[0] + "&token=" + date_string + "&format=xml");
            Log.d(TAG,"http://abc.com/api/" + separated[1] + "/?iata=" + separated[0] + "&token=" + date_string + "&format=xml");

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("item");

            String bb;
            String cc;

            for (int i = 0; i < nodeList.getLength(); i++) {

                Node node = nodeList.item(i);

                Element fstElmnt = (Element) node;
                NodeList nameList = fstElmnt.getElementsByTagName("carrier");

                Element nameElement;

                if(nameList.getLength()==0)
                {
                    bb = "";
                }else{
                    nameElement = (Element) nameList.item(0);
                    nameList = nameElement.getChildNodes();
                    Log.d(TAG,"carrier = " + ((Node) nameList.item(0)).getNodeValue());
                    bb = ((Node) nameList.item(0)).getNodeValue();
                }

                NodeList websiteList = fstElmnt.getElementsByTagName("flightNumber");
                Element websiteElement = (Element) websiteList.item(0);
                websiteList = websiteElement.getChildNodes();
                Log.d(TAG,"flightNumber = " + ((Node) websiteList.item(0)).getNodeValue());


                NodeList locationList = fstElmnt.getElementsByTagName("location");
                Element locationElement;

                if(locationList.getLength()==0)
                {
                    cc = "";
                }else{
                    locationElement = (Element) locationList.item(0);
                    locationList = locationElement.getChildNodes();
                    Log.d(TAG,"location = " + ((Node) locationList.item(0)).getNodeValue());
                    cc = ((Node) locationList.item(0)).getNodeValue();
                }


                list_flights.add(((Node) websiteList.item(0)).getNodeValue().toUpperCase() + " - " + bb + " - " + cc);


            }

            final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, list_flights);

            listview_details.setAdapter(adapter2);

            listview_details.destroyDrawingCache();
            listview_sect.setVisibility(ListView.INVISIBLE);
            listview_details.setVisibility(ListView.VISIBLE);


        } catch (Exception e) {
            System.out.println("XML Pasing Excpetion = " + e);
        }




    }
    public String md5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    public String sect_cevir(String s) {

        String a = "";

            if(s.equalsIgnoreCase("Ic Hatlar Gelen")){
                a = "domestic_arrivals";
            }else  if(s.equalsIgnoreCase("Ic Hatlar Giden")){
                a = "domestic_departures";
            }else  if(s.equalsIgnoreCase("Dis Hatlar Gelen")){
                a = "international_arrivals";
            }else  if(s.equalsIgnoreCase("Dis Hatlar Giden")){
                a = "international_departures";
            }
            return a;

    }
}

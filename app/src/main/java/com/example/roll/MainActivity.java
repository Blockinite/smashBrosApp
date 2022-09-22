package com.example.roll;

import static java.lang.Math.abs;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.roll.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.roll.databinding.ActivityMainBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    //map of every character and the proficiencies the user has with them
    private HashMap<String,Integer> prof = new HashMap<>();
    //map of every character and what their matchup is with every other character
    private HashMap<String,HashMap<String,String>> charsMap = new HashMap<>();
    private String saveTex = "";
    //map containing the characters the user plays and how good they are at them
    private Map<String,String> proficiencies = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear().commit();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard)
                .build();
        //array of all characters
        ArrayList<String> charArray = new ArrayList<>();
        getSupportActionBar().setTitle("Main");
        //find the smallest possible matchup (a negative number)
        int smallest = 0;
        try {
            //get the data of matchups from premade file
            BufferedReader objReader1 = new BufferedReader(
                    new InputStreamReader(getAssets().open("chars.txt")));
            String strCurrentLine1 = "";
            while ((strCurrentLine1 = objReader1.readLine()) != null) {

                String[] splitStr1 = strCurrentLine1.split(",");
                if(Integer.parseInt(splitStr1[2])<smallest){

                    smallest = Integer.parseInt(splitStr1[2]);

                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {

            BufferedReader objReader = new BufferedReader(
                    new InputStreamReader(getAssets().open("chars.txt")));

            String strCurrentLine = "";
            while ((strCurrentLine = objReader.readLine()) != null) {

                String[] splitStr = strCurrentLine.split(",");
                try {
                    splitStr[0] = splitStr[0].toUpperCase();
                    splitStr[0] = splitStr[0].replace("_", " ");
                    splitStr[1] = splitStr[1].toUpperCase();
                    splitStr[1] = splitStr[1].replace("_", " ");
                    splitStr[2] = splitStr[2].toUpperCase();
                    splitStr[2] = splitStr[2].replace("_", " ");
                }catch (Exception e){}
                HashMap<String, String> tempMap = new HashMap<>();
                if(charsMap.get(splitStr[0]) != null) {
                    tempMap = charsMap.get(splitStr[0]);
                }
                if(!charArray.contains(splitStr[0])){
                    charArray.add(splitStr[0]);
                    prof.put(splitStr[0],0);

                }
                //get map full of the matchups with corrected scores (lowest is 0, not a negative)
                tempMap.put(splitStr[1],String.valueOf(Integer.parseInt(splitStr[2])+abs(smallest)));
                charsMap.put(splitStr[0],tempMap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Button mainButton = findViewById(R.id.mainButton);
        getIntent().putExtra("chars", charArray);


        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView3, home.class,null).setReorderingAllowed(true)
                        .addToBackStack("name") // name can be null
                        .commit();

                getSupportActionBar().setTitle("Main");



            }
        });

        Button oppButton = findViewById(R.id.oppButton);
        oppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pass the proficiencies to the Opponent tab
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                Gson gson = new Gson();
                String hashMapString = gson.toJson(prof);
                editor.putString("text", saveTex);
                editor.commit();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView3, opponent.class,null).setReorderingAllowed(true)
                        .addToBackStack("name") // name can be null
                        .commit();

                getSupportActionBar().setTitle("Opponent");

            }
        });

    }

    public void addProf(View v){
        try {
            //get the chosen character
            Spinner s = (Spinner) findViewById(R.id.spinner);
            TextInputEditText t = (TextInputEditText) findViewById(R.id.textIn);
            //get the input text (the proficiency)
            String p = t.getText().toString();
            String character = s.getSelectedItem().toString();
            TextView textBox = (TextView) findViewById(R.id.charsSelected);
            textBox.setMovementMethod(new ScrollingMovementMethod());

            //put the proficiency in a map with the related character
            prof.put(character, Integer.parseInt(p));
            proficiencies.put(character,p);
            textBox.setText("");
            CharSequence currentText = textBox.getText();
            for (Map.Entry<String, String> entry : proficiencies.entrySet()) {
                currentText = textBox.getText();
                textBox.setText(currentText + "\n" + entry.getKey() + " : " + entry.getValue());
            }
            saveTex = textBox.getText().toString();
            System.out.println(prof);
        }catch (Exception e){


        }

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void runCheck(View v){

            System.out.println("here1");
            Spinner s = (Spinner) findViewById(R.id.spinner2);
            String character = s.getSelectedItem().toString();
            TextView text = (TextView) findViewById(R.id.textView);

            text.setText("");
            proficiencies.forEach((key,p) -> {
                try {
                            int temp =  Integer.parseInt(charsMap.get(key).get(character)) * (int) (0.5 * Integer.parseInt(p)) + (25*Integer.parseInt(p));
                            CharSequence currentText = text.getText();
                            System.out.println("here333333333");

                            text.setText(currentText + "\n" + key + " : " + String.valueOf(temp));

                }catch (Exception e){


                }
                });



    }

}
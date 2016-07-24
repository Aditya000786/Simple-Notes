package tejas.recyclerview1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import org.xdty.preference.colorpicker.ColorPickerDialog;
import org.xdty.preference.colorpicker.ColorPickerSwatch;

import java.util.Calendar;

public class Add_Screen extends AppCompatActivity {

    EditText title_text , content_text;
    public DBHelper mydb ;
    int id;
    String time_now,pre_color,pre_isLocked ;
    private Toolbar mytoolbar;
    View top_layout,bottom_layout;
    static int random_color=1 ;
    private int default_color;
    int[] color_array;
    Bundle bundle;
    SharedPreferences preferences;
    FloatingActionButton fab;

    boolean isDark;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences("prefs",MODE_PRIVATE);
        isDark = preferences.getBoolean("isDark",false);
        if(isDark)
            setTheme(R.style.DarkAppTheme);
        else
            setTheme(R.style.AppTheme);

        setContentView(R.layout.new_addscreen);

        mydb = new DBHelper(this);

        title_text=(EditText)findViewById(R.id.addscreen_text1);
        content_text=(EditText)findViewById(R.id.addscreen_text2);

        top_layout=(View)findViewById(R.id.addScreen_toplayout);
        bottom_layout=(View)findViewById(R.id.addscreen_bottomlayout);
        color_array=getResources().getIntArray(R.array.my_color_array);

        if(isDark){
            bottom_layout.setBackgroundColor(getResources().getColor(R.color.dark_theme_background));
        }

        // Set a Toolbar to replace the ActionBar.
        mytoolbar = (Toolbar) findViewById(R.id.addscreeen_toolbar);
        setTitle("Add");
        setSupportActionBar(mytoolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



            bundle = getIntent().getExtras();

            if(bundle != null)//called to edit
            {
                id = bundle.getInt("id");
                Cursor cursor = mydb.getData(id);
                cursor.moveToFirst();

                String pre_title = cursor.getString(cursor.getColumnIndex(DBHelper.COL_TITLE));
                String pre_content = cursor.getString(cursor.getColumnIndex(DBHelper.COL_CONTENT));
                pre_isLocked = cursor.getString(cursor.getColumnIndex(DBHelper.COL_IsLocked));
                pre_color = cursor.getString(cursor.getColumnIndex(DBHelper.COL_COLOR));

                title_text.setText(pre_title);
                content_text.setText(pre_content);
                top_layout.setBackgroundColor(Integer.parseInt(pre_color));
                mytoolbar.setBackgroundColor(Integer.parseInt(pre_color));
                if(Build.VERSION.SDK_INT >= 21){
                    getWindow().setStatusBarColor(Integer.parseInt(pre_color));
                }
                cursor.close();
            }
            else{// called to add
                default_color = getRandom(color_array);
                top_layout.setBackgroundColor(default_color);
                mytoolbar.setBackgroundColor(default_color);
                if(Build.VERSION.SDK_INT >= 21){
                    getWindow().setStatusBarColor(default_color);
                }
            }

        fab=(FloatingActionButton)findViewById(R.id.addscreen_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bundle == null){
                    Add_data(-1);//new data
                }
                else
                    Add_data(id);//update existing
            }
        });

    }

    //for auto_coloring of notes
    public static int getRandom(int[] array){
        if(random_color >= array.length)
            random_color=1;
        int random = array[random_color];
        random_color++;
        return random;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addscreen_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.pick_color:
                ColorButtonPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void Add_data(int id ){
        //Close Keyboard
        InputMethodManager manager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);


        //time_now = java.text.DateFormat.getDateTimeInstance().format(new Date());
        Calendar calendar =Calendar.getInstance();
        time_now = calendar.getTime().toString().substring(0,16);
        String titleEntered = title_text.getText().toString();
        String contentEntered = content_text.getText().toString();

        //if title empty get from substring
        if(titleEntered.matches("") && !contentEntered.matches("")){
            //get 2 words from content
            int length = contentEntered.length();
            if(length <= 15)
              titleEntered=contentEntered.substring(0,length);
            else
                titleEntered=contentEntered.substring(0,15);
        }

        if(titleEntered.matches("") && contentEntered.matches("")){
            //nothing entered
            Toast.makeText(Add_Screen.this,"Cant Save Empty Note",Toast.LENGTH_SHORT).show();
        }

        else{

            if(id == -1) {//new data
                String colorEntered =  String.valueOf(default_color);
                mydb.insertData(titleEntered, contentEntered, time_now, colorEntered,"false");
            }
            else
            {//update existing
                mydb.updateData(id,titleEntered,contentEntered,time_now,pre_color,pre_isLocked);
            }
            Toast.makeText(Add_Screen.this,"Saved",Toast.LENGTH_SHORT).show();
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
            supportFinishAfterTransition();
        }
        else
            finish();


    }

    public void ColorButtonPressed()
    {

        ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
                color_array,
                default_color,
                4,
                ColorPickerDialog.SIZE_SMALL);

        dialog.setOnColorSelectedListener(
                new ColorPickerSwatch.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int color) {
                        default_color=color;
                        pre_color=String.valueOf(color);
                        top_layout.setBackgroundColor(color);
                        mytoolbar.setBackgroundColor(color);
                        if(Build.VERSION.SDK_INT >= 21){
                            getWindow().setStatusBarColor(color);
                        }
                    }
                }
        );
        dialog.show(getFragmentManager(), "color_dialog_test");
    }

    @Override
    public void onBackPressed() {

        if(bundle != null )//called to edit
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.discard_changes)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //save
                            Add_data(id);//update existing
                        }
                    })
                    .setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //discard
                            supportFinishAfterTransition();
                        }
                    })
                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Do nothing
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                supportFinishAfterTransition();
            else
                finish();
        }

    }
}

package com.example.elcapi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;

import android.widget.Toast;

import com.example.myapplication.R;

import static com.example.elcapi.jnielc.ledseek;

public class MainActivity extends Activity implements OnSeekBarChangeListener{

    private SeekBar seekBar_red,seekBar_blue,seekBar_green;
    private Switch mSwitch;
    private static final int seek_red=0xa1;
    private static final int seek_green=0xa2;
    private static final int seek_blue=0xa3;
    private static final int led_off=0;
    private static final int led_on=1;
    private static final int led_red=2;
    private static final int led_blue=3;
    private static final int led_green=4;
    private int brightness;

    int fb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekBar_red=(SeekBar) findViewById(R.id.SeekBar_red);
        seekBar_blue=(SeekBar) findViewById(R.id.SeekBar_blue);
        seekBar_green=(SeekBar) findViewById(R.id.SeekBar_green);
        seekBar_red.setOnSeekBarChangeListener(this);
        seekBar_blue.setOnSeekBarChangeListener(this);
        seekBar_green.setOnSeekBarChangeListener(this);
        mSwitch=(Switch) findViewById(R.id.switch1);
        fb=jnielc.open();
        switch (get_led_color()){
            case led_red:
               seekBar_red.setProgress(get_led_brightness());
               seekBar_blue.setProgress(0);
               seekBar_green.setProgress(0);
                break;
            case led_blue:
                seekBar_blue.setProgress(get_led_brightness());
                seekBar_red.setProgress(0);
                seekBar_green.setProgress(0);
                break;
            case led_green:
                seekBar_green.setProgress(get_led_brightness());
                seekBar_red.setProgress(0);
                seekBar_blue.setProgress(0);
                break;
            default:
                break;
        }


        if(get_led_color()>=led_red){
            mSwitch.setChecked(true);
        }else{
            mSwitch.setChecked(false);
        }

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    if(get_led_color()==led_off) {
                        set_led_color(led_red);
                        jnielc.ledmix(fb);
                        jnielc.seekstart();
                        jnielc.ledseek(seek_red, 50);
                        seekBar_red.setProgress(50);
                        seekBar_blue.setProgress(0);
                        seekBar_green.setProgress(0);
                        jnielc.seekstop();
                        Intent intent = new Intent("android.intent.action.ledctl");
                        intent.putExtra("led", led_red);
                        intent.putExtra("ledbrightness", 50);
                        sendBroadcast(intent);
                        // SystemProperteisProxy.set("persist.demo.ledswitch", "1");
                        Toast.makeText(MainActivity.this, "LED red !!!!!", Toast.LENGTH_SHORT).show();
                    }
                }else {
                   // SystemProperteisProxy.set("persist.demo.ledswitch", "0");
                    set_led_color(led_off);
                    Intent intent1 =new Intent("android.intent.action.ledctl");
                    intent1.putExtra("led",led_off);
                    sendBroadcast(intent1);
                    seekBar_green.setProgress(0);
                    seekBar_red.setProgress(0);
                    seekBar_blue.setProgress(0);
                    Toast.makeText(MainActivity.this, "LED off !!!!!", Toast.LENGTH_SHORT).show();
                    jnielc.ledoff(fb);
                }
            }

        });

    }

    //数值改变
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        // TODO Auto-generated method stub
        //if(get_led_color()==led_on){
            if (seekBar == seekBar_red) {
                jnielc.ledseek(seek_red, progress);
                brightness=progress;
            }
            if (seekBar == seekBar_green) {
                jnielc.ledseek(seek_green, progress);
                brightness=progress;
            }
            if (seekBar == seekBar_blue) {
                jnielc.ledseek(seek_blue, progress);
                brightness=progress;
            }
       // }
    }

    //开始拖动
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
        jnielc.seekstart();
    }

    //停止拖动
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
       jnielc.seekstop();
       if(seekBar == seekBar_red){
           set_led_brightness(brightness);
           Intent intent2 =new Intent("android.intent.action.ledctl");
           intent2.putExtra("led",led_red);
           intent2.putExtra("ledbrightness",brightness);
           sendBroadcast(intent2);
           Toast.makeText(MainActivity.this, "LED red !!!!!", Toast.LENGTH_SHORT).show();

           set_led_color(led_red);
           seekBar_blue.setProgress(0);
           seekBar_green.setProgress(0);
       }
        if (seekBar == seekBar_green) {
            set_led_brightness(brightness);
            Intent intent3 =new Intent("android.intent.action.ledctl");
            intent3.putExtra("led",led_green);
            intent3.putExtra("ledbrightness",brightness);
            sendBroadcast(intent3);
            Toast.makeText(MainActivity.this, "LED green !!!!!", Toast.LENGTH_SHORT).show();

            set_led_color(led_green);
            seekBar_blue.setProgress(0);
            seekBar_red.setProgress(0);
        }
        if (seekBar == seekBar_blue) {
            jnielc.ledseek(seek_blue, brightness);
            set_led_brightness(brightness);
            Intent intent4 =new Intent("android.intent.action.ledctl");
            intent4.putExtra("led",led_blue);
            intent4.putExtra("ledbrightness",brightness);
            sendBroadcast(intent4);
            Toast.makeText(MainActivity.this, "LED green !!!!!", Toast.LENGTH_SHORT).show();

            set_led_color(led_blue);
            seekBar_green.setProgress(0);
            seekBar_red.setProgress(0);
        }
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                mSwitch.setChecked(true);
            }
        }, 500);
    }

    private void set_led_color(int freq){
        SharedPreferences save_par = getSharedPreferences("addata", 0);
        SharedPreferences.Editor save_editor = save_par.edit();
        save_editor.putString("ledcolor", String.valueOf(freq));
        save_editor.commit();
    }

    private int get_led_color(){
        int value = 0;
        try {
            SharedPreferences save_par = getSharedPreferences("addata", 0);
            value = Integer.parseInt(save_par.getString("ledcolor", "0"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return value;
    }

    private void set_led_brightness (int freq){
        SharedPreferences save_par = getSharedPreferences("addata", 0);
        SharedPreferences.Editor save_editor = save_par.edit();
        save_editor.putString("ledbrightness ", String.valueOf(freq));
        save_editor.commit();
    }

    private int get_led_brightness (){
        int value = 0;
        try {
            SharedPreferences save_par = getSharedPreferences("addata", 0);
            value = Integer.parseInt(save_par.getString("ledbrightness ", "0"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return value;
    }
}
package com.shrikanthravi.chatviewlibrary;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.github.zagum.expandicon.ExpandIconView;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.recyclerview.animators.ScaleInBottomAnimator;

public class ChatActivity extends AppCompatActivity {

    HorizontalScrollView moreHSV;
    ExpandIconView expandIconView;
    Typeface regular,bold;
    FontChanger regularFontChanger,boldFontChanger;
    boolean more=false;
    RecyclerView chatRV;
    List<Message> messageList;
    MessageAdapter messageAdapter;
    EditText messageET;
    MaterialRippleLayout sendMRL,galleryMRL;
    CircleImageView profilePicCV;
    float profilePicElevation;
    int imagePickerRequestCode=10;
    List<Uri> mSelected;
    TextView usernameTV;
    TextView userStatusTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Changing the font throughout the activity
        regular = Typeface.createFromAsset(getAssets(), "fonts/product_san_regular.ttf");
        bold = Typeface.createFromAsset(getAssets(),"fonts/product_sans_bold.ttf");
        regularFontChanger = new FontChanger(regular);
        boldFontChanger = new FontChanger(bold);
        regularFontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));

        //Initialization start
        moreHSV = findViewById(R.id.moreLL);
        expandIconView = findViewById(R.id.expandIconView);
        chatRV = findViewById(R.id.chatRV);
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList,ChatActivity.this);
        messageET = findViewById(R.id.messageET);
        sendMRL = findViewById(R.id.sendMRL);
        profilePicCV = findViewById(R.id.profilePicCV);
        galleryMRL = findViewById(R.id.galleryMRL);
        usernameTV = findViewById(R.id.usernameTV);
        userStatusTV = findViewById(R.id.userStatusTV);
        //Initialization end

        expandIconView.setState(1,false);
        expandIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(more){
                    expandIconView.setState(1,true);
                    moreHSV.setVisibility(View.GONE);
                    more=false;
                }
                else{
                    expandIconView.setState(0,true);
                    moreHSV.setVisibility(View.VISIBLE);
                    more=true;
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        layoutManager.setStackFromEnd(true);
        chatRV.setLayoutManager(layoutManager);
        chatRV.setItemAnimator(new ScaleInBottomAnimator(new OvershootInterpolator(1f)));
        chatRV.setAdapter(messageAdapter);


        sendMRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(messageET.getText().toString().trim().length()!=0){
                    if(messageList.size()!=0) {
                        if (messageList.get(messageList.size() - 1).getType().equals("quick")) {
                            messageList.remove(messageList.size()-1);
                            messageAdapter.notifyItemRemoved(messageList.size());
                        }
                    }
                    messageList.add(new Message("RIGHT",messageET.getText().toString().trim(),getTime()));
                    messageAdapter.notifyItemInserted(messageList.size());
                    chatRV.smoothScrollToPosition(messageList.size()-1);
                    messageET.setText("");
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            messageList.add(new Message("LEFT",getRandomText(),getTime()));
                            messageAdapter.notifyItemInserted(messageList.size()-1);
                            chatRV.smoothScrollToPosition(messageList.size()-1);

                        }
                    },1000);
                }
                else{
                    messageET.setText("");
                }
            }
        });

        profilePicCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ChatActivity.this,ProfileActivity.class);
                Pair<View, String> p1 = Pair.create((View)profilePicCV, profilePicCV.getTransitionName());
                Pair<View, String> p2 = Pair.create((View)usernameTV, usernameTV.getTransitionName());
                Pair<View, String> p3 = Pair.create((View)userStatusTV, userStatusTV.getTransitionName());
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(ChatActivity.this,p1,p2,p3);
                startActivity(intent, optionsCompat.toBundle());

                /*Animation floatingAnim = AnimationUtils.loadAnimation(ChatActivity.this,R.anim.floating_anim);
                profilePicCV.setAnimation(floatingAnim);
                profilePicCV.startAnimation(floatingAnim);

                PopupMenu popup = new PopupMenu(ChatActivity.this,view, Gravity.END,0,R.style.MyPopupMenu);
                try {
                    Field[] fields = popup.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if ("mPopup".equals(field.getName())) {
                            field.setAccessible(true);
                            Object menuPopupHelper = field.get(popup);
                            Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                            Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                            setForceIcons.invoke(menuPopupHelper, true);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                popup.getMenuInflater().inflate(R.menu.profile_pic_popup, popup.getMenu());
                Menu menu = popup.getMenu();
                for(int i=0;i<menu.size();i++){
                    applyFontToMenuItem(menu.getItem(i));
                }

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        if(item.getTitle().toString().equals("View Profile")){
                            Intent intent = new Intent(ChatActivity.this,ProfileActivity.class);
                            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(ChatActivity.this, profilePicCV,profilePicCV.getTransitionName());
                            startActivity(intent, optionsCompat.toBundle());
                        }
                        return true;
                    }
                });

                popup.setOnDismissListener(new PopupMenu.OnDismissListener() {
                    @Override
                    public void onDismiss(PopupMenu menu) {
                        profilePicCV.clearAnimation();
                    }
                });


                popup.show();*/
            }
        });

        galleryMRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Matisse.from(ChatActivity.this)
                        .choose(MimeType.allOf())
                        .countable(true)
                        .maxSelectable(9)
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .imageEngine(new PicassoEngine())
                        .forResult(imagePickerRequestCode);
            }
        });


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == imagePickerRequestCode && resultCode == RESULT_OK) {
            mSelected = Matisse.obtainResult(data);
            if(mSelected.size()==1){

                messageList.add(new Message("RightImage","",getTime(),mSelected));
                messageAdapter.notifyItemInserted(messageList.size());
                chatRV.smoothScrollToPosition(messageList.size()-1);
            }

        }
    }

    //Getting time
    public String getTime(){
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd MMM yyyy HH:mm");
        String time = mdformat.format(calendar.getTime());
        return time;
    }



    public static String getRandomText() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(30);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    /*private void applyFontToMenuItem(MenuItem mi) {

        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypeFaceSpan("", regular, Color.BLACK), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }*/

}


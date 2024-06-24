package com.example.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView myrecycler;
    Myadapter myadapter;
    List<Contactinformation> sourcelist=new ArrayList<>();//展示数组
    List<Contactinformation> contactinformationslist = new ArrayList<>();//总数组
    Button button;

    List<Contactinformation> list1 = new ArrayList<>();       // all source

    public static boolean is_sorted = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=findViewById(R.id.button_menu);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPopupMenu(button);
            }
        });
        //初始页面的绘制
        myrecycler = findViewById(R.id.recyclerview);
        for (int i = 0; i < 5; i++) {
            Contactinformation contactinformation = new Contactinformation();
            // Tmp value defined
            String name = "姓名" + i;
            String number = "电话号码" + i;
            String sort= "默认";
            String email = "";
            String notes = "";

            // update contractinformation
            contactinformation.name = name;
            contactinformation.number = number;
            contactinformation.sort= sort;
            contactinformation.select=false;
            contactinformationslist.add(contactinformation);

            // update database
            Handledb hdb = new Handledb(getApplicationContext());
            String key = contactinformation.name + "--" + System.currentTimeMillis();
            hdb.updateValueByKey(key, name, number, sort, email, notes);
        }
        sourcelist=contactinformationslist;
        DividerItemDecoration mDivider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        myrecycler.addItemDecoration(mDivider);
        myadapter = new Myadapter();
        myrecycler.setAdapter(myadapter);
        EditText search=findViewById(R.id.search_input);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                myadapter.getFilter().filter(s.toString());
            }
        });
        myrecycler.post(new Runnable() {
            @Override
            public void run() {
                myadapter.setOnclick(new Myadapter.ClickInterface(){
                    @Override
                    public void OnItemClick(View v,int position){
                        showalterdialog(position);
                    }

                    @Override
                    public void OnImageviewClick(View view, int position) {
                        if(!sourcelist.get(position).IsSelect())
                        {
                            sourcelist.get(position).SetSelect(true);

                        }else {
                            sourcelist.get(position).SetSelect(false);
                        }
                    }
                });
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        myrecycler.setLayoutManager(layoutManager);
    }


    class Myadapter extends RecyclerView.Adapter<Myadapter.MyViewHoder> {
        private ClickInterface clickInterface;
        private static final int STATE_DEFAULT = 0;//默认状态
        int mEditMode = STATE_DEFAULT;
        @NonNull
        @Override
        public MyViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(MainActivity.this, R.layout.contactinformation, null);
            MyViewHoder myViewHoder = new MyViewHoder(view);
            return myViewHoder;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onBindViewHolder(@NonNull MyViewHoder holder, @SuppressLint("RecyclerView") int position) {
                Contactinformation contactinformation = sourcelist.get(position);
                holder.name.setText(contactinformation.name);
                holder.number.setText(contactinformation.number);
                holder.sort.setText(contactinformation.sort);
                if (contactinformation.IsSelect()) {
                    holder.check.setBackgroundResource(R.drawable.select);
                } else {
                    holder.check.setBackgroundResource(R.drawable.choose_no);
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickInterface.OnItemClick(v, position);
                    }
                });
                holder.check.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickInterface.OnImageviewClick(v, position);
                        myadapter.notifyDataSetChanged();
                    }
                });
            if (mEditMode == STATE_DEFAULT) {
                //默认不显示
                holder.check.setVisibility(View.GONE);
            } else {
                //显示   显示之后再做点击之后的判断
                holder.check.setVisibility(View.VISIBLE);
            };
        }

         public void setEditMode(int editMode){
             mEditMode = editMode;//刷新
             notifyDataSetChanged();
         };

        @Override
        public int getItemCount() {
            return sourcelist.size();
        }

        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String chatString=constraint.toString();
                    if(chatString.isEmpty()){
                        sourcelist=contactinformationslist;
                    }else{
                        List<Contactinformation> filtered=new ArrayList<>();
                        for(Contactinformation contactinformation:contactinformationslist){
                            if(contactinformation.name.contains(chatString) || contactinformation.number.contains(chatString) || contactinformation.sort.contains(chatString) ){
                                filtered.add(contactinformation);
                            }
                        }
                        sourcelist=filtered;
                    }
                    FilterResults filterResults=new FilterResults();
                    filterResults.values=sourcelist;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    sourcelist=(ArrayList<Contactinformation>)results.values;
                    notifyDataSetChanged();
                }
            };
        }



        public void setOnclick(ClickInterface clickInterface){
            this.clickInterface=clickInterface;
        }

        public interface ClickInterface{
            void OnItemClick(View view,int position);
            void OnImageviewClick(View view,int position);
        }

        class MyViewHoder extends RecyclerView.ViewHolder {
             public int i;
             TextView name;
             TextView number;
             TextView sort;
             ImageView check;
             public MyViewHoder(@NonNull View itemView) {
                 super(itemView);
                 name = itemView.findViewById(R.id.name_input);
                 number = itemView.findViewById(R.id.number_input);
                 sort = itemView.findViewById(R.id.sort);
                 check=itemView.findViewById(R.id.imageView2);
             }
         }

     }

    public void showalterdialog(int position){
        View click_alterdialog=this.getLayoutInflater().inflate(R.layout.ciick_alterdialog,null);
        TextView name_input=click_alterdialog.findViewById(R.id.name_input2);
        TextView number_input=click_alterdialog.findViewById(R.id.number_input);
        TextView sort_input=click_alterdialog.findViewById(R.id.sort_input);
        Contactinformation contactinformation = sourcelist.get(position);
        name_input.setText(contactinformation.name);
        number_input.setText(contactinformation.number);
        sort_input.setText(contactinformation.sort);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("详情");
        builder.setView(click_alterdialog);
        AlertDialog alertDialog =builder.create();
        alertDialog.show();
    }

    public  List<Contactinformation> sortFilter(String sortName){
        List<Contactinformation> temp = new ArrayList<>();
        for(Contactinformation contactinformation:list1)
        {
            if(contactinformation.sort.equals(sortName))
            {
                temp.add(contactinformation);
            }
        }

        return temp;
    }


    public void ShowPopupMenu(View view){
        PopupMenu popupMenu=new PopupMenu(this,view);
        popupMenu.getMenuInflater().inflate(R.menu.menu,popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (list1 != null && list1.isEmpty() &&
                        (item.getItemId() == R.id.two1 || item.getItemId() == R.id.two2 ||
                                item.getItemId() == R.id.two3 || item.getItemId() == R.id.two4 ||
                                item.getItemId() == R.id.two5)) {
                    list1.addAll(sourcelist);
                    is_sorted = true;
                }

                if(item.getItemId() == R.id.one2)
                {
                    myadapter.setEditMode(1);
                    showbutton();
                }
                if(item.getItemId() == R.id.one1)
                {
                    getContactinformation();
//                  addItem(contactinformation);
                }
                if(item.getItemId() == R.id.one5)
                {
                    Collections.sort(sourcelist);
                    myadapter.notifyDataSetChanged();
                }
                if(item.getItemId()==R.id.two1)
                {
                    sourcelist=sortFilter("默认");
                    myadapter.notifyDataSetChanged();
                }
                if(item.getItemId()==R.id.two2)
                {
                    sourcelist=sortFilter("亲人");
                    myadapter.notifyDataSetChanged();
                }
                if(item.getItemId()==R.id.two3)
                {

                    sourcelist=sortFilter("同事");
                    myadapter.notifyDataSetChanged();

                }
                if(item.getItemId()==R.id.two4)
                {
                    sourcelist=sortFilter("朋友");
                    myadapter.notifyDataSetChanged();
                }
                if(item.getItemId()==R.id.two5) //all
                {
                    sourcelist.clear();
                    sourcelist.addAll(list1);
                    myadapter.notifyDataSetChanged();
                    is_sorted = false;
                    list1.clear();
                }
                return true;
            }
        });
    }

    private void getContactinformation() {
        String[] sort =getResources().getStringArray(R.array.spinnerclass);
        Contactinformation contactinformation=new Contactinformation();
        View get_alterdialog=this.getLayoutInflater().inflate(R.layout.get_alterdialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("详情");
        Spinner spinner=get_alterdialog.findViewById(R.id.sort_input);
        EditText name_input=get_alterdialog.findViewById(R.id.name_input2);
        EditText number_input=get_alterdialog.findViewById(R.id.number_input);
        EditText email_input=get_alterdialog.findViewById(R.id.email_input);
        EditText notes_input=get_alterdialog.findViewById(R.id.notes_input);
        contactinformation.sort="默认";
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                contactinformation.sort=sort[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        builder.setPositiveButton("提交", new DialogInterface.OnClickListener() {
            @Override

            public void onClick(DialogInterface dialog, int which) {
                if(!name_input.getText().toString().isEmpty()&&!number_input.getText().toString().isEmpty()){
                    contactinformation.name=name_input.getText().toString();
                    contactinformation.number=number_input.getText().toString();
                    contactinformation.email_adress=email_input.getText().toString();
                    contactinformation.notes=notes_input.getText().toString();
                    addItem(contactinformation);
                }else{
                    Toast.makeText(getApplicationContext(),"姓名或电话号码不能为空",Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setView(get_alterdialog);
        AlertDialog alertDialog =builder.create();
        alertDialog.show();
    }

    private void addItem(Contactinformation contactinformation) {
        contactinformationslist.add(contactinformation);
        String name = contactinformation.name;
        String number = contactinformation.number;
        String sort = contactinformation.sort;
        String email = contactinformation.email_adress;
        String notes = contactinformation.notes;

        // update database
        Handledb hdb = new Handledb(getApplicationContext());
        String key = contactinformation.name + "--" + System.currentTimeMillis();
        hdb.updateValueByKey(key, name, number, sort, email, notes);

        myadapter.notifyDataSetChanged();
    }

    public void deleteItem(){
        for (int i=contactinformationslist.size()-1;i>=0;i--){
            if(contactinformationslist.get(i).IsSelect()){
                contactinformationslist.remove(i);
                Contactinformation tmp = contactinformationslist.get(i);
                String name = tmp.name;
                Handledb hdb = new Handledb(getApplicationContext());
                hdb.deleteDataByName(name);
            }
        }
        myadapter.notifyDataSetChanged();
    }
    public void showbutton(){
        Button btn_cancel;
        Button btn_comfirm;
        btn_cancel=findViewById(R.id.button_cancel);
        btn_comfirm=findViewById(R.id.button_confirm);
        btn_cancel.setVisibility(View.VISIBLE);
        btn_comfirm.setVisibility(View.VISIBLE);
    }
    public void hidebutton(){
        Button btn_cancel;
        Button btn_comfirm;
        btn_cancel=findViewById(R.id.button_cancel);
        btn_comfirm=findViewById(R.id.button_confirm);
        btn_cancel.setVisibility(View.GONE);
        btn_comfirm.setVisibility(View.GONE);
    }
    public void cancel_click(View view){
        myadapter.setEditMode(0);
        hidebutton();
    }
    public void confirm_click(View view){
//      myadapter.notifyDataSetChanged();
        deleteItem();
        myadapter.setEditMode(0);
        hidebutton();
    }
}
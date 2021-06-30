package com.example.proiectfinal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity{

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;

    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserID;

    private ProgressDialog loader;

    private String key = "";
    private String task;
    private String description;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*toolbar = findViewById(R.id.homeToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Todo List App");*/

        mAuth = FirebaseAuth.getInstance();



        recyclerView = findViewById(R.id.recycleView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        loader = new ProgressDialog(this);


        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("tasks").child(onlineUserID);


        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addTask();
            }
        });


        /*
        //MessageReciver
        Thread myThread = new Thread(new MyServerThread());
        myThread.start();
        try {
            Socket s = new Socket("192.168.1.3", 5050);

            DataInputStream dis = new DataInputStream(s.getInputStream());
            String msg = dis.readUTF();

            Toast.makeText(getApplicationContext(),"Hey",Toast.LENGTH_LONG).show();
        }catch (Exception e){
            e.printStackTrace();
        }
        */


        /*add=findViewById(R.id.add);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_name=edit.getText().toString();

                if(txt_name.isEmpty()){
                    Toast.makeText(MainActivity.this, "Introdu o activitate", Toast.LENGTH_SHORT).show();
                }else{
                    FirebaseDatabase.getInstance().getReference().child("Activitati").setValue(txt_name);
                }
            }
        });

        ArrayList<String> list = new ArrayList<>();
        ArrayAdapter adapter =new ArrayAdapter<String>(this, R.layout.list_item, list);
        listView.setAdapter(adapter);

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Activitati");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for(DataSnapshot snapshot1:dataSnapshot.getChildren()){
                    list.add(snapshot1.getValue().toString());

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/


    }
    private void addTask(){
        AlertDialog.Builder myDialog =new AlertDialog.Builder(this);
        LayoutInflater inflater=LayoutInflater.from(this);

        View myView = inflater.inflate(R.layout.input_file,null);
        myDialog.setView(myView);

        AlertDialog dialog=myDialog.create();
        dialog.setCancelable(false);// face sa nu dispara cand apesi langa

        final EditText task = myView.findViewById(R.id.task);
        final EditText description = myView.findViewById(R.id.description);
        Button save = myView.findViewById(R.id.saveBtn);
        Button cancel = myView.findViewById(R.id.cancelBtn);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mTask = task.getText().toString().trim();
                String mDescription = description.getText().toString().trim();
                String id = reference.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());

                if(TextUtils.isEmpty(mTask)){
                    task.setError("Task Required");
                    return;
                }
                if(TextUtils.isEmpty(mDescription)){
                    task.setError("Description required");
                    return;
                }else{
                    loader.setMessage("Adding your data");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    Model model = new Model(mTask,mDescription, id, date);
                    reference.child(id).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Task.ul a fost adaugat cu succes", Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                            } else {
                                String error = task.getException().toString();
                                Toast.makeText(MainActivity.this, "Failed: " + error, Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                            }
                        }
                    });
                }
            }
        });
        dialog.show();
    }

    @Override
    protected void onStart(){
        super.onStart();
        FirebaseRecyclerOptions<Model> options = new FirebaseRecyclerOptions.Builder<Model>().setQuery(reference, Model.class).build();

        FirebaseRecyclerAdapter<Model, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Model, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Model model) {
                holder.setDate(model.getDate());
                holder.setTask(model.getTask());
                holder.setDescription(model.getDescription());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        key = getRef(position).getKey();
                        task = model.getTask();
                        description = model.getDescription();

                        updateTask();
                    }
                });


            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
               View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieverd_layout, parent, false);
               return new MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

        }
        public void setTask(String task){
            TextView taskTexView = mView.findViewById(R.id.taskTv);
            taskTexView.setText(task);

        }
        public void setDescription(String desc){
            TextView descTextView = mView.findViewById(R.id.descriptionTv);
            descTextView.setText(desc);
        }

        public void setDate(String data){
            TextView dateTextView = mView.findViewById(R.id.dateTv);
            dateTextView.setText(data);
        }
    }

    private void updateTask(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.update_data, null);
        myDialog.setView(view);

        AlertDialog dialog = myDialog.create();

        EditText mTask = view.findViewById(R.id.mEditTextTask);
        EditText mDescription = view.findViewById(R.id.mEditTextDescription);

        mTask.setText(task);
        mTask.setSelection(task.length());

        mDescription.setText(description);
        mDescription.setSelection(description.length());

        Button delButton = view.findViewById(R.id.btnDelete);
        Button updateButton = view.findViewById(R.id.btnUpdate);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task = mTask.getText().toString().trim();
                description = mDescription.getText().toString().trim();

                String date = DateFormat.getDateInstance().format(new Date());

                Model model = new Model(task, description, key, date);


                reference.child(key).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull  Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Update cu succes.", Toast.LENGTH_SHORT).show();
                        }else
                        {
                            Toast.makeText(MainActivity.this, "Update esuat.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.dismiss();

            }
        });

        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull  Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Task sters cu succes", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this, "Eroare la stergere", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.dismiss();
            }
        });

        dialog.show();
    }


    //INCEPUT INCERCARE SERVER

    /*

    //MessageReciver
    class MyServerThread implements Runnable{

        Socket s;
        ServerSocket ss;
        InputStreamReader isr;
        BufferedReader bufferedReader;
        String message;
        TextView textView;
        Handler h = new Handler();
        @Override
        public void run() {
            try{
                s = new Socket("192.168.0.143", 5050);
                while (true){

                    isr = new InputStreamReader(s.getInputStream());
                    bufferedReader = new BufferedReader(isr);
                    message = bufferedReader.readLine();

                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }


    public void sendMessage(View v)
    {
        String mTask = task.getText().toString().trim();
        com.example.betterme.message.MessageSender messageSender = new com.example.betterme.message.MessageSender();
        messageSender.execute(mTask);
    }



*/
    //FINAL INCERCARE SERVER



}
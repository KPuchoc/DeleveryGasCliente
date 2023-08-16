package com.example.sportwearstoreolivos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sportwearstoreolivos.Common.Common;
import com.example.sportwearstoreolivos.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {
 EditText edtPhone,edtPassword;
 Button btnSignIn;
 TextView txtSlogan;
 CheckBox ckbRemenber;
 TextView txtForgotPwd;
 FirebaseDatabase database;
DatabaseReference table_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        edtPassword=findViewById(R.id.edtPasswor);
        edtPhone=findViewById(R.id.edtPhone);
        btnSignIn=findViewById(R.id.BtnSignIn);
        ckbRemenber=findViewById(R.id.cbkRemenber);
        txtForgotPwd=findViewById(R.id.txtForgotPwd);

        txtSlogan=findViewById(R.id.txtSignIn);


        Paper.init(this);

   //     Typeface face =Typeface.createFromAsset(getAssets(),"fonts/NABILA.TTF");
    //    txtSlogan.setTypeface(face);

       database=FirebaseDatabase.getInstance();
       table_user = database.getReference("User");
       
       txtForgotPwd.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v) {
               showForgotPwsDialog();
           }
       });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                if (Common.isConnectedToInternet(getBaseContext())) {

                        //Guardar Usuario :v
                    if(ckbRemenber.isChecked())
                    {
                        Paper.book().write(Common.USER_KEY,edtPhone.getText().toString());
                        Paper.book().write(Common.PWD_KEY,edtPassword.getText().toString());
                    }

                    final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                    mDialog.setMessage("Por Favor Espere");
                    mDialog.show();

                    table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                mDialog.dismiss();

                                //consulta con la base de datos
                                User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                                user.setPhone(edtPhone.getText().toString());

                                //Condicion inicio de usuario
                                if (user.getPassword().equals(edtPassword.getText().toString())) {
                                    Toast.makeText(SignIn.this, "Inicio de Sesion Exitoso", Toast.LENGTH_SHORT).show();
                                    Intent homeIntent = new Intent(SignIn.this, Home.class);
                                    Common.currentUser = user;
                                    startActivity(homeIntent);
                                    finish();

                                    table_user.removeEventListener(this);
                                } else {
                                    Toast.makeText(SignIn.this, "Contraseña Incorreta", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                mDialog.dismiss();
                                Toast.makeText(SignIn.this, "El Usuario no existe en la Base de datos", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else
                {
                    Toast.makeText(SignIn.this, "Por favor Chequea tu Conexion", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

        });
    }

    private void showForgotPwsDialog() {

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password");
        builder.setMessage("Enter your secure code");

        LayoutInflater inflater=this.getLayoutInflater();
        View forgot_view=inflater.inflate(R.layout.forgot_password,null);

        builder.setView(forgot_view);
        builder.setIcon(R.drawable.ic_secury);

        MaterialEditText edtPhone=(MaterialEditText) forgot_view.findViewById(R.id.edtPhone);
        MaterialEditText edtSecureCode=(MaterialEditText) forgot_view.findViewById(R.id.edtSecureCode);

        builder.setPositiveButton("YES",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user=dataSnapshot.child(edtPhone.getText().toString())
                                .getValue(User.class);

                        if(user.getSecureCode().equals(edtSecureCode.getText().toString()))
                            Toast.makeText(SignIn.this, "Your password :"+user.getPassword(), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(SignIn.this, "Worng secure code!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();

    }
}
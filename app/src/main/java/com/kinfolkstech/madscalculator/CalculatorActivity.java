package com.kinfolkstech.madscalculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.Stack;

public class CalculatorActivity extends AppCompatActivity {

    FirebaseUser currentuser;
    FirebaseAuth firebaseAuth;
    private EditText editText1,editText2;
    private Button buttonEqual,buttonHistory;
    private int count=0;
    private String expression="";
    private String text="";
    private int result=0;
    PastData pastData;


    ///Checking the user logged in or not ///
    @Override
    protected void onStart() {
        super.onStart();
        if(currentuser==null)
        {
            startActivity(new Intent(CalculatorActivity.this,MainActivity.class));
        }
    }


    /// Menu Creation for logout button///
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.taskbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        switch (id)
        {
            case R.id.logout:
               firebaseAuth.signOut();
               startActivity(new Intent(CalculatorActivity.this,MainActivity.class));
               return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        firebaseAuth=FirebaseAuth.getInstance();
        currentuser=firebaseAuth.getCurrentUser();
        editText1=findViewById(R.id.edittext1);
        editText2=findViewById(R.id.edittext2);
        buttonEqual=findViewById(R.id.operatorEquals);
        pastData=new PastData();
        buttonHistory=findViewById(R.id.btnHistory);


        ///Equal Button on Click Function///

        buttonEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editText2.length()!=0)
                {
                    text= editText2.getText().toString();
                    expression=editText1.getText().toString()+text;
                    editText1.setText(expression);
                }
                editText2.setText("");
                if(expression.length()==0)
                {
                    expression="0.0";
                }
                else {
                    //Passing the expression to solve function//
                    result=solve(expression);

                    //Storing the data to Firebase Realtime Database data upto 10 values/user
                    count=count+1;
                    if(count>=0 && count<=10)
                    {
                        PastData p=new PastData(expression,result,count);
                        FirebaseDatabase.getInstance().getReference("History").child(firebaseAuth.getUid())
                                .child(String.valueOf(count)).setValue(p).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                Toast.makeText(CalculatorActivity.this, "Data Saved", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Toast.makeText(CalculatorActivity.this, "Error Occured : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                editText1.setText(""+result);
            }
        });

        //History button on click listener - Navigate to ViewHistory Activity
        buttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CalculatorActivity.this,ViewHistory.class));
            }
        });
    }

    //On Click function for all the buttons except History and Equals
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.num0:
                editText2.setText(editText2.getText()+"0");
                break;
            case R.id.num1:
                editText2.setText(editText2.getText()+"1");
                break;
            case R.id.num2:
                editText2.setText(editText2.getText()+"2");
                break;
            case R.id.num3:
                editText2.setText(editText2.getText()+"3");
                break;
            case R.id.num4:
                editText2.setText(editText2.getText()+"4");
                break;
            case R.id.num5:
                editText2.setText(editText2.getText()+"5");
                break;
            case R.id.num6:
                editText2.setText(editText2.getText()+"6");
                break;
            case R.id.num7:
                editText2.setText(editText2.getText()+"7");
                break;
            case R.id.num8:
                editText2.setText(editText2.getText()+"8");
                break;
            case R.id.num9:
                editText2.setText(editText2.getText()+"9");
                break;
            case R.id.clearText:
                editText1.setText("");
                editText1.setText("");
                expression="";
                break;
            case R.id.backspace:
                text=editText2.getText().toString();
                if(text.length()>0)
                {
                    editText2.setText(text.substring(0,text.length()-1));
                }
                break;
            case R.id.operatorPlus:
                operationSelected("+");
                break;
            case R.id.operatorSub:
                operationSelected("-");
                break;
            case R.id.operatorMul:
                operationSelected("*");
                break;
            case R.id.operatorDiv:
                operationSelected("/");
                break;
        }
    }


    //Function to add the operator selected to the expression//
    public void operationSelected(String operator)
    {
        if(editText2.length()!=0)
        {
            String text=editText2.getText().toString();
            editText1.setText(editText1.getText()+text+operator);
            editText2.setText("");

        }
        else
        {
            String text=editText1.getText().toString();
            if(text.length()>0)
            {
                editText1.setText(text+operator);
            }
        }
    }


    //Static function to solve the expression
    // Implementation using stack

    public static int solve(String expression)
    {

        char[] tokens=expression.toCharArray();
        Stack<Integer> numbers=new Stack<Integer>();
        Stack<Character> operators=new Stack<Character>();

        for(int i=0;i<tokens.length;i++)
        {
            if(tokens[i]==' ')
                continue;
            if(tokens[i]>='0' && tokens[i]<='9')
            {
                StringBuffer stringBuffer=new StringBuffer();
                while(i<tokens.length && tokens[i]>='0' && tokens[i]<='9')
                    stringBuffer.append(tokens[i++]);
                numbers.push(Integer.parseInt(stringBuffer.toString()));
                i--;
            }

            else if(tokens[i]=='+'||tokens[i]=='-'||tokens[i]=='*'||tokens[i]=='/')
            {
                while(!operators.empty() && hasPrecedence(tokens[i],operators.peek()))
                {
                    numbers.push(applyOperation(operators.pop(),numbers.pop(),numbers.pop()));
                }
                operators.push(tokens[i]);
            }
        }
        while(!operators.empty())
        {
            numbers.push(applyOperation(operators.pop(),numbers.pop(),numbers.pop()));
        }
        return numbers.pop();
    }

    //Function to check the Operator Precedence according to MADS rule

    public static boolean hasPrecedence(
            char op1, char op2)
    {
        if ((op1 == '*' ) && (op2 == '/' || op2 == '-'|| op1 == '+'))
            return false;
        else if ((op1 == '+' ) && (op2 == '/' || op2 == '-'))
            return false;
        else if ((op1 == '/' ) && (op2 == '-'))
            return false;
        else
            return true;
    }

    //Fuction to apply to operation of stack values

    public static int applyOperation(char op, int b, int a)
    {
        switch (op)
        {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0)
                    throw new
                            UnsupportedOperationException("Cannot divide by zero");
                return a / b;
        }
        return 0;
    }

}
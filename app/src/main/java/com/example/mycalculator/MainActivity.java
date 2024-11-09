package com.example.mycalculator;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    private TextView equationDisplay;
    private String equation = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        equationDisplay = findViewById(R.id.equationDisplay);

        setNumberButtonListeners();
        setOperatorButtonListeners();
    }

    private void setNumberButtonListeners() {
        int[] numberButtonIDs = {R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
                R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9,
                R.id.buttonDot};

        View.OnClickListener listener = view -> {
            Button button = (Button) view;
            equation += button.getText();
            equationDisplay.setText(equation);
        };

        for (int id : numberButtonIDs) {
            findViewById(id).setOnClickListener(listener);
        }
    }

    private void setOperatorButtonListeners() {
        findViewById(R.id.buttonAdd).setOnClickListener(view -> appendOperator("+"));
        findViewById(R.id.buttonSubtract).setOnClickListener(view -> appendOperator("-"));
        findViewById(R.id.buttonMultiply).setOnClickListener(view -> appendOperator("*"));
        findViewById(R.id.buttonDivide).setOnClickListener(view -> appendOperator("/"));

        findViewById(R.id.buttonClear).setOnClickListener(view -> {
            equation = "";
            equationDisplay.setText("0");
        });

        findViewById(R.id.buttonDelete).setOnClickListener(view -> {
            if (!equation.isEmpty()) {
                equation = equation.substring(0, equation.length() - 1);
                equationDisplay.setText(equation.isEmpty() ? "0" : equation);
            }
        });

        findViewById(R.id.buttonEqual).setOnClickListener(view -> calculateResult());
    }

    private void appendOperator(String operator) {
        if (!equation.isEmpty() && !isOperator(equation.charAt(equation.length() - 1))) {
            equation += operator;
            equationDisplay.setText(equation);
        }
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private void calculateResult() {
        try {
            double result = evaluateExpression(equation);
            equationDisplay.setText(String.valueOf(result));
            equation = String.valueOf(result);
        } catch (Exception e) {
            equationDisplay.setText("Error");
            equation = "";
        }
    }

    private double evaluateExpression(String expression) {
        // Using two stacks: one for numbers and one for operators
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();
        int i = 0;

        while (i < expression.length()) {
            char c = expression.charAt(i);

            // If current character is a digit or a decimal point, extract the full number
            if (Character.isDigit(c) || c == '.') {
                StringBuilder num = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    num.append(expression.charAt(i));
                    i++;
                }
                numbers.push(Double.parseDouble(num.toString()));
                continue;
            }

            // If current character is an operator, handle operator precedence
            if (isOperator(c)) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(c)) {
                    numbers.push(applyOperator(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(c);
            }
            i++;
        }

        // Apply remaining operators
        while (!operators.isEmpty()) {
            numbers.push(applyOperator(operators.pop(), numbers.pop(), numbers.pop()));
        }

        // The final number is the result
        return numbers.pop();
    }

    private int precedence(char operator) {
        if (operator == '+' || operator == '-') return 1;
        if (operator == '*' || operator == '/') return 2;
        return -1;
    }

    private double applyOperator(char operator, double b, double a) {
        switch (operator) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/':
                if (b == 0) throw new ArithmeticException("Division by zero");
                return a / b;
        }
        return 0;
    }
}

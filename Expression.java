package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	StringTokenizer info = new StringTokenizer(expr, delims);
        int index = 0;

        while (info.hasMoreTokens()) {

            String token = info.nextToken();
            index = expr.indexOf(token, index);
            int endindex = index + token.length() - 1;

            if (token.matches("\\d+")) {
                continue;
            }

            if (endindex + 1 < expr.length() && expr.charAt(endindex + 1) == '[') {
                Array tokenized1 = new Array(token);
                if (!arrays.contains(tokenized1)) {
                    arrays.add(tokenized1);
                    //make new objects var
                }
            } else {
                Variable tokenized2 = new Variable(token);
                if (!vars.contains(tokenized2)) {
                    vars.add(tokenized2);
                }
            }
            index = endindex;
        }
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	// following line just a placeholder for compilation
        float returnValue = calculate(expr, vars, arrays);
        return returnValue;
   
    }
    
    private static int findValue(String vari, ArrayList<Variable> vars) {//finds numerical value of vars
        Variable var = new Variable(vari);
        int index = vars.indexOf(var);
        return vars.get(index).value;
    }

    private static int findArrayValue(String arri, ArrayList<Array> arrays, float indexOfValue) {
        Array arr = new Array(arri);
        int index = arrays.indexOf(arr);
        int[] values = arrays.get(index).values;
        return values[(int) indexOfValue];
    }

    private static float operations(String ch, float first, float second) {
        if (ch.equals("+") || ch.equals("-") || ch.equals("/") || ch.equals("*")) {//if it is operator this does the operation
            switch (ch) {
                case "+":
                    return (first + second);
                case "-":
                    return (first - second);
                case "/":
                    return (first / second);
                case "*":
                    return (first * second);
            }
        }
        return 0;
    }

    private static int getEndIndex(String substring, int openParenIndex, char open, char close){
        int parenCounter = 0;//counts if there is even number
        int count = openParenIndex; //keeps count of each char in String
        int endIndex = 0;
        for(int i = openParenIndex; i<substring.length(); i++){
            count++;
            if(substring.charAt(i) == open){
                parenCounter++;
            }
            if(substring.charAt(i) == close){
                parenCounter--;
            }
            if(parenCounter == 0 && substring.charAt(i) == close){
                endIndex = count-1;
                break;
            }
        }
        return endIndex;
    }

    private static float calculate(String substring, ArrayList<Variable> vars, ArrayList<Array> arrays) {
        //take substring and make an arraylist
        //tokenize the string
        //add constants, operators, Converted vars into it
        //come across open paren or array add to arraylist and call caluclate on it
        ArrayList<String> substr = new ArrayList<String>(20);
        int counter = 0;
        String prevToken ="";
        float insideValue = 0;
        StringTokenizer info = new StringTokenizer(substring, delims, true);

        while (info.hasMoreTokens()) {
            String token = info.nextToken();
            if (token.equals(" ")) {
                counter = counter + token.length();
                continue;
            } else if (token.matches("\\d+")) {
                substr.add(token);//add corresponding int to arraylist instead of string
                counter = counter + token.length();
            } else if (token.equals("+") || token.equals("-") || token.equals("/") || token.equals("*")) {
                substr.add(token);
                counter = counter + token.length();
            } else if (vars.contains(new Variable(token))){
                substr.add(Integer.toString(findValue(token, vars)));
                counter = counter + token.length();
            } else if (token.equals("(")){
                int openParenIndex = counter;
                counter = counter + token.length();
                int endIndex = getEndIndex(substring, openParenIndex, '(', ')');
                counter = 0;
                String newSubstring = substring.substring(openParenIndex + 1, endIndex);
                substr.add(Float.toString((calculate(newSubstring, vars, arrays)))); //call calculate
                substring = substring.substring(endIndex+1);
                info = new StringTokenizer(substring, delims, true);
            } else if(token.equals("[")){
                int openParenIndex = counter;
                counter = counter + token.length();
                int endIndex = getEndIndex(substring, openParenIndex, '[', ']');
                counter = 0;
                String newSubstring = substring.substring(openParenIndex + 1, endIndex);
                insideValue = (calculate(newSubstring, vars, arrays));
                substr.add(Integer.toString(findArrayValue(prevToken, arrays, insideValue)));
                substring = substring.substring(endIndex+1);
                info = new StringTokenizer(substring, delims, true);
            }
            else{
                prevToken = token; //prevToken is arraylist object name
                counter = counter + token.length();
            }
        }

        for(int i = 0; i<substr.size(); i++){
            if (substr.get(i).equals("*") || substr.get(i).equals("/")) {
                String operand1 = substr.get(i-1);
                String operand2 = substr.get(i+1);
                String operator = substr.get(i);
                float newoperand1 = Float.parseFloat(operand1);
                float newoperand2 = Float.parseFloat(operand2);

                float res = operations(operator, newoperand1, newoperand2);
                //delete the 3 tokens from arraylist
                //insert res in its place
                substr.set(i-1, Float.toString(res));
                substr.remove(i);
                substr.remove(i);
                i = i-1;
            }
        }
        for(int i = 0; i<substr.size(); i++){
            if (substr.get(i).equals("+") || substr.get(i).equals("-")) {
                String operand1 = substr.get(i-1);
                String operand2 = substr.get(i+1);
                String operator = substr.get(i);
                float newoperand1 = Float.parseFloat(operand1);
                float newoperand2 = Float.parseFloat(operand2);

                float res = operations(operator, newoperand1, newoperand2);
                //delete the 3 tokens from arraylist
                //insert res in its place
                substr.set(i-1, Float.toString(res));
                substr.remove(i);
                substr.remove(i);
                i = i-1;
            }
        }

        return Float.parseFloat(substr.get(0));
    }
    
}

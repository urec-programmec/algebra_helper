import javax.script.ScriptException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class VirtualMachine {

    public static Double Calculate(String expression) throws ScriptException {
        List<Token> tokens = Tokening(expression);

        Preprocessing(tokens);

        tokens = PostfixConversion(tokens);

        return CalculateExpression(tokens);
    }

    private static ArrayList<Token> Tokening(String expression){
        ArrayList<Token> tokens = new ArrayList<>();

        var exprChar = expression.toCharArray();
        List<String> exprList = new ArrayList<>();
        String x, temp = "";
        TokenType temptype = null;
        Integer tokenIndex = -1;

        for (char c : exprChar) exprList.add(Character.toString(c));

        exprList.add("X");

        for (int i = 0; i < exprList.size(); i++){
            x = exprList.get(i);

            if (x.equals(" "))
                continue;

            if (temp == ""){
                temp = x;
                temptype = setType(temp, i);
                tokenIndex = i;
            }
            else{
                if (temptype == TokenType.NUMBER && isNumber(temp + x, false) ||
                    temptype == TokenType.OPERATOR && isOperator(temp + x, false) ||
                    temptype == TokenType.OPEN_BR && isOpenBr(temp + x) ||
                    temptype == TokenType.CLOSE_BR && isClosedBr(temp + x)){

                    temp += x;
                    continue;
                }
                else if (isNumber(x, false) || isOperator(x, false) || isOpenBr(x) || isClosedBr(x) || x.equals("X")){
                    if (temptype == TokenType.NUMBER && isNumber(temp, true) ||
                        temptype == TokenType.OPERATOR && isOperator(temp, true) && isVisable(temp)||
                        temptype == TokenType.OPEN_BR && isOpenBr(temp) ||
                        temptype == TokenType.CLOSE_BR && isClosedBr(temp)){

                        tokens.add(new Token(temptype, temp, tokenIndex));
                        temp = x;
                        temptype = setType(temp, tokenIndex);
                        tokenIndex = i;
                    }
                    else
                        throw new RuntimeException("There is incorrect token on index " + tokenIndex);
                }
                else
                    throw new RuntimeException("There is unknown symbol on index " + i);
            }
        }

        System.out.println("Tokening (list of tokens):");
        System.out.println("[ " + tokens.stream().map(Token::getContains).collect(Collectors.joining(", ")) + " ]");

        return tokens;
    }

    private static void Preprocessing(List<Token> tokens){
        Token before = null, now = null;
//        Integer offset = 0;

        for (int i = 0; i < tokens.size(); i++){
            now = tokens.get(i);
//            now.setPosition(now.getPosition() + offset);
            if (now.getType().equals(TokenType.OPEN_BR) && before != null && before.getType().equals(TokenType.NUMBER)) {
                tokens.add(i, new Token(TokenType.OPERATOR, Loader.Operators.stream()
                        .filter(k -> k.getId().equals("multiplication"))
                        .collect(Collectors.toList()).get(0).getSign(), now.getPosition()));
//                offset++;
            }
            else if (Loader.Operators.stream().filter(k -> k.getId().equals("addition")).collect(Collectors.toList()).get(0).getSign().equals(now.getContains()) &&
                    before != null &&
                    !(before.getType().equals(TokenType.CLOSE_BR) || before.getType().equals(TokenType.NUMBER))){
                now.setContains(Loader.Operators.stream().filter(k -> k.getId().equals("plus")).collect(Collectors.toList()).get(0).getSign());
            }
            else if (Loader.Operators.stream().filter(k -> k.getId().equals("subtraction")).collect(Collectors.toList()).get(0).getSign().equals(now.getContains()) &&
                    before != null &&
                    !(before.getType().equals(TokenType.CLOSE_BR) || before.getType().equals(TokenType.NUMBER))){
                now.setContains(Loader.Operators.stream().filter(k -> k.getId().equals("minus")).collect(Collectors.toList()).get(0).getSign());
            }
            before = now;
        }

        System.out.println("Preprocessing (list of tokens):");
        System.out.println("[ " + tokens.stream().map(Token::getContains).collect(Collectors.joining(", ")) + " ]");
    }

    private static List<Token> PostfixConversion(List<Token> tokens){
        Token token;
        Operator operator, tempOperator;
        Stack<Token> stack = new Stack<>();
        List<Token> outTokenList = new ArrayList<>();

        for (int i = 0; i < tokens.size(); i++){
            token = tokens.get(i);
            operator = getOperatorByToken(token);

            if (token.getType().equals(TokenType.NUMBER))
                outTokenList.add(token);
            else if (token.getType().equals(TokenType.OPEN_BR) || operator != null && operator.getType().equals(OperatorType.PREFIX))
                stack.push(token);
            else if (token.getType().equals(TokenType.CLOSE_BR)){
                while (!stack.isEmpty() && !stack.peek().getType().equals(TokenType.OPEN_BR))
                    outTokenList.add(stack.pop());
                if (stack.isEmpty())
                    throw new RuntimeException("There is incorrect arrangement of brackets on index " + token.getPosition());
                stack.pop();
            }
            else if (operator != null && operator.getCountArguments() == 2){
                if (!stack.isEmpty()) {
                    tempOperator = getOperatorByToken(stack.peek());
                    while (tempOperator != null && !stack.isEmpty() && (tempOperator.getType().equals(OperatorType.PREFIX) ||
                            tempOperator.getPriority() < operator.getPriority() ||
                            tempOperator.getPriority() == operator.getPriority() && tempOperator.getAssociatio().equals(AssociatioType.LEFT))) {
                        outTokenList.add(stack.pop());
                        if (!stack.isEmpty())
                            tempOperator = getOperatorByToken(stack.peek());
                    }
                }
                stack.add(token);
            }
        }

        while (!stack.isEmpty()){
            token = stack.pop();
            if (token.getType().equals(TokenType.OPEN_BR))
                throw new RuntimeException("There is incorrect arrangement of brackets on index " + token.getPosition());
            outTokenList.add(token);
        }

        System.out.println("Postfix conversion (list of tokens):");
        System.out.println("[ " + outTokenList.stream().map(Token::getContains).collect(Collectors.joining(", ")) + " ]");

        return outTokenList;
    }

    private static Double CalculateExpression(List<Token> tokens) throws ScriptException {
        Stack<Double> stack = new Stack<>();
        Token token;
        Operator operator;
        List<Double> args = new ArrayList<>();

        for (int i = 0; i< tokens.size(); i++){
            token = tokens.get(i);
            if (token.getType().equals(TokenType.NUMBER))
                stack.add(Double.parseDouble(token.getContains()));
            else {
                operator = getOperatorByToken(token);
                args.clear();
                for (int j = 0; j < operator.getCountArguments(); j++) {
                    try {
                        args.add(stack.pop());
                    }
                    catch (EmptyStackException e){
                        throw new RuntimeException("There is incorrect arrangement of operators on index " + token.getPosition());
                    }
                }
                Collections.reverse(args);
                try {
                    stack.add(operator.operate(args));
                }
                catch (ArithmeticException e){
                    throw new ArithmeticException(e.getMessage() + " on index " + token.getPosition());
                }
            }
        }
        if (stack.size() == 0)
            throw new RuntimeException("There is an empty expression");
        if (stack.size() > 1)
            throw new RuntimeException("There is too less operators for number " + stack.peek() + " on index " + tokens.stream()
                    .filter(i -> i.getContains().equals(stack.peek().toString()))
                    .collect(Collectors.toList()).get(0).getPosition());

        return stack.peek();
    }


    private static TokenType setType(String x, Integer index){
        if (isNumber(x, false))
            return TokenType.NUMBER;
        else if (isOperator(x, false))
            return TokenType.OPERATOR;
        else if (isOpenBr(x))
            return TokenType.OPEN_BR;
        else if (isClosedBr(x))
            return TokenType.CLOSE_BR;
        else if (x.equals("X"))
            return null;
        else
            throw new RuntimeException("There is the unknown symbol on index " + index);
    }

    private static Boolean isVisable(String operator){
        return Loader.Operators.stream()
                .filter(i -> i.getSign().equals(operator))
                .collect(Collectors.toList()).get(0).getVisable();
    }

    private static Boolean isNumber(String number, Boolean isCorrectly){
        return isCorrectly ? number.matches("^(\\d)+(\\.\\d+)?$") : number.matches("^(\\d)+(\\.\\d?)?$");
    }
    private static Boolean isOperator(String operator, Boolean isCorrectly){
        List<String> operators = Loader.Operators.stream()
                .map(Operator::getSign)
                .collect(Collectors.toList());

        for (String op : operators)
            if (op.contains(operator) && !isCorrectly || op.equals(operator) && isCorrectly)
                return true;

        return false;
    }
    private static Boolean isOpenBr(String br){
        return br.equals("(");
    }
    private static Boolean isClosedBr(String br){
        return br.equals(")");
    }

    private static Operator getOperatorByToken(Token token){
        List<Operator> operators = Loader.Operators.stream().filter(i -> i.getSign().equals(token.getContains())).collect(Collectors.toList());
        return operators.size() == 0 ? null : operators.get(0);
    }

}

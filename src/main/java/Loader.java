import com.typesafe.config.ConfigFactory;

import java.util.ArrayList;
import java.util.List;

public class Loader {

    public static List<Operator> Operators;

    public static void load(){
        Operators = new ArrayList<>();

        var configApp = ConfigFactory.load();
        var conf = configApp.getConfigList("operators");

        Operator operator;
        String separator = configApp.getString("separator");

        String sign;
        Integer priority;
        Boolean visable;
        Integer countArguments;
        OperatorType type;
        String function;
        String id;
        AssociatioType associatio;

        for (var cf : conf){
            sign = cf.getConfig("operator").getString("sign");
            priority = Integer.parseInt(cf.getConfig("operator").getString("priority"));
            visable = Boolean.parseBoolean(cf.getConfig("operator").getString("visable"));
            countArguments = Integer.parseInt(cf.getConfig("operator").getString("countArguments"));
            type = OperatorType.valueOf(cf.getConfig("operator").getString("type"));
            function = cf.getConfig("operator").getString("function");
            id = cf.getConfig("operator").getString("id");
            associatio = AssociatioType.valueOf(cf.getConfig("operator").getString("associatio"));

            if (function == ""){
                function = "(" + separator + sign + separator + ")";
            }
            else{
                function += "(";
                for (int i = 0; i < countArguments; i++){
                    function += separator;
                    if (i != countArguments - 1)
                        function += ",";
                }
                function += ");";
            }

            operator = new Operator(sign, priority, visable, countArguments, type, function, separator, id, associatio);
            Operators.add(operator);
        }

//        +  -> 2 -> a + b  -> (a + b);         -> 5
//        -  -> 2 -> a - b  -> (a - b);         -> 5
//        *  -> 2 -> a * b  -> (a * b);         -> 4
//        /  -> 2 -> a / b  -> (a / b);         -> 4
//        %  -> 2 -> a % b  -> (a % b);         -> 4
//        ** -> 2 -> a ** b -> Math.pow(a, b);  -> 3
//        sqrt -> 1 -> sqrt a -> Math.sqrt(a);  -> 2
//        &  -> 1 -> & a    -> (-1) * (a);      -> 1
//        ?  -> 1 -> ? a    -> (a);             -> 1

//
    }
}

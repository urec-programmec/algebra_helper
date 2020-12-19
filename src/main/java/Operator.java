import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Operator {
    private final String sign;
    private final Integer priority;
    private Boolean visable;

    private Integer countArguments;
    private OperatorType type;

    private String function;
    private String separator;
    private String id;
    private AssociatioType associatio;



    public Operator(String sign, Integer priority, Boolean visable, Integer countArguments, OperatorType type, String function, String separator, String id, AssociatioType associatio) {
        this.sign = sign;
        this.priority = priority;
        this.visable = visable;
        this.countArguments = countArguments;
        this.type = type;
        this.function = function;
        this.separator = separator;
        this.id = id;
        this.associatio = associatio;
    }

    public String getSign() {
        return sign;
    }

    public int getCountArguments() {
        return countArguments;
    }

    public int getPriority() {
        return priority;
    }

    public OperatorType getType() {
        return type;
    }

    public Boolean getVisable() {
        return visable;
    }

    public void setType(OperatorType type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public AssociatioType getAssociatio() {
        return associatio;
    }

    public Double operate(List<Double> args) throws ScriptException {
        String function = this.function;
        for(int i = 0; i < this.countArguments; i++)
            function = function.replaceFirst(this.separator, args.get(i).toString());

        Double result = Double.parseDouble((new ScriptEngineManager().getEngineByName("JavaScript").eval(function)).toString());

        if (result.isNaN() || result.isInfinite())
            throw new ArithmeticException("This operation cannot be calculated");
        return result;
    }

}

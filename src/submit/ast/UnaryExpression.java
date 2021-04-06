package submit.ast;

import java.util.List;

public class UnaryExpression extends Expression{

    private List<String> ops;
    private Node factor;

    public UnaryExpression(List<String> ops, Node factor){
        this.ops = ops;
        this.factor = factor;
    }

    @Override
    public void toCminus(StringBuilder builder, String prefix) {
//        builder.append(prefix);

        for(String op : this.ops){
            builder.append(op);
        }

        factor.toCminus(builder, "");
    }
}

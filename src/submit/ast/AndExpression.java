package submit.ast;

import java.util.List;

public class AndExpression extends Expression{

    private List<UnaryRelExpression> unarys;

    public AndExpression(List<UnaryRelExpression> unarys){
        this.unarys = unarys;
    }

    @Override
    public void toCminus(StringBuilder builder, String prefix) {
        builder.append(prefix);

        //Adds and removes the first and expression
        UnaryRelExpression first = this.unarys.get(0);
        first.toCminus(builder, prefix);
//        builder.append(String.format("%s%s", prefix, first));
        this.unarys.remove(0);

        //Appends the rest of the AndExpressions
        for(UnaryRelExpression e : this.unarys){
            builder.append(" || ");
            e.toCminus(builder, "");
//            builder.append(e);
        }
    }
}

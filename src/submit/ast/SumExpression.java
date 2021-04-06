package submit.ast;

import java.util.List;

public class SumExpression extends Expression{

    private List<TermExpression> termExpr;
    private List<String> ops;

    public SumExpression(List<TermExpression> termExpr, List<String> ops){
        this.termExpr = termExpr;
        this.ops = ops;
    }

    @Override
    public void toCminus(StringBuilder builder, String prefix) {
//        builder.append(prefix);

        for(int i = 0; i < termExpr.size(); i++){
            termExpr.get(i).toCminus(builder, "");

            try{
                builder.append(String.format(" %s ", ops.get(i)));
            }catch (IndexOutOfBoundsException e){
                break;
            }
        }
    }
}

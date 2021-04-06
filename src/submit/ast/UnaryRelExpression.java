package submit.ast;

import parser.CminusParser;

public class UnaryRelExpression extends Expression{

    private int bangs;
    private RelExpression relExpr;

    public UnaryRelExpression(int bangs, RelExpression relExpr){
        this.bangs = bangs;
        this.relExpr = relExpr;
    }

    @Override
    public void toCminus(StringBuilder builder, String prefix) {
        builder.append(prefix);

        //Appends bangs
        while(this.bangs > 0){
            builder.append("!");
            this.bangs--;
        }

        this.relExpr.toCminus(builder, prefix);
    }
}

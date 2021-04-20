package submit.ast;

public class ExpressionStatement extends AbstractNode implements Statement{

    private Expression expr;

    public ExpressionStatement(Expression expr){
        this.expr = expr;
    }

    @Override
    public void toCminus(StringBuilder builder, String prefix) {
        if(expr != null) {
            expr.toCminus(builder, prefix);
        }

        builder.append(";\n");
    }
}

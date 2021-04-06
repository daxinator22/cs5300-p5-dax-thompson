package submit.ast;

public class While implements Statement{

    private Expression simpleExpr;
    private Statement stmt;

    public While(Expression simpleExpr, Statement stmt){
        this.simpleExpr = simpleExpr;
        this.stmt = stmt;
    }

    @Override
    public void toCminus(StringBuilder builder, String prefix) {
        builder.append(String.format("%swhile", prefix));


        builder.append("(");
        simpleExpr.toCminus(builder, "");
        builder.append(")\n");

        if (stmt instanceof CompoundStatment) {
            stmt.toCminus(builder, prefix);
        } else {
            stmt.toCminus(builder, prefix + " ");
        }
    }
}

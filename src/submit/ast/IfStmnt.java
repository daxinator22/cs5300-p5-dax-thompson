package submit.ast;

import java.util.List;

public class IfStmnt implements Statement {

    private Expression simpleExpr;
    private Statement trueStmt;
    private Statement falseStmt;

    public IfStmnt(Expression simpleExpr, Statement trueStmt, Statement falseStmt){
        this.simpleExpr = simpleExpr;
        this.trueStmt = trueStmt;
        this.falseStmt = falseStmt;
    }

    @Override
    public void toCminus(StringBuilder builder, String prefix) {
        builder.append(String.format("%sif(", prefix));

        //Adds expression
        simpleExpr.toCminus(builder, "");
        builder.append(")\n");

        //Adds true statement
        if(trueStmt instanceof CompoundStatment) {
            trueStmt.toCminus(builder, prefix);
        }
        else{
            trueStmt.toCminus(builder, prefix + " ");
        }

        //Adds false statement
        if(falseStmt != null){
            builder.append(String.format("%selse\n", prefix));
            if(falseStmt instanceof CompoundStatment) {
                falseStmt.toCminus(builder, prefix);
            }
            else{
                falseStmt.toCminus(builder, prefix + " ");
            }
        }

    }
}

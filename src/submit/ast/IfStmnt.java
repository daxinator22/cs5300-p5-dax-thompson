package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;

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

    @Override
    public MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
        //Get boolean value of statement
        MIPSResult condition = simpleExpr.toMIPS(code, data, symbolTable, regAllocator);

        //Adds if statement branch
        //If the condition is not true, will jump to data label
        String dataLabel = regAllocator.getUniqueLabel();
        code.append(String.format("# Checking if condition\n"));
        code.append(String.format("bne %s $zero %s\n", condition.getRegister(), dataLabel));
        code.append("\n");

        //True condition
        code.append("# True statement\n");
        trueStmt.toMIPS(code, data, symbolTable, regAllocator);

        //Jumping to next code
        String nextDataLabel = regAllocator.getUniqueLabel();
        code.append("# Jumping to code after statement\n");
        code.append(String.format("j %s", nextDataLabel));

        //False condition
        code.append("# False condition\n");
        code.append(String.format("%s: \n", dataLabel));
        code.append("\n");

        //Continue on to next lines of code
        code.append(String.format("%s: \n", nextDataLabel));
        code.append("\n");

        return MIPSResult.createVoidResult();
    }
}

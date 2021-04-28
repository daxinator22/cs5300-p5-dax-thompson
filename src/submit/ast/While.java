package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;

import javax.print.DocFlavor;

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

    @Override
    public MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
        String startLoop = regAllocator.getUniqueLabel();
        code.append(String.format("# Beginning while loop\n"));
        code.append(String.format("%s: \n", startLoop));

        MIPSResult result = simpleExpr.toMIPS(code, data, symbolTable, regAllocator);
        String exitLoop = regAllocator.getUniqueLabel();

        code.append(String.format("bne %s $zero %s\n", result.getRegister(), exitLoop));
        stmt.toMIPS(code, data, symbolTable, regAllocator);

        code.append(String.format("j %s\n", startLoop));
        code.append("\n");

        code.append(String.format("%s: \n", exitLoop));
        code.append("\n");

        return MIPSResult.createVoidResult();
    }
}

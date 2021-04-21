package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;

public class ExpressionStatement implements Statement{

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

    @Override
    public MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
        expr.toMIPS(code, data, symbolTable, regAllocator);

        return MIPSResult.createVoidResult();
    }


}

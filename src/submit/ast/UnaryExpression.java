package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;

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
        builder.append(prefix);

        for(String op : this.ops){
            builder.append(op);
        }

        factor.toCminus(builder, "");
    }

    @Override
    public MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
        factor.toMIPS(code, data, symbolTable, regAllocator);

        return MIPSResult.createVoidResult();
    }
}

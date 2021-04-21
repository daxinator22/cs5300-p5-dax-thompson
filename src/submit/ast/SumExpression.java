package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;

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
        builder.append(prefix);

        for(int i = 0; i < termExpr.size(); i++){
            termExpr.get(i).toCminus(builder, "");

            try{
                builder.append(String.format(" %s ", ops.get(i)));
            }catch (IndexOutOfBoundsException e){
                break;
            }
        }
    }

    @Override
    public MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
        MIPSResult result = MIPSResult.createVoidResult();

        for(Node term : termExpr){
            result = term.toMIPS(code, data, symbolTable, regAllocator);
        }

        return result;
    }
}

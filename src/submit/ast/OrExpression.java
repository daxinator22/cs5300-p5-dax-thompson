package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;

import java.util.List;

public class OrExpression extends Expression{

    private List<AndExpression> ands;

    public OrExpression(List<AndExpression> ands){
        this.ands = ands;
    }

    @Override
    public void toCminus(StringBuilder builder, String prefix) {
        builder.append(prefix);

        //Adds and removes the first and expression
        AndExpression first = this.ands.get(0);
        first.toCminus(builder, prefix);
        this.ands.remove(0);

        //Appends the rest of the AndExpressions
        for(AndExpression e : this.ands){
            builder.append(" || ");
            e.toCminus(builder, "");
        }
    }

    @Override
    public MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
        MIPSResult result = MIPSResult.createVoidResult();

        for(Node and : ands){
            result = and.toMIPS(code, data, symbolTable, regAllocator);
        }

        return result;
    }
}

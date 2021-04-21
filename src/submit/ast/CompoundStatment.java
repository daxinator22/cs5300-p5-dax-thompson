package submit.ast;

import parser.CminusParser;
import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;

import java.util.List;

public class CompoundStatment implements Statement{

    private List<VarDeclaration> vars;
    private List<Statement> statements;

    public CompoundStatment(List<VarDeclaration> vars, List<Statement> statements){
        this.vars = vars;
        this.statements = statements;
    }

    @Override
    public void toCminus(StringBuilder builder, String prefix) {

        builder.append(String.format("%s{\n", prefix));

        for(VarDeclaration v: this.vars){
            v.toCminus(builder, prefix + "  ");
        }

        for(Statement s : statements){
            s.toCminus(builder, prefix + "  ");
        }

        builder.append(String.format("%s}\n", prefix));
    }

    @Override
    public MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
        for(Node stmt : statements){
            stmt.toMIPS(code, data, symbolTable, regAllocator);
        }

        return MIPSResult.createVoidResult();
    }
}

package submit.ast;

import parser.CminusParser;
import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;

import java.util.List;

public class CompoundStatment implements Statement{

    private List<VarDeclaration> vars;
    private List<Statement> statements;
    private SymbolTable symbolTable;

    public CompoundStatment(List<VarDeclaration> vars, List<Statement> statements, SymbolTable symbolTable){
        this.vars = vars;
        this.statements = statements;
        this.symbolTable = symbolTable;
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
        code.append(String.format("# Symbol table is %s bytes\n", this.symbolTable.getSize()));

        for(Node stmt : statements){
            stmt.toMIPS(code, data, this.symbolTable, regAllocator);
        }

        return MIPSResult.createVoidResult();
    }
}

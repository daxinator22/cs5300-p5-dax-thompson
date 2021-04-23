package submit.ast;

import parser.CminusParser;
import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolInfo;
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

    public SymbolTable getSymbolTable(){return this.symbolTable;}

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

        //Lists everything in the symbol table
        for(String id : this.symbolTable.getKeys()){
            SymbolInfo param = this.symbolTable.find(id);
            code.append(String.format("#  %s at %s\n", param, param.getOffset()));
        }
        code.append("\n");
        code.append(String.format("# Entering new scope, adjusting stack pointer %s bytes\n", symbolTable.getSize()));
        code.append(String.format("addi $sp $sp -%s\n", symbolTable.getSize()));

        for(Node stmt : statements){
            stmt.toMIPS(code, data, this.symbolTable, regAllocator);
        }

        code.append(String.format("# Leaving scope, adjusting stack pointer %s bytes\n", symbolTable.getSize()));
        code.append(String.format("addi $sp $sp %s\n", symbolTable.getSize()));

        return MIPSResult.createVoidResult();
    }
}

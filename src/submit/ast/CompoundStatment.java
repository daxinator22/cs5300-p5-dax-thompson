package submit.ast;

import parser.CminusParser;

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
        builder.append("{\n");

        for(VarDeclaration v: this.vars){
            v.toCminus(builder, prefix);
        }

        for(Statement s : statements){
            s.toCminus(builder, prefix);
        }

        builder.append("}\n");
    }
}

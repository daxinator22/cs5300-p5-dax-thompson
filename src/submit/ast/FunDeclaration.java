package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;

import java.util.List;

public class FunDeclaration implements Declaration, Node {

    private VarType type;
    private String id;
    private List<Param> params;
    private CompoundStatment stmt;

    public FunDeclaration(VarType type, String id, List<Param> params, CompoundStatment stmt){
        this.type = type;
        this.id = id;
        this.params = params;
        this.stmt = stmt;
    }
    @Override
    public void toCminus(StringBuilder builder, String prefix) {
        builder.append(prefix);
        builder.append("\n");

        if(type == null){
            builder.append("void");
        }
        else{
            builder.append(type.toString());
        }

        builder.append(String.format(" %s(", this.id));     //Appends type and id

        try {
            //Gets and removes first param
            Param first = params.get(0);
            first.toCminus(builder, "");
            params.remove(0);

            for (Param p : this.params) {
                builder.append(", ");
                p.toCminus(builder, "");
            }
        } catch(IndexOutOfBoundsException e){}

        builder.append(")\n");

        stmt.toCminus(builder, prefix);
    }

    @Override
    public MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {

        code.append(String.format("# Creating function call for %s\n", this.id));
        code.append(String.format("%s:\n\n", this.id));

        for(Param param : params){
            param.toMIPS(code, data, stmt.getSymbolTable(), regAllocator);
        }

        stmt.toMIPS(code, data, symbolTable, regAllocator);

        //Main will always exit
        if(!this.id.equals("main")){
            code.append(String.format("# Returning to function\n"));
            code.append(String.format("jr $ra\n"));
            code.append("\n");
        }

        return MIPSResult.createVoidResult();
    }
}

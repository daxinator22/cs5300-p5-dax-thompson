package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;

import javax.swing.text.html.ImageView;

public class Immutable implements Node{

    private Node part;

    public Immutable(Node part){
        this.part = part;
    }

    @Override
    public void toCminus(StringBuilder builder, String prefix) {
//        builder.append(prefix);

        if(part instanceof Expression){
            builder.append("(");
            part.toCminus(builder, "");
            builder.append(")");
        }
        else{
            part.toCminus(builder, "");
        }
    }

    @Override
    public MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
        part.toMIPS(code, data, symbolTable, regAllocator);

        return MIPSResult.createVoidResult();
    }
}

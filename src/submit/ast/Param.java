package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolInfo;
import submit.SymbolTable;

public class Param implements Declaration, Node{

    private VarType type;
    private String id;
    private Integer size;

    public Param(VarType type, String id, Integer size){
        this.type = type;
        this.id = id;
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public VarType getType() {
        return type;
    }

    @Override
    public void toCminus(StringBuilder builder, String prefix) {
        builder.append(prefix);

        String arrayFomrat = "";

        try{
            if(size.intValue() == 0){
                arrayFomrat = "[]";
            }
            else{
                arrayFomrat = String.format("[%s]", size.intValue());
            }
        } catch (NullPointerException e){

        }

        builder.append(String.format("%s %s%s", type.toString(), id, arrayFomrat));
    }

    @Override
    public MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {

        SymbolInfo param = symbolTable.find(this.id);

        code.append(String.format("# Loading value of %s from %s from $sp\n", this.id, param.getOffset()));

        return MIPSResult.createVoidResult();
    }
}

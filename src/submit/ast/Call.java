package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;

import java.util.List;

public class Call implements Node{

    private String id;
    private List<Expression> exprs;

    public Call(String id, List<Expression> exprs){
        this.id = id;
        this.exprs = exprs;
    }

    @Override
    public void toCminus(StringBuilder builder, String prefix) {
//        builder.append(prefix);

        builder.append(id);
        builder.append("(");

        //Adds expressions as parameters
        try{
            Expression first = exprs.get(0);
            first.toCminus(builder, "");
            exprs.remove(0);

        }catch (IndexOutOfBoundsException e){

        }

        for(Expression e : this.exprs){
            builder.append(", ");
            e.toCminus(builder, prefix);
        }

        builder.append(")");
    }

    @Override
    public MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {

        if(this.id.equals("println")){
            //Since this is a print, we know there is only one arg
            MIPSResult result = exprs.get(0).toMIPS(code, data, symbolTable, regAllocator);

            code.append(String.format("# Adding print statement\n"));
            code.append("li $v0 4\n");

            //Setting params for syscall
            if(result != null) {
                if (result.getRegister() != null) {
                    code.append(String.format("lw $a0 %s\n", result.getRegister()));
                } else if (result.getAddress() != null) {
                    code.append(String.format("la $a0 %s\n", result.getAddress()));
                }
            }

            code.append("syscall\n\n");

        }

        return MIPSResult.createVoidResult();
    }
}

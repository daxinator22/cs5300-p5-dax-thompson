package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolInfo;
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

            //Determines which syscall to use
            int syscall = 4;
            if(result.getType() == VarType.INT){
                syscall = 1;
            }
            code.append(String.format("li $v0 %s\n", syscall));

            //Setting params for syscall
            if(result != null) {
                if (result.getRegister() != null) {

                    code.append(String.format("move $a0 %s\n", result.getRegister()));
                } else if (result.getAddress() != null) {
                    code.append(String.format("la $a0 %s\n", result.getAddress()));
                }
            }

            code.append("syscall\n");

            printNewLine(code);

        }
        else{
            //Checks to see if return address is in symbol table
            SymbolInfo returnAddress = null;

            if(symbolTable.contains("$ra")){
                returnAddress = symbolTable.find("$ra");
            }
            else {
                returnAddress = new SymbolInfo("$ra", null, false);
                symbolTable.addSymbol("$ra", returnAddress);
            }

            //Saves return address
            String register = regAllocator.getT();
            code.append(String.format("# Storing return address\n"));
            code.append(String.format("move %s $ra\n", register));
            code.append(String.format("sw %s %s($sp)\n", register, returnAddress.getOffset()));
            code.append("\n");

            code.append(String.format("# Jumping to %s\n", this.id));
            code.append(String.format("jal %s\n", this.id));
            code.append("\n");

            //Loads return address
            code.append(String.format("# Loading return address\n"));
            code.append(String.format("lw %s %s($sp)\n", register, returnAddress.getOffset()));
            code.append(String.format("move $ra %s\n", register));
            code.append("\n");
        }

        regAllocator.clearAll();

        return MIPSResult.createVoidResult();
    }

    private void printNewLine(StringBuilder code){
        code.append("\n");
        code.append("# Adding new line\n");
        code.append("li $v0 4\n");
        code.append("la $a0 newline\n");
        code.append("syscall\n");
        code.append("\n");
    }
}

package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;

public class Expression implements Node{

    private Mutable mutable;
    private String operator;
    private Expression expr;

    public Expression(){

    }

     public Expression(Mutable mutable, String operator, Expression expr){
         this.mutable = mutable;
         this.operator = operator;
         this.expr = expr;
     }

    @Override
    public void toCminus(StringBuilder builder, String prefix) {
        builder.append(prefix);

        if(mutable == null && operator == null){

            //In this case, there is only a SimpleExpression
            this.expr.toCminus(builder, "");
        }
        else {
            //Appending mutable and operator
            this.mutable.toCminus(builder, "");
            builder.append(String.format(" %s", this.operator));

            //Appends expression
            if(this.expr != null){

                //If there is a mutable, then there is no need for a prefix
                this.expr.toCminus(builder, " ");
            }
        }
    }

    @Override
    public MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
        if(operator == null) {
            return expr.toMIPS(code, data, symbolTable, regAllocator);
        }

        if(expr == null){

        }
        MIPSResult leftHand = mutable.toMIPS(code, data, symbolTable, regAllocator);


        //Get right hand value
        MIPSResult rightHand = expr.toMIPS(code, data, symbolTable, regAllocator);

        //Store right hand value to stack
        int offset = symbolTable.find(mutable.getId()).getOffset();
        code.append(String.format("# Saving new value of %s to stack\n", mutable.getId()));
        if(operator.equals("=")) {
            code.append(String.format("sw %s %s($sp)\n", rightHand.getRegister(), offset));
        }
        regAllocator.clearAll();

        return leftHand;
    }
}

package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;

import java.util.ArrayList;
import java.util.List;

public class SumExpression extends Expression{

    private List<TermExpression> termExpr;
    private List<String> ops;

    public SumExpression(List<TermExpression> termExpr, List<String> ops){
        this.termExpr = termExpr;
        this.ops = ops;
    }

    @Override
    public void toCminus(StringBuilder builder, String prefix) {
        builder.append(prefix);

        for(int i = 0; i < termExpr.size(); i++){
            termExpr.get(i).toCminus(builder, "");

            try{
                builder.append(String.format(" %s ", ops.get(i)));
            }catch (IndexOutOfBoundsException e){
                break;
            }
        }
    }

    @Override
    public MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
        ArrayList<MIPSResult> registers = new ArrayList<>();

        for(Node term : termExpr){
            registers.add(term.toMIPS(code, data, symbolTable, regAllocator));
        }

        MIPSResult result = registers.get(0);
        registers.remove(0);

        if(result.getRegister() == null){
            String reg = regAllocator.getT();

            //Loading address result into register
            code.append(String.format("# Loading address result into register\n"));
            code.append(String.format("lw %s %s"));

//            MIPSResult.createRegisterResult()
        }

        for(String op : ops){
            if(op.equals("+")) {
//                if(result.) {
                    code.append(String.format("add %s %s %s\n", result.getRegister(), result.getRegister(), registers.get(0).getRegister()));
//                }
            }
            else{
                code.append(String.format("sub %s %s %s\n", result.getRegister(), result.getRegister(), registers.get(0).getRegister()));
            }
            registers.remove(0);
        }


        return result;
    }
}

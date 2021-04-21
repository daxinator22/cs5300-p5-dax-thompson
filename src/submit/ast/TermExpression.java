package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;

import java.util.ArrayList;
import java.util.List;

public class TermExpression extends Expression{

    private List<UnaryExpression> unarys;
    private List<String> ops;

    public TermExpression(List<UnaryExpression> unarys, List<String> ops){

        this.unarys = unarys;
        this.ops = ops;

    }

    @Override
    public void toCminus(StringBuilder builder, String prefix) {

        builder.append(prefix);

        for(int i = 0; i < unarys.size(); i++){
            unarys.get(i).toCminus(builder, "");

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

        for(Node unary : unarys){
            registers.add(unary.toMIPS(code, data, symbolTable, regAllocator));
        }

        MIPSResult result = registers.get(0);
        registers.remove(result);

        for(String op : ops){
            if(op.equals("*")){
                code.append(String.format("mul %s %s %s\n", result.getRegister(), result.getRegister(), registers.get(0).getRegister()));
                registers.remove(0);
            }
            else{
                code.append(String.format("div %s %s %s\n", result.getRegister(), result.getRegister(), registers.get(0).getRegister()));
                registers.remove(0);
            }
        }

        return result;
    }
}

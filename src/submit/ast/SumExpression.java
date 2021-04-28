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

        if(result.getRegister() == null && result.getAddress() != null){
            result = loadFromAddress(code, regAllocator, result);
        }

        for(String op : ops){
            String command = "";
            if(op.equals("+")) {
                command = "add";
            }
            else{
                command = "sub";
            }

            MIPSResult compResult = registers.get(0);
            if(compResult.getRegister() == null && compResult.getAddress() != null) {
                compResult = loadFromAddress(code, regAllocator, compResult);
            }

            code.append(String.format("%s %s %s %s\n", command, result.getRegister(), result.getRegister(), compResult.getRegister()));
            registers.remove(0);
        }


        return result;
    }

    private MIPSResult loadFromAddress(StringBuilder code, RegisterAllocator regAllocator, MIPSResult compResult){
        String reg = regAllocator.getT();
        code.append(String.format("# Loading result into %s\n", reg));
        code.append(String.format("lw %s %s\n", reg, compResult.getAddress()));

        compResult = MIPSResult.createRegisterResult(reg, compResult.getType());

        return compResult;
    }
}

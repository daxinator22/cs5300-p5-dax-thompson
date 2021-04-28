package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;

import java.util.ArrayList;
import java.util.List;

public class RelExpression extends Expression{

    private List<SumExpression> sums;
    private List<String> ops;

    public RelExpression(List<SumExpression> sums, List<String> ops){
        this.sums = sums;
        this.ops = ops;
    }

    @Override
    public void toCminus(StringBuilder builder, String prefix) {

//        builder.append(prefix);

        for(int i = 0; i < this.sums.size(); i++){
            SumExpression sum = this.sums.get(i);
            sum.toCminus(builder, "");

            //Appends operator
            try{
                String op = this.ops.get(i);
                builder.append(String.format(" %s ", op));
            }
            catch (IndexOutOfBoundsException e){
                break;
            }
        }

    }

    @Override
    public MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
        ArrayList<MIPSResult> results = new ArrayList<>();

        for(Node sum : sums){
            results.add(sum.toMIPS(code, data, symbolTable, regAllocator));
        }

        MIPSResult result = results.get(0);
        results.remove(0);
        for(String op : ops){
            MIPSResult compResult = results.get(0);
            results.remove(0);

            code.append(String.format("# Comparing %s %s %s\n", result.getRegister(), op, compResult.getRegister()));
            if(op.equals(">")){
                code.append(String.format("slt %s %s %s\n", result.getRegister(), result.getRegister(), compResult.getRegister()));
            }
            else if(op.equals("<")){
                code.append(String.format("addi %s %s 1\n", result.getRegister(), result.getRegister()));
                code.append(String.format("slt %s %s %s\n", result.getRegister(), compResult.getRegister(), result.getRegister()));
            }
            else if(op.equals("==")){
                String compResultReg = regAllocator.getT();
                code.append(String.format("slt %s %s %s\n", compResultReg, result.getRegister(), compResult.getRegister()));
                code.append(String.format("slt %s %s %s\n", compResultReg, compResult.getRegister(), result.getRegister()));

                code.append(String.format("move %s %s\n", result.getRegister(), compResultReg));
                regAllocator.clear(compResultReg);
            }
            else if(op.equals("<=")){
                code.append(String.format("slt %s %s %s\n", result.getRegister(), compResult.getRegister(), result.getRegister()));
            }

            regAllocator.clear(compResult.getRegister());
        }

        return result;
    }
}

package submit.ast;

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
}

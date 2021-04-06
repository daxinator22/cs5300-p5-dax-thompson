package submit.ast;

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
}

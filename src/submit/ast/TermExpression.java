package submit.ast;

import java.util.List;

public class TermExpression extends Expression{

    private List<String> unarys;
    private List<String> ops;

    public TermExpression(List<String> unarys, List<String> ops){

        this.unarys = unarys;
        this.ops = ops;

    }

    @Override
    public void toCminus(StringBuilder builder, String prefix) {

        builder.append(prefix);

        for(int i = 0; i < unarys.size(); i++){
            builder.append(unarys.get(i));

            try{
                builder.append(String.format(" %s ", ops.get(i)));
            }catch (IndexOutOfBoundsException e){
                break;
            }
        }

    }
}

package submit.ast;

public class Break extends AbstractNode implements Statement{

    public Break(){

    }

    @Override
    public void toCminus(StringBuilder builder, String prefix) {
        builder.append(String.format("%sbreak;\n", prefix));
    }
}

package submit;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import parser.CminusBaseVisitor;
import parser.CminusParser;
import submit.ast.*;

import java.awt.image.CropImageFilter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ASTVisitor extends CminusBaseVisitor<Node> {
    private final Logger LOGGER;
    private SymbolTable symbolTable;

    public ASTVisitor(Logger LOGGER) {
        this.LOGGER = LOGGER;
    }

    public SymbolTable getSymbolTable(){return this.symbolTable;}

    private VarType getVarType(CminusParser.TypeSpecifierContext ctx) {
        final String t = ctx.getText();
        return (t.equals("int")) ? VarType.INT : (t.equals("bool")) ? VarType.BOOL : VarType.CHAR;
    }

    private VarType getFunType(CminusParser.TypeSpecifierContext ctx) {
        final String t = ctx.getText();

        if(t.equals("void")){
            return null;
        }
        return getVarType(ctx);
    }

    @Override public Node visitProgram(CminusParser.ProgramContext ctx) {
        symbolTable = new SymbolTable();
        List<Declaration> decls = new ArrayList<>();
//        decls.add((Declaration) visitDeclaration(ctx.declaration(ctx.declaration().size() - 1)));
        for (CminusParser.DeclarationContext d : ctx.declaration()) {
            decls.add((Declaration) visitDeclaration(d));
//            System.out.println(d.getText());
        }
//        decls.remove(decls.size() - 1);

        LOGGER.info(String.format("PARENT %s", symbolTable.toString()));
        return new Program(decls);
    }

    @Override public Node visitVarDeclaration(CminusParser.VarDeclarationContext ctx) {
        for (CminusParser.VarDeclIdContext v : ctx.varDeclId()) {
            String id = v.ID().getText();
//            LOGGER.fine("Var ID: " + id);
        }
//        return null;

        VarType type = getVarType(ctx.typeSpecifier());
        List<String> ids = new ArrayList<>();
        List<Integer> arraySizes = new ArrayList<>();
        for (CminusParser.VarDeclIdContext v : ctx.varDeclId()) {
            String id = v.ID().getText();
            ids.add(id);
            symbolTable.addSymbol(id, new SymbolInfo(id, type, false));
            if (v.NUMCONST() != null) {
                arraySizes.add(Integer.parseInt(v.NUMCONST().getText()));
            } else {
                arraySizes.add(-1);
            }
        }
        final boolean isStatic = false;
        Node var = new VarDeclaration(type, ids, arraySizes, isStatic);
        StringBuilder test = new StringBuilder();
        var.toCminus(test, "");

//        LOGGER.fine(String.format("New varDeclaration: %s", test.toString()));

        return var;
    }

    @Override public Node visitReturnStmt(CminusParser.ReturnStmtContext ctx) {
        if (ctx.expression() != null) {
            return new Return((Expression) visitExpression(ctx.expression()));
        }
        return new Return(null);
    }

    @Override public Node visitConstant(CminusParser.ConstantContext ctx) {
        final Node node;
        if (ctx.NUMCONST() != null) {
            node = new NumConstant(Integer.parseInt(ctx.NUMCONST().getText()));
        } else if (ctx.CHARCONST() != null) {
            node = new CharConstant(ctx.CHARCONST().getText().charAt(0));
        } else if (ctx.STRINGCONST() != null) {
            node = new StringConstant(ctx.STRINGCONST().getText());
        } else {
            node = new BoolConstant(ctx.getText().equals("true"));
        }
        return node;
    }

    // TODO Uncomment and implement whatever methods make sense
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitDeclaration(CminusParser.DeclarationContext ctx) {
        if(ctx.getChild(0) instanceof CminusParser.FunDeclarationContext){
            return visitFunDeclaration(ctx.funDeclaration());
        }
//        LOGGER.fine(ctx.varDeclaration().toString());
        return visitVarDeclaration(ctx.varDeclaration());
    }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitVarDeclId(CminusParser.VarDeclIdContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitFunDeclaration(CminusParser.FunDeclarationContext ctx) {
        symbolTable.addSymbol(ctx.ID().toString(), new SymbolInfo(ctx.ID().toString(), this.getFunType(ctx.typeSpecifier()), true));
        SymbolTable funTable = symbolTable.createChild();

        //Finds the parameters
        ArrayList<Param> params = new ArrayList<>();
        List<CminusParser.ParamContext> parent = ctx.param();
        for(CminusParser.ParamContext c : parent){
            Param param = (Param) visitParam(c);
            params.add(param);

            //Creates new symbol table entry
            funTable.addSymbol(param.getId(), (new SymbolInfo(param.getId(), param.getType(), false)));
        }

        //Processing statements
        Statement statement = (Statement) visitStatement(ctx.statement());

        LOGGER.info(String.format("NEW %s", funTable.toString()));

        return new FunDeclaration(getFunType(ctx.typeSpecifier()), ctx.ID().toString(), params, statement);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitTypeSpecifier(CminusParser.TypeSpecifierContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitParam(CminusParser.ParamContext ctx) {
        Integer size = null;

        //Determines the size of the array
        if (ctx.children.get(1).getChildCount() == 2){
            TerminalNodeImpl array = (TerminalNodeImpl) ctx.children.get(1).getChild(1);
            if(array.getText().equals("[]")){
                size = 0;
            }
        }

        return new Param(getVarType(ctx.typeSpecifier()), ctx.paramId().ID().toString(), size);
    }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitParamId(CminusParser.ParamIdContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitStatement(CminusParser.StatementContext ctx) {
        if(ctx == null)
            return null;

        ParseTree s = ctx.children.get(0);
        if(s instanceof CminusParser.CompoundStmtContext){
            return visitCompoundStmt((CminusParser.CompoundStmtContext)s);
        }

        else if(s instanceof CminusParser.ExpressionStmtContext){
            return visitExpressionStmt((CminusParser.ExpressionStmtContext) s);
        }

        else if(s instanceof CminusParser.IfStmtContext){
            return visitIfStmt(ctx.ifStmt());
        }


        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitCompoundStmt(CminusParser.CompoundStmtContext ctx) {

        ArrayList<VarDeclaration> vars = new ArrayList<>();
        ArrayList<Statement> statements = new ArrayList<>();

        for(ParseTree t : ctx.children){
            if(t instanceof CminusParser.VarDeclarationContext){
                vars.add((VarDeclaration) visitVarDeclaration((CminusParser.VarDeclarationContext) t));
            }
            else if(t instanceof CminusParser.StatementContext){
                statements.add((Statement) visitStatement((CminusParser.StatementContext) t));
            }
        }
        return new CompoundStatment(vars, statements);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitExpressionStmt(CminusParser.ExpressionStmtContext ctx) {
        ParseTree e = ctx.getChild(0);
        if(e instanceof CminusParser.ExpressionContext){
            return new ExpressionStatement((Expression) visitExpression((CminusParser.ExpressionContext) e));
        }

        return new ExpressionStatement(null);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitIfStmt(CminusParser.IfStmtContext ctx) {

//        return visitChildren(ctx);
        return new IfStmnt((Expression) visitSimpleExpression(ctx.simpleExpression()), (Statement) visitStatement(ctx.statement(0)), (Statement) visitStatement(ctx.statement(1)));
    }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitWhileStmt(CminusParser.WhileStmtContext ctx) { return visitChildren(ctx); }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitBreakStmt(CminusParser.BreakStmtContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitExpression(CminusParser.ExpressionContext ctx) {
        if(ctx == null)
            return null;

        ParseTree first = ctx.getChild(0);
        if(first instanceof CminusParser.MutableContext){
            return new Expression((Mutable) visitMutable(ctx.mutable()), ctx.getChild(1).getText(),(Expression) visitExpression(ctx.expression()));
        }

        return new Expression(null, null, (Expression) visitSimpleExpression(ctx.simpleExpression()));
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitSimpleExpression(CminusParser.SimpleExpressionContext ctx) {
        return visitOrExpression(ctx.orExpression());
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitOrExpression(CminusParser.OrExpressionContext ctx) {
        ArrayList<AndExpression> andExpressions = new ArrayList<>();

        for(CminusParser.AndExpressionContext and : ctx.andExpression()){
            andExpressions.add((AndExpression) visitAndExpression(and));
        }

        return new OrExpression(andExpressions);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitAndExpression(CminusParser.AndExpressionContext ctx) {
        ArrayList<UnaryRelExpression> unarys = new ArrayList<>();

        for(CminusParser.UnaryRelExpressionContext unary : ctx.unaryRelExpression()){
            unarys.add((UnaryRelExpression) visitUnaryRelExpression(unary));
        }

        return new AndExpression(unarys);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitUnaryRelExpression(CminusParser.UnaryRelExpressionContext ctx) {

        int bangs = ctx.BANG().size();
        RelExpression rel = (RelExpression) visitRelExpression(ctx.relExpression());

        return new UnaryRelExpression(bangs, rel);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitRelExpression(CminusParser.RelExpressionContext ctx) {
        ArrayList<SumExpression> sums = new ArrayList<>();
        ArrayList<String> ops = new ArrayList<>();


        for(CminusParser.SumExpressionContext e : ctx.sumExpression()){
            sums.add((SumExpression) visitSumExpression(e));
        }

        for(CminusParser.RelopContext r : ctx.relop()){
            ops.add(r.getText());
        }

        return new RelExpression(sums, ops);
    }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitRelop(CminusParser.RelopContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitSumExpression(CminusParser.SumExpressionContext ctx) {
        ArrayList<TermExpression> terms = new ArrayList<>();
        ArrayList<String> ops = new ArrayList<>();

        for(CminusParser.TermExpressionContext t : ctx.termExpression()){
            terms.add((TermExpression) visitTermExpression(t));
        }

        for(CminusParser.SumopContext s : ctx.sumop()){
            ops.add(s.getText());
        }

        return new SumExpression(terms, ops);
    }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitSumop(CminusParser.SumopContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitTermExpression(CminusParser.TermExpressionContext ctx) {
        ArrayList<UnaryExpression> unarys = new ArrayList<>();
        ArrayList<String> ops = new ArrayList<>();

        for(CminusParser.UnaryExpressionContext e : ctx.unaryExpression()){
            unarys.add((UnaryExpression) visitUnaryExpression(e));
        }

        for(CminusParser.MulopContext m : ctx.mulop()){
            ops.add(m.getText());
        }

        return new TermExpression(unarys, ops);
    }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitMulop(CminusParser.MulopContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitUnaryExpression(CminusParser.UnaryExpressionContext ctx) {
        ArrayList<String> ops = new ArrayList<>();

        for(CminusParser.UnaryopContext u : ctx.unaryop()){
            ops.add(u.getText());
        }

        return new UnaryExpression(ops, visitFactor(ctx.factor()));
    }
//    /**
//     * {@inheritDoc}
//     *
//     * <p>The default implementation returns the result of calling
//     * {@link #visitChildren} on {@code ctx}.</p>
//     */
//    @Override public T visitUnaryop(CminusParser.UnaryopContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitFactor(CminusParser.FactorContext ctx) {

        if(ctx.mutable() != null){
            return visitMutable(ctx.mutable());
        }

        return visitImmutable(ctx.immutable());


    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitMutable(CminusParser.MutableContext ctx) {

        return new Mutable(ctx.ID().toString(), (Expression) visitExpression(ctx.expression()));
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitImmutable(CminusParser.ImmutableContext ctx) {
        Node part = null;

        if(ctx.expression() != null){
            part = visitExpression(ctx.expression());
        }
        else if(ctx.call() != null){
            part = visitCall(ctx.call());
        }
        else{
            CminusParser.ConstantContext constant = ctx.constant();
            if(constant.CHARCONST() != null){
                return new CharConstant(constant.getText().charAt(0));
            }
            else if(constant.NUMCONST() != null){
                return new NumConstant(Integer.parseInt(constant.getText()));
            }
            else if(constant.STRINGCONST() != null){
                return new StringConstant(constant.getText());
            }
            else{
                return new BoolConstant(Boolean.parseBoolean(constant.getText()));
            }
        }

        return new Immutable(part);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public Node visitCall(CminusParser.CallContext ctx) {
        ArrayList<Expression> exprs = new ArrayList<>();

        for(CminusParser.ExpressionContext e : ctx.expression()){
            exprs.add((Expression) visitExpression(e));
        }

        String id = ctx.ID().toString();

        return new Call(id, exprs);
    }

}

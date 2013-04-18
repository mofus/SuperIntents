package transformers;

import java.util.ArrayList;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;

import superintents.control.ASTNodeWrapper;

import intentmodel.impl.*;

public class Java2AST {
	
	private static String intentName;

	public static SuperIntentImpl createTestSI() {
		SuperIntentImpl bigRedButtonIntent = new SuperIntentImpl();
		bigRedButtonIntent.setDescription("THIS IS AN INTENT DESCRIPTION");

		DataImpl bigRedButtonOutput = new DataImpl();
		bigRedButtonOutput.setMIMEType("THIS IS A MIME TYPE");
		bigRedButtonOutput.setValue("THISISAVALUE");
		
		bigRedButtonIntent.setOutput(bigRedButtonOutput);
		
		IntentImpl newIntent = new IntentImpl();
		newIntent.setAction("THIS IS AN ACTION");
		
		newIntent.getCategories().add("CATEGORY1");
		newIntent.getCategories().add("CATEGORY2");
		
		newIntent.setComponent("String");
		
		DataImpl data = new DataImpl();
		data.setValue("THISISDATA");
		data.setMIMEType(".mp3");
		newIntent.setData(data);
		
		bigRedButtonIntent.setIntent(newIntent);
		
		bigRedButtonIntent.getIntent().getExtras().put("SECOND URL", "YOUR TEXT HERE");
		bigRedButtonIntent.getIntent().getExtras().put("THIRD URL", "YOUR TEXT HERE");
		
		return bigRedButtonIntent;
	}
	
	
	public static ArrayList<ASTNodeWrapper> transformSuperIntent(SuperIntentImpl si)
	{
		intentName = "i";
		
		//result list
		ArrayList<ASTNodeWrapper> resultList = new ArrayList<ASTNodeWrapper>();
		
		//AST for generating nodes
		AST ast = AST.newAST(AST.JLS4);
		
		//Insert Input and OutPut Comments
		resultList.add(newCommentInsideMethod("Description: \n// " + si.getDescription()));
		resultList.add(newCommentInsideMethod("Output: \n// " + si.getOutput()));
		
		//Initialize the Intent
		resultList.add(new ASTNodeWrapper(initializeIntent(si, ast)));
		
		//Set the data type
		if(si.getIntent().getData() != null & si.getIntent().getData().getMIMEType() != null) {
			resultList.add(new ASTNodeWrapper(setType(si, ast)));
		}
		
		//Set Categories
		for (String category : si.getIntent().getCategories()) {
			resultList.add(new ASTNodeWrapper(setCategory(category, ast)));
		}
		
		//Set Extras
		for (String extra : si.getIntent().getExtras().keySet()) {
			resultList.add(new ASTNodeWrapper(setExtra(extra, si.getIntent().getExtras().get(extra), ast)));
		}
		
		//Add callback method
		resultList.add(new ASTNodeWrapper(generateCallbackMethod(ast),true));
			
		return resultList;
	}

	private static ASTNode initializeIntent(SuperIntentImpl si, AST ast)
	{
		//Intent(String action, Uri uri, Context packageContext, Class<?> cls)
		if(si.getIntent().getAction() != null && si.getIntent().getData() != null && si.getIntent().getComponent() != null)
			return InitializeConstructor1(si, ast);
		//Intent(Context packageContext, Class<?> cls)
		else if(si.getIntent().getComponent() != null) 
			return InitializeConstructor2(si, ast);
		//Intent(String action, Uri uri)
		else if(si.getIntent().getAction() != null && si.getIntent().getData() != null)
			return InitializeConstructor3(si, ast);
		//Intent(String action)
		else if(si.getIntent().getAction() != null)
			return InitializeConstructor4(si, ast);
		//Intent(Intent o) is not supported
		//Intent()
		else 
			InitializeConstructor5(si, ast);
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static ASTNode InitializeConstructor1(SuperIntentImpl si, AST ast)
	{
		//set the name of the variable
		VariableDeclarationFragment vdf = ast.newVariableDeclarationFragment();
		vdf.setName(ast.newSimpleName(intentName));
		
		//set the class of the instance 
		ClassInstanceCreation cic = ast.newClassInstanceCreation();
		cic.setType(ast.newSimpleType(ast.newSimpleName("Intent")));
		
		//set arguments
		StringLiteral arg1 = ast.newStringLiteral();
		arg1.setLiteralValue(si.getIntent().getAction());
		cic.arguments().add(arg1);
		
		StringLiteral arg2 = ast.newStringLiteral();
		arg2.setLiteralValue(si.getIntent().getData().getValue());
		cic.arguments().add(arg2);
		
		TypeLiteral arg3 = ast.newTypeLiteral();
		arg3.setType(ast.newSimpleType(ast.newSimpleName(si.getIntent().getComponent())));
		cic.arguments().add(arg3);
		
		vdf.setInitializer(cic);
		
		//set the type of the variable
		FieldDeclaration f = ast.newFieldDeclaration(vdf);
		f.setType(ast.newSimpleType(ast.newSimpleName("Intent")));
		
		return f;
	}

	@SuppressWarnings("unchecked")
	private static ASTNode InitializeConstructor2(SuperIntentImpl si, AST ast)
	{
		//set the name of the variable
		VariableDeclarationFragment vdf = ast.newVariableDeclarationFragment();
		vdf.setName(ast.newSimpleName(intentName));
		
		//set the class of the instance 
		ClassInstanceCreation cic = ast.newClassInstanceCreation();
		cic.setType(ast.newSimpleType(ast.newSimpleName("Intent")));
		
		//set arguments
		TypeLiteral arg1 = ast.newTypeLiteral();
		arg1.setType(ast.newSimpleType(ast.newSimpleName(si.getIntent().getComponent())));
		cic.arguments().add(arg1);
		
		vdf.setInitializer(cic);
		
		//set the type of the variable
		FieldDeclaration f = ast.newFieldDeclaration(vdf);
		f.setType(ast.newSimpleType(ast.newSimpleName("Intent")));
		
		return f;
	}
	
	@SuppressWarnings("unchecked")
	private static ASTNode InitializeConstructor3(SuperIntentImpl si, AST ast)
	{
		//set the name of the variable
		VariableDeclarationFragment vdf = ast.newVariableDeclarationFragment();
		vdf.setName(ast.newSimpleName(intentName));
		
		//set the class of the instance 
		ClassInstanceCreation cic = ast.newClassInstanceCreation();
		cic.setType(ast.newSimpleType(ast.newSimpleName("Intent")));
		
		//set arguments
		StringLiteral arg1 = ast.newStringLiteral();
		arg1.setLiteralValue(si.getIntent().getAction());
		cic.arguments().add(arg1);

		StringLiteral arg2 = ast.newStringLiteral();
		arg2.setLiteralValue(si.getIntent().getData().getValue());
		cic.arguments().add(arg2);

		vdf.setInitializer(cic);
		
		//set the type of the variable
		FieldDeclaration f = ast.newFieldDeclaration(vdf);
		f.setType(ast.newSimpleType(ast.newSimpleName("Intent")));
		
		return f;
	}
	
	@SuppressWarnings("unchecked")
	private static ASTNode InitializeConstructor4(SuperIntentImpl si, AST ast)
	{
		//set the name of the variable
		VariableDeclarationFragment vdf = ast.newVariableDeclarationFragment();
		vdf.setName(ast.newSimpleName(intentName));
		
		//set the class of the instance 
		ClassInstanceCreation cic = ast.newClassInstanceCreation();
		cic.setType(ast.newSimpleType(ast.newSimpleName("Intent")));
		
		//set arguments
		StringLiteral arg1 = ast.newStringLiteral();
		arg1.setLiteralValue(si.getIntent().getAction());
		cic.arguments().add(arg1);

		vdf.setInitializer(cic);
		
		//set the type of the variable
		FieldDeclaration f = ast.newFieldDeclaration(vdf);
		f.setType(ast.newSimpleType(ast.newSimpleName("Intent")));
		
		return f;
	}
	
	private static ASTNode InitializeConstructor5(SuperIntentImpl si, AST ast)
	{
		//set the name of the variable
		VariableDeclarationFragment vdf = ast.newVariableDeclarationFragment();
		vdf.setName(ast.newSimpleName(intentName));
		
		//set the class of the instance 
		ClassInstanceCreation cic = ast.newClassInstanceCreation();
		cic.setType(ast.newSimpleType(ast.newSimpleName("Intent")));

		vdf.setInitializer(cic);
		
		//set the type of the variable
		FieldDeclaration f = ast.newFieldDeclaration(vdf);
		f.setType(ast.newSimpleType(ast.newSimpleName("Intent")));
		
		return f;
	}
	
	@SuppressWarnings("unchecked")
	private static ASTNode setType(SuperIntentImpl si, AST ast)
	{
		//set invocation method name
		MethodInvocation mi = ast.newMethodInvocation();
		mi.setExpression(ast.newSimpleName(intentName));
		mi.setName(ast.newSimpleName("setType"));
		
		//set argument
		StringLiteral sl = ast.newStringLiteral();
		sl.setLiteralValue(si.getIntent().getData().getMIMEType());
		mi.arguments().add(sl);
		
		ExpressionStatement es = ast.newExpressionStatement(mi);
		
		return es;
	}
	
	@SuppressWarnings("unchecked")
	private static ASTNode setCategory(String category, AST ast) {
		// set invocation method name
		MethodInvocation mi = ast.newMethodInvocation();
		mi.setExpression(ast.newSimpleName(intentName));
		mi.setName(ast.newSimpleName("setCategory"));

		// set argument
		StringLiteral sl = ast.newStringLiteral();
		sl.setLiteralValue(category);
		mi.arguments().add(sl);

		ExpressionStatement es = ast.newExpressionStatement(mi);

		return es;
	}
	
	@SuppressWarnings("unchecked")
	private static ASTNode setExtra(String extra, String value, AST ast) {
		// set invocation method name
		MethodInvocation mi = ast.newMethodInvocation();
		mi.setExpression(ast.newSimpleName(intentName));
		mi.setName(ast.newSimpleName("putExtra"));

		// set argument
		StringLiteral sl1 = ast.newStringLiteral();
		sl1.setLiteralValue(extra);
		mi.arguments().add(sl1);
		
		StringLiteral sl2 = ast.newStringLiteral();
		sl2.setLiteralValue(value);
		mi.arguments().add(sl2);

		ExpressionStatement es = ast.newExpressionStatement(mi);

		return es;
	}
	
	private static ASTNodeWrapper newCommentInsideMethod(String comment) {
		return new ASTNodeWrapper(comment);
	}
	
	private static ASTNodeWrapper newCommentOutsideMethod(String comment) {
		return new ASTNodeWrapper(comment);
	}
	
	@SuppressWarnings("unchecked")
	private static ASTNode generateCallbackMethod(AST ast) {
		//method declaration
		MethodDeclaration m = ast.newMethodDeclaration();
		m.setName(ast.newSimpleName("OnActivityResult"));
		
		//add the @Override annotation
		MarkerAnnotation ma = ast.newMarkerAnnotation();
		ma.setTypeName(ast.newSimpleName("Override"));
		m.modifiers().add(ma);
		
		//parameters
		SingleVariableDeclaration svd1 = ast.newSingleVariableDeclaration();
		svd1.setType(ast.newPrimitiveType(PrimitiveType.INT));
		svd1.setName(ast.newSimpleName("requestCode"));
		m.parameters().add(svd1);
		
		SingleVariableDeclaration svd2 = ast.newSingleVariableDeclaration();
		svd2.setType(ast.newPrimitiveType(PrimitiveType.INT));
		svd2.setName(ast.newSimpleName("resultCode"));
		m.parameters().add(svd2);
		
		SingleVariableDeclaration svd3 = ast.newSingleVariableDeclaration();
		svd3.setType(ast.newSimpleType(ast.newSimpleName("Intent")));
		svd3.setName(ast.newSimpleName("data"));
		m.parameters().add(svd3);
		
		//Generate the internal if statement
//		IfStatement is = ast.newIfStatement();
//		InfixExpression ie1 = ast.newInfixExpression();
//		
//		InfixExpression leftIe = ast.newInfixExpression();
//		leftIe.setLeftOperand(ast.newSimpleName("resultCode"));
//		leftIe.setOperator(Operator.EQUALS);
//		leftIe.setRightOperand(ast.newSimpleName("RESULT_OK"));
//		ie1.setLeftOperand(leftIe);
//		
//		InfixExpression RightIe = ast.newInfixExpression();
//		RightIe.setLeftOperand(ast.newSimpleName("requestCode"));
//		RightIe.setOperator(Operator.EQUALS);
//		RightIe.setRightOperand(ast.newSimpleName("REQUEST_CODE"));
//		ie1.setLeftOperand(RightIe);
//		
//		is.setExpression(ie1);
//		m.getBody().statements().add(is);
		
		return m;
	}
}
	
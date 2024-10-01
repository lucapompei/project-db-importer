package lp.web.utils;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import lp.web.constants.CommonConstants;

import java.io.File;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * This class exposes utilities to handle parsing operations
 */
public class ParsingUtils {

    /**
     * Private constructor for the utility class
     */
    private ParsingUtils() {
        throw new IllegalAccessError(CommonConstants.STANDARD_MESSAGE_UTILITY_CLASS);
    }

    /**
     * Configures the parser in order to extract the project information from the template
     *
     * @param projectFolder, the project folder
     */
    public static void configureParser(String projectFolder) {
        // Creates a CombinedTypeSolver which includes reflection types and project types
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());
        combinedTypeSolver.add(new JavaParserTypeSolver(new File(projectFolder)));

        // Sets up the symbol solver
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);

        // Configures the parser to use the symbol solver
        ParserConfiguration parserConfiguration = new ParserConfiguration()
                .setSymbolResolver(symbolSolver)
                .setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_21);

        // Applies the configuration
        StaticJavaParser.setConfiguration(parserConfiguration);
    }

    /**
     * Extract the stream of name expressions from the class or interface types existing the in under analysis file
     *
     * @param compilationUnit, the unit related to the under analysis file
     * @return the stream of name expressions from the class or interface types existing the in under analysis file
     */
    public static Stream<String> getNameExprFromClassOrInterfaceTypes(CompilationUnit compilationUnit) {
        return compilationUnit
                .findAll(ClassOrInterfaceType.class)
                .stream()
                .map(NodeWithSimpleName::getNameAsString);
    }

    /**
     * Extract the stream of name expressions from the method declarations existing the in under analysis file
     *
     * @param compilationUnit, the unit related to the under analysis file
     * @return the stream of name expressions from the method declarations existing the in under analysis file
     */
    public static Stream<String> getNameExprFromMethodDeclarations(CompilationUnit compilationUnit) {
        return compilationUnit
                .findAll(MethodDeclaration.class)
                .stream()
                .map(NodeWithSimpleName::getNameAsString);
    }

    /**
     * Extract the stream of name expressions from the method expressions existing the in under analysis file
     *
     * @param compilationUnit, the unit related to the under analysis file
     * @return the stream of name expressions from the method expressions existing the in under analysis file
     */
    public static Stream<String> getNameExprFromMethodExpressions(CompilationUnit compilationUnit) {
        return compilationUnit
                .findAll(MethodCallExpr.class)
                .stream()
                .map(MethodCallExpr::getScope)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(Expression::isNameExpr)
                .map(Expression::asNameExpr)
                .map(NodeWithSimpleName::getNameAsString);
    }

    /**
     * Extract the stream of name expressions from the field expressions existing the in under analysis file
     *
     * @param compilationUnit, the unit related to the under analysis file
     * @return the stream of name expressions from the field expressions existing the in under analysis file
     */
    public static Stream<String> getNameExprFromFieldExpressions(CompilationUnit compilationUnit) {
        return compilationUnit
                .findAll(FieldAccessExpr.class)
                .stream()
                .map(FieldAccessExpr::getScope)
                .filter(Expression::isNameExpr)
                .map(Expression::asNameExpr)
                .map(NodeWithSimpleName::getNameAsString);
    }

}

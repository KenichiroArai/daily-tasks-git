package io.github.kenichiroarai.dailytasks;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.Architectures;

/**
 * 層間のルールのテスト<br>
 * <p>
 * 本番コード（テストを除く）を対象に、AGENTS.md の「層間のルール」を検査する。
 * </p>
 *
 * @author KenichiroArai
 *
 * @since 0.1.0
 *
 * @version 0.1.0
 */
@SuppressWarnings({
    "nls", "static-method"
})
public class ArchitectureTest {

    /**
     * ルートパッケージ
     */
    private static final String ROOT_PACKAGE = "io.github.kenichiroarai.dailytasks";

    /**
     * 検査対象のクラス
     */
    private static JavaClasses classes;

    /**
     * 検査対象のクラスを読み込む<br>
     */
    @BeforeAll
    public static void importClasses() {

        ArchitectureTest.classes
            = new ClassFileImporter().withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(ArchitectureTest.ROOT_PACKAGE);

    }

    /**
     * impl の公開範囲のテスト - 正常系:impl のクラスをフィールドの型として使っていない場合
     */
    @Test
    public void testImplUsage_normalNoImplField() {

        /* 期待値の定義 */

        /* 準備 */
        final ArchRule testRule
            = ArchRuleDefinition.noFields().should().haveRawType(JavaClass.Predicates.resideInAPackage("..impl.."));

        /* テスト対象の実行 */
        testRule.check(ArchitectureTest.classes);

        /* 検証の準備 */

        /* 検証の実施 */
        // 違反があれば check が AssertionError を投げる

    }

    /**
     * impl の公開範囲のテスト - 正常系:impl のクラスをメソッド・コンストラクタの引数の型として使っていない場合
     */
    @Test
    public void testImplUsage_normalNoImplParameter() {

        /* 期待値の定義 */

        /* 準備 */
        // 無名クラスのコンストラクタは外側のインスタンスを暗黙の引数に取るため対象外とする
        final ArchRule testRule = ArchRuleDefinition.noCodeUnits().that().areDeclaredInClassesThat()
            .areNotAnonymousClasses().should().haveRawParameterTypes(DescribedPredicate.describe("impl のクラスを含む",
                parameters -> parameters.stream().anyMatch(JavaClass.Predicates.resideInAPackage("..impl..")::test)));

        /* テスト対象の実行 */
        testRule.check(ArchitectureTest.classes);

        /* 検証の準備 */

        /* 検証の実施 */
        // 違反があれば check が AssertionError を投げる

    }

    /**
     * impl の公開範囲のテスト - 正常系:impl のクラスをメソッドの戻り値の型として使っていない場合
     */
    @Test
    public void testImplUsage_normalNoImplReturnType() {

        /* 期待値の定義 */

        /* 準備 */
        final ArchRule testRule = ArchRuleDefinition.noMethods().should()
            .haveRawReturnType(JavaClass.Predicates.resideInAPackage("..impl.."));

        /* テスト対象の実行 */
        testRule.check(ArchitectureTest.classes);

        /* 検証の準備 */

        /* 検証の実施 */
        // 違反があれば check が AssertionError を投げる

    }

    /**
     * DI のテスト - 正常系:フィールドインジェクションを使っていない場合
     */
    @Test
    public void testInjection_normalNoFieldInjection() {

        /* 期待値の定義 */

        /* 準備 */
        final ArchRule testRule = ArchRuleDefinition.noFields().should().beAnnotatedWith(Autowired.class);

        /* テスト対象の実行 */
        testRule.check(ArchitectureTest.classes);

        /* 検証の準備 */

        /* 検証の実施 */
        // 違反があれば check が AssertionError を投げる

    }

    /**
     * 層間の参照のテスト - 正常系:すぐ下の層だけを参照し、飛び越し・逆向きの参照がない場合
     */
    @Test
    public void testLayerDependencies_normalOnlyNextLayer() {

        /* 期待値の定義 */

        /* 準備 */
        final ArchRule testRule = Architectures.layeredArchitecture().consideringOnlyDependenciesInLayers()
            .layer("EntryPoint").definedBy(ArchitectureTest.ROOT_PACKAGE).layer("Presentation")
            .definedBy("..carryover.presentation..").layer("Application").definedBy("..carryover.application..")
            .layer("Domain").definedBy("..carryover.domain..").layer("Repository").definedBy("..carryover.repository..")
            .optionalLayer("Infrastructure").definedBy("..carryover.infrastructure..").whereLayer("EntryPoint")
            .mayNotBeAccessedByAnyLayer().whereLayer("Presentation").mayOnlyBeAccessedByLayers("EntryPoint")
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Presentation").whereLayer("Domain")
            .mayOnlyBeAccessedByLayers("Application").whereLayer("Repository").mayOnlyBeAccessedByLayers("Domain")
            .whereLayer("Infrastructure").mayNotAccessAnyLayer();

        /* テスト対象の実行 */
        testRule.check(ArchitectureTest.classes);

        /* 検証の準備 */

        /* 検証の実施 */
        // 違反があれば check が AssertionError を投げる

    }

    /**
     * 設定値のテスト - 正常系:@Value で設定値を個別に取得していない場合
     */
    @Test
    public void testSettings_normalNoValueAnnotation() {

        /* 期待値の定義 */

        /* 準備 */
        final ArchRule testRule
            = ArchRuleDefinition.noClasses().should().dependOnClassesThat().areAssignableTo(Value.class);

        /* テスト対象の実行 */
        testRule.check(ArchitectureTest.classes);

        /* 検証の準備 */

        /* 検証の実施 */
        // 違反があれば check が AssertionError を投げる

    }

    /**
     * 設定値のテスト - 正常系:設定ファイルを受け取るクラスが presentation 層だけにある場合
     */
    @Test
    public void testSettings_normalPropertiesOnlyPresentation() {

        /* 期待値の定義 */

        /* 準備 */
        final ArchRule testRule = ArchRuleDefinition.classes().that().areAnnotatedWith(ConfigurationProperties.class)
            .should().resideInAPackage("..carryover.presentation..");

        /* テスト対象の実行 */
        testRule.check(ArchitectureTest.classes);

        /* 検証の準備 */

        /* 検証の実施 */
        // 違反があれば check が AssertionError を投げる

    }

}

package architecture

import architecture.CleanArchitectureTests.{`classes in domain can only access classes in domain itself`, `clean architecture is respected`}
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.library.Architectures.layeredArchitecture

class CleanArchitectureTests
    extends ArchUnitFunSpec(
      "Clean architecture",
      "..ordershipping..",
      `clean architecture is respected`,
      `classes in domain can only access classes in domain itself`
    )

object CleanArchitectureTests {
  private val domain = "Domain"
  private val usecase = "Use Cases"

  private val domainPackages = List("..domain..")
  private val systemPackages = List("java..", "scala..")

  private val `clean architecture is respected`: ArchRule =
    layeredArchitecture()
      .consideringAllDependencies()
      .layer(domain).definedBy(domainPackages: _*)
      .layer(usecase).definedBy("..usecase..")
      .whereLayer(domain).mayOnlyBeAccessedByLayers(usecase)
      .as("We should respect our clean architecture rules")


  private val `classes in domain can only access classes in domain itself`: ArchRule =
    restrictClassDependencyOnItselfAndOnAuthorizedPackages(domainPackages)
      .as("Domain should only have dependencies on Domain itself")

  private def restrictClassDependencyOnItselfAndOnAuthorizedPackages(classesInPackages: List[String],
                                                                     authorizedPackages: List[String] = List.empty): ArchRule =
    classes()
      .that().resideInAnyPackage(classesInPackages: _*)
      .should()
      .onlyDependOnClassesThat()
      .resideInAnyPackage(classesInPackages ::: authorizedPackages ::: systemPackages: _*)
}

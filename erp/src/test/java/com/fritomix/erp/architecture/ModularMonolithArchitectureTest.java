package com.fritomix.erp.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import java.util.Arrays;
import java.util.List;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Executable module map for the modular monolith.
 *
 * <p>Without these rules the module boundaries decay silently: every added
 * {@code import} that reaches into another module passes the compiler and the build,
 * and the option of extracting a service later turns into a rewrite. Failing the build
 * instead is what keeps the architecture honest.
 */
@AnalyzeClasses(packages = "com.fritomix.erp", importOptions = ImportOption.DoNotIncludeTests.class)
class ModularMonolithArchitectureTest {

    private static final String MODULES = "com.fritomix.erp.modules..";

    private static final String AUTH = "com.fritomix.erp.modules.auth..";
    private static final String CUSTOMERS = "com.fritomix.erp.modules.customers..";
    private static final String DASHBOARD = "com.fritomix.erp.modules.dashboard..";
    private static final String DISPATCH = "com.fritomix.erp.modules.dispatch..";
    private static final String DRIVERS = "com.fritomix.erp.modules.drivers..";
    private static final String MULTIPEDIDOS = "com.fritomix.erp.modules.multipedidos..";
    private static final String NOTIFICATIONS = "com.fritomix.erp.modules.notifications..";
    private static final String ORDERS = "com.fritomix.erp.modules.orders..";
    private static final String PRODUCTS = "com.fritomix.erp.modules.products..";
    private static final String PUSH = "com.fritomix.erp.modules.push..";
    private static final String REPORTS = "com.fritomix.erp.modules.reports..";
    private static final String ROLES = "com.fritomix.erp.modules.roles..";
    private static final String SETTINGS = "com.fritomix.erp.modules.settings..";
    private static final String USERS = "com.fritomix.erp.modules.users..";
    private static final String VEHICLES = "com.fritomix.erp.modules.vehicles..";

    private static final List<String> ALL_MODULES = List.of(
            AUTH, CUSTOMERS, DASHBOARD, DISPATCH, DRIVERS, MULTIPEDIDOS, NOTIFICATIONS,
            ORDERS, PRODUCTS, PUSH, REPORTS, ROLES, SETTINGS, USERS, VEHICLES);

    /**
     * Modules that own a single table and nothing else. They are the first candidates
     * if a service is ever extracted, so they must stay free of module dependencies.
     */
    private static final String[] LEAF_MODULES = {
            CUSTOMERS, PRODUCTS, DRIVERS, VEHICLES, DASHBOARD, SETTINGS
    };

    @ArchTest
    static final ArchRule customers_is_a_leaf_module =
            leafModule(CUSTOMERS, "customers");

    @ArchTest
    static final ArchRule products_is_a_leaf_module =
            leafModule(PRODUCTS, "products");

    @ArchTest
    static final ArchRule drivers_is_a_leaf_module =
            leafModule(DRIVERS, "drivers");

    @ArchTest
    static final ArchRule vehicles_is_a_leaf_module =
            leafModule(VEHICLES, "vehicles");

    @ArchTest
    static final ArchRule dashboard_is_a_leaf_module =
            leafModule(DASHBOARD, "dashboard");

    @ArchTest
    static final ArchRule settings_is_a_leaf_module =
            leafModule(SETTINGS, "settings");

    /**
     * Layering inside every module: domain sits at the centre and knows nothing about
     * the layers wrapped around it.
     */
    @ArchTest
    static final ArchRule domain_does_not_depend_on_outer_layers = noClasses()
            .that().resideInAPackage(MODULES + "domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    MODULES + "api..",
                    MODULES + "application..")
            .because("domain must not depend on application or api");

    /**
     * An application service must not reach up into the web layer.
     */
    @ArchTest
    static final ArchRule application_does_not_depend_on_api = noClasses()
            .that().resideInAPackage(MODULES + "application..")
            .should().dependOnClassesThat().resideInAPackage(MODULES + "api..")
            .because("application must not depend on api");

    /**
     * The orders/dispatch boundary. Both sides share one aggregate through the
     * {@code dispatch_orders} join table, so a domain-level reference is intentional.
     * What must not happen is orders borrowing dispatch services or controllers, which
     * would turn a data relationship into a behavioural one.
     */
    @ArchTest
    static final ArchRule orders_only_references_the_dispatch_domain_model = noClasses()
            .that().resideInAPackage(ORDERS)
            .should().dependOnClassesThat().resideInAnyPackage(
                    DISPATCH + "api..",
                    DISPATCH + "application..")
            .because("orders may read the dispatch aggregate, not its services");

    /**
     * Dispatch composes drivers, vehicles, products and orders as part of its aggregate
     * and notifies on side effects. It must not read query-side modules, otherwise the
     * write model would depend on read models.
     */
    @ArchTest
    static final ArchRule dispatch_does_not_depend_on_read_side_modules = noClasses()
            .that().resideInAPackage(DISPATCH)
            .should().dependOnClassesThat().resideInAnyPackage(
                    REPORTS, MULTIPEDIDOS, ROLES, USERS)
            .because("dispatch is a write model and must not depend on read models");

    /**
     * Auth owns identity and is depended upon widely; the edge itself must stay
     * one-directional so security code never imports business modules.
     */
    @ArchTest
    static final ArchRule auth_does_not_depend_on_business_modules = noClasses()
            .that().resideInAPackage(AUTH)
            .should().dependOnClassesThat().resideInAnyPackage(
                    CUSTOMERS, DASHBOARD, DISPATCH, DRIVERS, MULTIPEDIDOS,
                    ORDERS, PRODUCTS, PUSH, REPORTS, VEHICLES)
            .because("the identity boundary must remain one-directional");

    /**
     * Shared code must not reach into a module's repository layer.
     *
     * <p>Documented exception: {@code com.fritomix.erp.security} is the authentication
     * infrastructure that sits inside the auth boundary, so
     * {@code CustomUserDetailsService} legitimately loads users through
     * {@code auth.domain.repository.UserRepository}. Excluding that package keeps the rule
     * about genuinely shared code instead of hiding the real intent.
     */
    @ArchTest
    static final ArchRule shared_code_does_not_touch_module_repositories = noClasses()
            .that().resideOutsideOfPackages(MODULES, "com.fritomix.erp.security..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    AUTH + "domain.repository..",
                    CUSTOMERS + "domain.repository..",
                    DISPATCH + "domain.repository..",
                    ORDERS + "domain.repository..",
                    PRODUCTS + "domain.repository..")
            .because("repositories belong to the module that owns the table");

    private static ArchRule leafModule(String modulePackage, String moduleName) {
        String[] otherModules = ALL_MODULES.stream()
                .filter(candidate -> !candidate.equals(modulePackage))
                .toArray(String[]::new);
        return noClasses()
                .that().resideInAPackage(modulePackage)
                .should().dependOnClassesThat().resideInAnyPackage(otherModules)
                .because("module '" + moduleName
                        + "' is a leaf and must not depend on another module (forbidden: "
                        + Arrays.toString(otherModules) + ")");
    }
}
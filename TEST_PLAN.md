# Test Structure Plan for Slay the Spire Board Game Mod

## Executive Summary

This plan establishes a comprehensive testing infrastructure for the Board Game mod, which currently has **no tests** for 400+ Java files. The approach focuses on pragmatic, high-value testing while acknowledging the challenges of testing a mod that heavily depends on the Slay the Spire game framework.

## Current State

- **No test directory** (`src/test/java` does not exist)
- **No test dependencies** in pom.xml (no JUnit, no Mockito)
- **No test plugins** configured
- **900+ files** of source code with complex game dependencies
- Heavy reliance on **static state** (AbstractDungeon, CardCrawlGame)
- Actions execute via **frame-based update() lifecycle**

## Recommended Test Structure

### 1. Directory Structure

```
src/
├── main/java/CoopBoardGame/          # Existing source code
└── test/
    ├── java/CoopBoardGame/
    │   ├── actions/              # Action tests (mirror main structure)
    │   ├── cards/
    │   │   ├── BGRed/
    │   │   ├── BGBlue/
    │   │   └── ...
    │   ├── relics/
    │   ├── thedie/
    │   ├── util/
    │   └── testutil/             # NEW: Shared test utilities
    │       ├── TestBase.java
    │       ├── MockActionManager.java
    │       ├── fixtures/
    │       │   ├── CombatFixtures.java
    │       │   ├── PlayerFixtures.java
    │       │   └── MonsterFixtures.java
    │       ├── mocks/
    │       │   └── MockAbstractDungeon.java
    │       └── builders/
    │           ├── CardBuilder.java
    │           └── MonsterBuilder.java
    └── resources/
        └── CoopBoardGameTestResources/
```

**Naming Convention:** `{ClassName}Test.java` (e.g., `BGFeedTest.java`, `BGFeedActionTest.java`)

### 2. Maven Dependencies to Add

```xml
<properties>
    <junit.version>5.9.3</junit.version>
    <mockito.version>4.11.0</mockito.version>
    <assertj.version>3.24.2</assertj.version>
</properties>

<dependencies>
    <!-- Test Dependencies -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>${junit.version}</version>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>${mockito.version}</version>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-inline</artifactId>
        <version>${mockito.version}</version>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-junit-jupiter</artifactId>
        <version>${mockito.version}</version>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
        <version>${assertj.version}</version>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <!-- Maven Surefire for test execution -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.0.0</version>
            <configuration>
                <includes>
                    <include>**/*Test.java</include>
                    <include>**/*Tests.java</include>
                </includes>
                <argLine>
                    --add-opens java.base/java.lang=ALL-UNNAMED
                    --add-opens java.base/java.util=ALL-UNNAMED
                </argLine>
            </configuration>
        </plugin>
    </plugins>
</build>
```

**Rationale:**

- **JUnit 5 (Jupiter)** - Modern test framework with better assertions and parameterized tests
- **Mockito 4.x** - Industry standard mocking framework
- **mockito-inline** - Required for mocking static methods (AbstractDungeon.\*)
- **AssertJ** - Fluent assertions for more readable tests
- **Surefire Plugin** - Maven test execution

### 3. Testing Strategy by Component Type

#### High Priority (Start Here)

**A. Utility Classes** (Easy wins, no mocking needed)

- Target: `util/TextureLoader.java`, `util/IDCheckDontTouchPls.java`
- Effort: Low | Value: Medium
- Strategy: Pure unit tests, no game dependencies

**B. Action Classes** (Core game logic)

- Target: 10-15 most complex actions from `actions/` (e.g., BGFeedAction, BGLockInRollAction)
- Effort: Medium | Value: Very High
- Strategy: Mock target/player, test update() lifecycle, verify state changes
- Example: `BGFeedActionTest` tests damage → death detection → strength gain

**C. TheDie System** (Critical board game mechanic)

- Target: `thedie/TheDie.java`
- Effort: Medium | Value: Very High
- Strategy: Mock RNG for deterministic tests, verify roll → move selection → UI buttons

#### Medium Priority

**D. Cards** (User-facing functionality)

- Target: 20-30 representative cards (simple to complex)
- Effort: Medium-High | Value: High
- Strategy: Mock player/target, verify use() → actions queued → effects applied
- Examples: BGFeed, BGStrike, BGDefend, BGWhirlwind, BGCatalyst

**E. Relics** (Board game mechanics)

- Target: 15-20 most complex relics (especially dice-controlled)
- Effort: Medium | Value: High
- Strategy: Test lifecycle hooks (atTurnStart, onVictory), clickable interactions
- Examples: BGGamblingChip, BGTheAbacus, BGToolbox

**F. Base Abstract Classes** (Shared behavior)

- Target: AbstractBGCard, AbstractBGRelic, AbstractBGMonster
- Effort: Low-Medium | Value: High
- Strategy: Test shared functionality (secondMagicNumber, payment system, row positioning)

#### Lower Priority

**G. Monsters** (Complex state machines)

- Target: 5-10 representative monsters
- Effort: High | Value: Medium
- Strategy: Test dice-controlled move selection, not vanilla AI

**H. Powers** (Many are vanilla wrappers)

- Target: Custom powers only (20-30)
- Effort: Medium | Value: Medium

**I. Events** (Text/UI heavy)

- Target: 5-10 key events
- Effort: High | Value: Low-Medium

**J. Patches** (SpirePatch classes)

- Strategy: Skip direct testing, test indirectly through integration tests
- Effort: Very High | Value: Low

**K. UI Components** (Requires LibGDX runtime)

- Strategy: Manual testing only
- Effort: Very High | Value: Low

### 4. Mocking Strategy

#### Full Mocks (Mockito)

- `AbstractDungeon` - Use `mockStatic()` for static state
- `CardCrawlGame` - Singleton instance
- `AbstractPlayer` / `AbstractMonster` - Game entities
- Texture/Graphics classes - Irrelevant to logic

#### Custom Fakes (Lightweight implementations)

- `MockActionManager` - Controllable action queue for testing
- `MonsterGroup` - Simple list wrapper
- `DamageInfo` - Data holder

#### Real Instances (No mocking)

- Your utility classes
- Your custom actions/cards/relics (when testing behavior)
- Pure data objects

### 5. Critical Test Utilities to Build First

#### A. TestBase.java

Base class with common mock setup:

```java
@ExtendWith(MockitoExtension.class)
public abstract class TestBase {

    @Mock
    protected AbstractPlayer mockPlayer;

    @Mock
    protected AbstractMonster mockMonster;

    protected MockActionManager actionManager;

    @BeforeEach
    void baseSetUp() {
        actionManager = new MockActionManager();
        // Common stubs...
    }
}
```

#### B. MockActionManager.java

Controllable action queue that processes update() lifecycle:

```java
public class MockActionManager {

    private Queue<AbstractGameAction> actions = new LinkedList<>();

    public void addToBottom(AbstractGameAction action) {
        actions.add(action);
    }

    public void processAllActions() {
        while (!actions.isEmpty()) {
            AbstractGameAction action = actions.poll();
            while (!action.isDone) {
                action.update();
            }
        }
    }
}
```

#### C. CombatFixtures.java

Standard combat scenarios:

```java
public class CombatFixtures {

    public static CombatScenario basicCombat() {
        // Returns pre-configured player + enemy + actionManager
    }

    public static CombatScenario withDiceRoll(int roll) {
        // Returns combat scenario with specific die roll
    }
}
```

### 6. Test Pattern Examples

#### Pattern A: Testing Cards

```java
@ExtendWith(MockitoExtension.class)
class BGFeedTest extends TestBase {

    private BGFeed card;

    @BeforeEach
    void setUp() {
        card = new BGFeed();
    }

    @Test
    void testUse_KillsEnemy_GrantsStrength() {
        // Arrange: Set up enemy with low HP
        // Act: card.use(player, enemy)
        // Assert: Verify damage dealt + strength power queued
    }

    @Test
    void testUpgrade_IncreasesStrengthGain() {
        // Test upgrade logic
    }
}
```

#### Pattern B: Testing Actions

```java
@ExtendWith(MockitoExtension.class)
class BGFeedActionTest {

    @Mock
    private AbstractCreature target;

    private BGFeedAction action;

    @Test
    void testUpdate_TargetKilled_GrantsStrength() {
        // Arrange: Set target to dying state
        // Act: action.update() // Process frame
        // Assert: Verify damage + ApplyPowerAction queued
    }
}
```

#### Pattern C: Testing Dice Mechanics

```java
class TheDieTest {

    @Test
    void testRoll_GeneratesValueBetween1And6() {
        // Mock RNG or test range
        TheDie.roll();
        assertThat(TheDie.initialRoll).isBetween(1, 6);
    }

    @Test
    void testTentativeRoll_UpdatesMonsterRoll() {
        // Test dice modification phase
    }
}
```

### 7. Implementation Roadmap

#### Phase 1: Foundation (Week 1)

**Goal:** Prove infrastructure works

1. Update [pom.xml](pom.xml) with test dependencies and Surefire plugin
2. Create `src/test/java/CoopBoardGame/testutil/` directory structure
3. Build core test utilities:
    - TestBase.java
    - MockActionManager.java
    - CombatFixtures.java
4. Write 5 proof-of-concept tests:
    - TextureLoaderTest (no mocks)
    - TheDieTest (mocked RNG)
    - BGFeedActionTest (action lifecycle)
    - BGFeedTest (card usage)
    - BGGamblingChipTest (relic interaction)
5. Run `mvnw.cmd test` and verify all pass

**Deliverable:** 5 passing tests, reusable infrastructure

#### Phase 2: Core Systems (Weeks 2-3)

**Goal:** Test critical board game mechanics

6. TheDie system tests (10 tests)
    - Roll generation, tentative rolls, lock-in, monster moves
7. Key action classes (20-30 tests)
    - Damage, draw, buff, complex chains
8. Base abstract classes (10 tests)
    - AbstractBGCard, AbstractBGRelic, AbstractBGMonster

**Deliverable:** 40-50 tests covering dice and action systems

#### Phase 3: Game Components (Weeks 3-4)

**Goal:** Expand to cards and relics

9. Sample cards from each color (30-40 tests)
    - Red, Blue, Green, Purple, Colorless
10. Dice-controlled relics (15-20 tests)
    - Gambling Chip, Abacus, Toolbox, Red Skull
11. Integration tests (10 tests)
    - Full combat scenarios, card chains

**Deliverable:** 100+ tests covering representative components

#### Phase 4: Ongoing Maintenance

**Goal:** Test-driven development

12. Write tests for new features
13. Add regression tests for bug fixes
14. Expand coverage incrementally

**Target:** 300+ tests over 3-6 months

### 8. Testing Commands

```bash
# Run all tests
mvnw.cmd test

# Run specific test class
mvnw.cmd test -Dtest=BGFeedTest

# Run tests in package
mvnw.cmd test -Dtest=CoopBoardGame.actions.*

# Skip tests during build
mvnw.cmd package -DskipTests

# Clean and test
mvnw.cmd clean test
```

## Critical Files to Create/Modify

### To Modify

1. [pom.xml](pom.xml) - Add test dependencies, Surefire plugin

### To Create (Test Infrastructure)

2. `src/test/java/CoopBoardGame/testutil/TestBase.java` - Base test class
3. `src/test/java/CoopBoardGame/testutil/MockActionManager.java` - Action queue fake
4. `src/test/java/CoopBoardGame/testutil/fixtures/CombatFixtures.java` - Combat scenarios
5. `src/test/java/CoopBoardGame/testutil/fixtures/PlayerFixtures.java` - Player configs
6. `src/test/java/CoopBoardGame/testutil/fixtures/MonsterFixtures.java` - Enemy configs
7. `src/test/java/CoopBoardGame/testutil/builders/CardBuilder.java` - Test card builder

### To Create (Example Tests)

8. `src/test/java/CoopBoardGame/util/TextureLoaderTest.java` - Pure utility test
9. `src/test/java/CoopBoardGame/thedie/TheDieTest.java` - Dice mechanics test
10. `src/test/java/CoopBoardGame/actions/BGFeedActionTest.java` - Action lifecycle test
11. `src/test/java/CoopBoardGame/cards/BGRed/BGFeedTest.java` - Card usage test
12. `src/test/java/CoopBoardGame/relics/BGGamblingChipTest.java` - Relic test

## Key Principles

1. **Focus on YOUR logic, not the game framework** - Don't test AbstractCard's damage calculation, test YOUR custom damage logic
2. **Mock strategically** - Full mocks for framework, real instances for your code
3. **Test behavior, not implementation** - What does it do, not how
4. **Start small, expand incrementally** - 5 tests → 50 tests → 100+ tests
5. **Prioritize high-value tests** - Actions and dice first, UI last
6. **Build reusable utilities** - Invest in TestBase, MockActionManager, fixtures
7. **Integration tests sparingly** - Use for complex chains, not every component

## Success Metrics

- **Week 1:** 5 passing tests, infrastructure working
- **Month 1:** 50 tests covering utilities, actions, dice
- **Month 2:** 100 tests adding cards and relics
- **Month 3+:** 200+ tests, test-driven development for new features
- **Long-term:** 50-60% code coverage on business logic

## Pragmatic Notes

- This is a **900+ file mod** - you cannot test everything
- Focus on **custom game logic**, not vanilla Slay the Spire behavior
- **Manual testing** still required for UI/graphics
- Accept that **some code is hard to test** (patches, events)
- **Incremental adoption** is better than trying to test everything at once

package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SplittableRandom;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RandomGeneratorTest {

  public static final int DICE_SIDES_COUNT = 6;
  public static final int DICE_THROW_COUNT = 1_000_000;
  public static final int THREAD_COUNT = 4;
  private Map<Integer, Integer> diceResults;

  @BeforeEach
  void prepareDice() {
    diceResults = new HashMap<>();
    for (int i = 0; i < DICE_SIDES_COUNT; i++) {
      diceResults.put(i, 0);
    }
  }

  @Test
  public void RandomGeneratorInterface() {
    RandomGenerator randomGenerator = new Random();

    // generate int between 0 - 9
    randomGenerator.nextInt(10);

    // generate int between 1 - 9
    randomGenerator.nextInt(1, 9);

    // generate long between 1 - 9
    randomGenerator.nextLong(1, 9);

    // generate float
    randomGenerator.nextFloat(1, 9);

    // generate double
    randomGenerator.nextDouble(1, 9);

    // generate boolean
    randomGenerator.nextBoolean();
  }

  @Test
  public void forSingleThreadedPerformanceUseCasesUseSplittableRandom() {
    calculateAndPrintResultsFor(new SplittableRandom());
  }

  @Test
  public void forMultiThreadedPerformanceUseCasesUseThreadLocalRandom()
      throws InterruptedException {

    ExecutorService executor = Executors.newWorkStealingPool();
    List<Callable<Void>> callables = new ArrayList<>();

    for (int i = 0; i < THREAD_COUNT; i++) {
      callables.add(() -> {
        calculateAndPrintResultsFor(ThreadLocalRandom.current());
        return null;
      });
    }

    executor.invokeAll(callables);
  }

  @Test
  public void forMultiThreadedSecurityRelevantUseCasesUseSecureRandomStrongInstance()
      throws NoSuchAlgorithmException {
    calculateAndPrintResultsFor(SecureRandom.getInstanceStrong());
  }

  @Test
  public void forMultiThreadedLessSecurityRelevantUseCasesUseSecureRandom() {
    calculateAndPrintResultsFor(new SecureRandom());
  }

  @Test
  public void splittableRandomWithSeedIsDeterministic() {
    assertEquals(new SplittableRandom(42).nextInt(), -491277234);
  }

  @Test
  public void splittableRandomWithDifferentSeedIsDeterministic() {
    assertEquals(new SplittableRandom(9999).nextInt(), -788346102);
  }

  @Test
  public void randomWithSeedIsDeterministic() {
    assertEquals(new Random(9999).nextInt(), -509091100);
  }

  private void calculateAndPrintResultsFor(RandomGenerator randomGenerator) {
    System.out.println(randomGenerator.getClass().getSimpleName());
    calculateDiceResults(randomGenerator);
    printDiceResults();
    printVariance();
  }

  private void calculateDiceResults(RandomGenerator randomGenerator) {
    for (int i = 0; i < DICE_THROW_COUNT; i++) {
      int rollingTheDiceResult = randomGenerator.nextInt(DICE_SIDES_COUNT);
      diceResults.compute(rollingTheDiceResult, (key, count) -> count + 1);
    }
  }

  private void printDiceResults() {
    System.out.println(diceResults);
  }

  private void printVariance() {
    int avg = calculateAverage();
    int variance = calculateVariance(avg);
    System.out.println("Variance: " + variance);
  }

  private int calculateAverage() {
    int sum = 0;
    for (int i = 0; i < DICE_SIDES_COUNT; i++) {
      sum += diceResults.get(i);
    }
    return sum / (DICE_SIDES_COUNT);
  }

  private int calculateVariance(Integer avg) {
    int diff = 0;
    for (int i = 0; i < DICE_SIDES_COUNT; i++) {
      diff += Math.abs(avg - diceResults.get(i));
    }
    int variance = diff / (DICE_SIDES_COUNT);
    return variance;
  }
}

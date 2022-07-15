package org.example;

import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;


//The test index is throughput
@BenchmarkMode(Mode.Throughput)
//Preheating is required to eliminate the impact of jit real-time compilation and JVM collection of various indicators. Since we cycle many times in a single cycle, preheating once is OK
@Warmup(iterations = 3)
//Number of threads
@Threads(10)
@Fork(1)
//Test times, we test 50 times
@Measurement(iterations = 5)
//The life cycle of a class instance is defined, and all test threads share an instance
@State(value = Scope.Benchmark)
public class BenchmarkMultiThreadedTest {
  @Param({
      "Random", "SplittableRandom", "ThreadLocalRandom"
  })
  private String name;
  private RandomGenerator randomGenerator;

  @Setup
  public void setup() {
    if (this.name.equals("ThreadLocalRandom")) {
      randomGenerator = ThreadLocalRandom.current();
    } else {
      randomGenerator = RandomGeneratorFactory.of(this.name).create();
    }
  }

  @Benchmark
  public void testRandomInt(Blackhole blackhole) {
    blackhole.consume(randomGenerator.nextInt());
  }

  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder().include(BenchmarkMultiThreadedTest.class.getSimpleName()).build();
    new Runner(opt).run();
  }
}

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


@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3)
@Threads(10)
@Fork(1)
@Measurement(iterations = 5)
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

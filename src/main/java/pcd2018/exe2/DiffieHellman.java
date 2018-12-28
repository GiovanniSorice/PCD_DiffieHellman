package pcd2018.exe2;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Classe da completare per l'esercizio 2.
 */
public class DiffieHellman {

  public class DiffieHellmanTaskBuilder implements Supplier<Callable<List<Integer>>> {

    List<Long> publicA;
    List<Long> publicB;
    int start;
    int step;



    public DiffieHellmanTaskBuilder(List<Long> publicA, List<Long> publicB) {
      this.publicA = publicA;
      this.publicB = publicB;
      this.start = 0;
      this.step = Math.round(LIMIT /Runtime.getRuntime().availableProcessors());
    }


    @Override
    public Callable<List<Integer>> get() {
      this.start +=step ;
      return new DiffieHellmanTask(start-step,start>LIMIT?LIMIT:start,publicA,publicB);
    }

    class DiffieHellmanTask implements Callable<List<Integer>> {


      int start;
      int stop;
      List<Long> aList;
      List<Long> bList;

      public DiffieHellmanTask(int start, int stop, List<Long> publicA, List<Long> publicB) {
        this.start = start;
        this.stop = stop;
        this.aList = publicA;
        this.bList = publicB;
      }

      @Override
      public List<Integer> call() throws Exception {
        System.out.println("Starting thread:"+Thread.currentThread().getId()+ "(" + this.start + ", " + this.stop + ")");

        List<Integer> partialRes = new ArrayList<>();

        int partialResCount = 0;

        /*for (int i = start; i <= stop; i++) {
          aList.add(DiffieHellmanUtils.modPow(publicA, i, p));
        }

        for (int i = 0; i <= LIMIT; i++) {
          bList.add(DiffieHellmanUtils.modPow(publicB, i, p));
        }
*/

        for (int i = start; i < stop; i++) {
          for (int j = 0; j < LIMIT; j++) {
            if (aList.get(i).equals(bList.get(j))) {
              partialRes.add(j);
              partialRes.add(i);
              partialResCount++;
            }
          }
        }
        System.out.println("Ending thread: "+Thread.currentThread().getId()+ "(" + this.start + ", " + this.stop + "); Found matches: " + partialResCount);

        return partialRes;
      }
    }
  }
  /**
   * Limite massimo dei valori segreti da cercare
   */
  private static final int LIMIT = 65536;

  private final long p;
  private final long g;

  public DiffieHellman(long p, long g) {
    this.p = p;
    this.g = g;
  }

  /**
   * Metodo da completare
   * 
   * @param publicA valore di A
   * @param publicB valore di B
   * @return tutte le coppie di possibili segreti a,b
   */
  public List<Integer> crack(long publicA, long publicB) {
    List<Integer> res = new ArrayList<>();

    List<Long> aList;
    List<Long> bList;

    aList= IntStream.range(0, LIMIT).parallel().mapToObj(x->DiffieHellmanUtils.modPow(publicA, x, p)).collect(Collectors.toList());
    bList= IntStream.range(0, LIMIT).parallel().mapToObj(x->DiffieHellmanUtils.modPow(publicB, x, p)).collect(Collectors.toList());


    ThreadPoolExecutor executor =
            (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    List<Callable<List<Integer>>> callables = new ArrayList<Callable<List<Integer>>>();

    DiffieHellmanTaskBuilder supplier = new DiffieHellmanTaskBuilder(aList,bList);

    for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++)
      callables.add(supplier.get());

    System.out.println("Scheduling computations");
    List<Future<List<Integer>>> futures = null;
    try {
      futures = executor.invokeAll(callables);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    for (Future<List<Integer>> future:futures
         ) {

      try {
        res.addAll(future.get());
      } catch (ExecutionException e) {
        e.printStackTrace();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

    }

    System.out.println("Done scheduling.");

    return res;
  }
}

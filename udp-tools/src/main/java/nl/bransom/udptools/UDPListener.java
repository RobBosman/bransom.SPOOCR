package nl.bransom.udptools;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPListener {

  private static final UDPListener singleton = new UDPListener();

  public static UDPListener getInstance() {
    return singleton;
  }

  private Map<Integer, UDPListenerThreadWorker> portWorkerMap;
  private ExecutorService executor;

  private UDPListener() {
    portWorkerMap = new ConcurrentHashMap<>();
    executor = Executors.newSingleThreadScheduledExecutor();
  }

  public void startListening(final int port, final DatagramPacketDispatcher dpDispatcher) throws IOException {
    final UDPListenerThreadWorker toBeReplacedWorker = portWorkerMap.get(port);
    if (toBeReplacedWorker != null) {
      toBeReplacedWorker.stop();
    }
    final UDPListenerThreadWorker worker = new UDPListenerThreadWorker(port, dpDispatcher);
    portWorkerMap.put(port, worker);
    executor.submit(worker);
  }

  public void stopListening(final int port) throws IOException {
    final UDPListenerThreadWorker worker = portWorkerMap.remove(port);
    if (worker != null) {
      worker.stop();
    }
  }
}

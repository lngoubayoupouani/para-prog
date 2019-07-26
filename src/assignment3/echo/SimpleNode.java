package assignment3.echo;

import assignment3.node.Node;
import assignment3.node.NodeAbstract;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleNode extends NodeAbstract {
    private int count = 0;
    private Node wokeUpBy;
    private boolean receivedFirstWakUpCall = false;
    private StringBuilder echoResult = new StringBuilder();

    public SimpleNode(String name, boolean isInitiator) {
        super(name, isInitiator);
    }

    @Override
    public void setupNeighbours(Node... neighbours) {
        for (Node n : neighbours) {
            this.neighbours.add(n);
            n.hello(this);
        }
    }

    public void printNeighbours() {
        System.out.print(name + " Neighbours:[");
        neighbours.forEach(node -> System.out.print(node + ","));
        System.out.println("]");
    }

    @Override
    public void hello(Node neighbour) {
        this.neighbours.add(neighbour);
    }

    @Override
    public synchronized void wakeup(Node neighbour) {
        System.out.println(this + ": Got wakeup-call from: " + neighbour);
        count++;
        if (wokeUpBy == null) {
            System.out.println(this + ": Woke-up from: " + neighbour);
            wokeUpBy = neighbour;
            receivedFirstWakUpCall = true;
        } else {
            System.out.println(this + ": Ignoring wakeup-call from: " + neighbour);
        }
        notify();
    }

    @Override
    public synchronized void echo(Node neighbour, Object data) {
        System.out.println(this + ": Got echo-call from: " + neighbour);
        count++;
        echoResult.append(neighbour).append(" -> ").append(this).append("\n").append(data);
        System.out.println("New echo-result " + echoResult);
        notify();
    }

    @Override
    public synchronized void run() {
        if (isInitiator) {
            ExecutorService executorService = Executors.newFixedThreadPool(neighbours.size());
            neighbours.forEach(e -> executorService.submit(() -> e.wakeup(SimpleNode.this)));
        }

        while (true) {
            try {
                System.out.println("Iteration from " + this);
                System.out.println(this + " " + count + " " + getNeighbourCount());

                if (receivedFirstWakUpCall) {
                    System.out.println(this + " waking up neighbours");

                    ExecutorService executorService = Executors.newFixedThreadPool(neighbours.size());
                    neighbours.forEach(e -> {
                        if (e != wokeUpBy) {
                            executorService.submit(() -> e.wakeup(SimpleNode.this));
                        }
                    });
                    receivedFirstWakUpCall = false;
                }

                if (count == getNeighbourCount()) {
                    if (isInitiator) {
                        System.out.println("Finished! Result: \n" + echoResult);
                    } else {
                        wokeUpBy.echo(this, echoResult);
                    }
                    return;
                }
                wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int getNeighbourCount() {
        return neighbours.size();
    }

}

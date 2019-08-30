package assignment3.election.test;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import assignment3.election.SimpleElectionNode;
import assignment3.election.node.ElectionCluster;

class Test2 {

  private SimpleElectionNode a;
  private SimpleElectionNode b;
  private SimpleElectionNode c;
  private SimpleElectionNode d;
  
  @BeforeEach
  void setUp() throws Exception {
    a = new SimpleElectionNode("A", 3, false);
    b = new SimpleElectionNode("B", 2, true);
    c = new SimpleElectionNode("C", 1, false);
    d = new SimpleElectionNode("D", 4, true);
    a.setupNeighbours(b, c, d);
    b.setupNeighbours(a, c, d);
    c.setupNeighbours(a, b, d);
    d.setupNeighbours(a, b, c);
  }

  @AfterEach
  void tearDown() throws Exception {
    assertEquals(false, a.isWon());
    assertEquals(false, b.isWon());
    assertEquals(false, c.isWon());
    assertEquals(true, d.isWon());
  }

  @Test
  @RepeatedTest(value = 100)
  void test() {
    ElectionCluster cluster = new ElectionCluster(a, b, c, d);
    cluster.printConnections();
    cluster.visualize();
    cluster.startNodes();
    
    int count = 0;
    while (!d.isWon()) {
      try {
	if (count++ == 10000) {
	  break;
	}
	Thread.sleep(1);
      } catch (InterruptedException e) {
	e.printStackTrace();
      }
    }
  }

}
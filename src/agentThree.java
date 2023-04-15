import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.LinkedList;
import java.util.Queue;

public class agentThree extends Agent {
    LinkedList<lamportMessage> Queue = new LinkedList<lamportMessage>();
    int ACK = 0;
    lamportMessage REQ = new lamportMessage("REQ", 1, 3);

    public void setup() {
        System.out.println("Site 3 running ...");
        // Sending REQ
        System.out.println("REQ-Site3 --> req Sending ...");
        send(REQ.sendREQ(1));
        send(REQ.sendREQ(2));
        Queue.add(REQ);
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage message = myAgent.receive();
                if (message != null) {
                    // Building the Message Object from the received message
                    lamportMessage receivedLamportMessage = lamportMessage.getQueueMessages(message.getContent());
                    // ---
                    switch (receivedLamportMessage.messageType) {
                        case "ACK":
                            // Updating ACK
                            ACK += 1;
                            System.out.println("Site 3 --> Received ACK number " + ACK + " from Site " + receivedLamportMessage.clock);
                            if (ACK == 2) {
                                System.out.println(" ! Site3 --> Trying to access Critical Section ...");
                                if (Queue.getFirst().siteNumber != 3) {
                                    System.out.println(" ! Site3 --> Access Denied, Site " + Queue.getFirst().siteNumber + " is on the peek of the list");
                                }
                            }
                            break;
                        case "REQ":
                            // Updating Queue
                            lamportMessage.checkpriority(Queue, receivedLamportMessage);
                            // Sending ACK
                            System.out.println("Site 3 --> Received Site " + receivedLamportMessage.siteNumber + " req");
                            receivedLamportMessage.clock = 3; // Using Clock Value to as an emitter site number
                            System.out.println("ACK-Site3 --> ACK Sending");
                            send(receivedLamportMessage.sendACK());
                            break;
                        case "REL":
                            System.out.println("Site 3 --> received REL From Site " + receivedLamportMessage.siteNumber);
                            if (!Queue.isEmpty() && receivedLamportMessage.siteNumber == Queue.getFirst().siteNumber) {
                                // Updating Queue
                                Queue.removeFirst();
                            }
                            break;
                    }
                } else {

                    this.block();
                }
            }
        });
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                // Waiting ACK ...
                if (ACK == 2 && !Queue.isEmpty() && Queue.getFirst().siteNumber == 3) {
                    /**
                     * Accessing the critical section
                     * */
                    System.out.println("-----------------------------------");
                    System.out.println("Site 3 Consuming Critical Section");
                    System.out.println("-----------------------------------");
                    //Updating Queue
                    ACLMessage[] rel = Queue.getFirst().sendREL();
                    Queue.removeFirst();
                    //Sending REL
                    System.out.println("REL-Site 3 --> REL Sending ...");
                    send(rel[0]);
                    send(rel[1]);
                    System.out.println("Site 3 Leaving Critical Section---");
                    ACK = 0;
                }
            }
        });
    }
}

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.LinkedList;


/**
 * @author ${ BENMOUSSA Younes}
 * @email : devBenmoussYounes@gmail.com
 * @created 13/04/2023
 * @project ${Simulation of the Lamport synchronisation method}
 */

// Stack(Pile) LIFO
// Queue(File) FIFO
// add = enqueue, Offer()
// remove = dequeue, poll()

public class agentOne extends Agent {
    lamportMessage REQ = new lamportMessage("REQ", 2, 1);
    int ACK = 0;
    LinkedList<lamportMessage> Queue = new LinkedList<lamportMessage>();

    public void setup() {
        /**
         Site 1 broadcast its request to enter the Critical Section  ...
         * */
        System.out.println("Site 1 running ...");
        // Sending REQ
        System.out.println("REQ-Site1 --> req Sending ...");
        send(REQ.sendREQ(2));
        send(REQ.sendREQ(3));
        Queue.add(REQ);
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                // Queue Process
                ACLMessage message = myAgent.receive();
                if (message != null) {
                    // Building the Message Object from the received message
                    lamportMessage receivedLamportMessage = lamportMessage.getQueueMessages(message.getContent());
                    // ---
                    switch (receivedLamportMessage.messageType) {
                        case "ACK":
                            // Updating ACK
                            ACK += 1;
                            System.out.println("Site 1 --> Received ACK number " + ACK + " from Site " + receivedLamportMessage.clock);
                            if (ACK == 2) {
                                System.out.println(" ! Site1 --> Trying to access Critical Section ...");
                                if (Queue.getFirst().siteNumber != 1) {
                                    System.out.println(" ! Site1 --> Access Denied, Site " + Queue.getFirst().siteNumber + " is on the peek of the list");
                                }
                            }
                            break;
                        case "REQ":
                            // Updating Queue
                            lamportMessage.checkpriority(Queue, receivedLamportMessage);
                            System.out.println("Site 1 --> Received Site " + receivedLamportMessage.siteNumber + " REQ");
                            // Sending ACK
                            receivedLamportMessage.clock = 1; // Using Clock Value to as an emitter site number
                            System.out.println("ACK-Site1 --> ACK Sending");
                            send(receivedLamportMessage.sendACK());
                            break;
                        case "REL":
                            System.out.println("Site 1 --> received REL From " + receivedLamportMessage.siteNumber);
                            if (receivedLamportMessage.siteNumber == Queue.getFirst().siteNumber) {
                                // Updating Queue
                                Queue.removeFirst();
                            }
                            break;
                    }
                } else {
                    block();
                }
            }
        });
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                // Waiting ACK ...
                if (ACK == 2 && !Queue.isEmpty() && Queue.getFirst().siteNumber == 1) {
                    /**
                     * Accessing the critical section
                     * */
                    System.out.println("-----------------------------------");
                    System.out.println("Site 1 Consuming Critical Section");
                    System.out.println("-----------------------------------");
                    this.block(3000);
                    //Updating Queue
                    ACLMessage[] rel = Queue.getFirst().sendREL();
                    Queue.removeFirst();
                    //Sending REL
                    System.out.println("REL-Site 1 --> REL Sending ...");
                    send(rel[0]);
                    send(rel[1]);
                    System.out.println("Site 1 Leaving Critical Section---");
                    ACK = 0;
                }
            }
        });
    }

}


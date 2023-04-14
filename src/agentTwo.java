import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.LinkedList;

public class agentTwo extends Agent {
    lamportMessage REQ = new lamportMessage("REQ", 2, 2);
    int ACK = 0;
    LinkedList<lamportMessage> Queue = new LinkedList<lamportMessage>();

    public void setup() {
        /**
         Site 2 broadcast its request to enter the Critical Section  ...
         * */
        System.out.println("Site 2 running ...");
        // Sending REQ
        System.out.println("REQ-Site2 --> req Sending ...");
        send(REQ.sendREQ(1));
        send(REQ.sendREQ(3));
        Queue.add(REQ);
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                if (ACK != 2) {
                    ACLMessage message = myAgent.receive();
                    if (message != null) {
                        // Building the Message Object from the received message
                        lamportMessage receivedLamportMessage = lamportMessage.getQueueMessages(message.getContent());
                        // ---
                        switch (receivedLamportMessage.messageType) {
                            case "ACK":
                                // Updating ACK
                                ACK += 1;
                                System.out.println("Site 2 --> Received ACK number " + ACK + " from Site " + receivedLamportMessage.clock);
                                if (ACK == 2) {
                                    System.out.println(" ! Site2 --> Trying to access Critical Section ...");
                                }
                                break;
                            case "REQ":
                                // Updating Queue
                                lamportMessage.checkpriority(Queue, receivedLamportMessage);
                                System.out.println("Site 2 --> Received Site " + receivedLamportMessage.siteNumber + " REQ");
                                // Sending ACK
                                receivedLamportMessage.clock = 2; // Using Clock Value to as an emitter site number
                                System.out.println("ACK-Site 2 --> ACK Sending");
                                send(receivedLamportMessage.sendACK());
                                break;
                            case "REL":
                                System.out.println("Site 2 --> REL received rel From " + receivedLamportMessage.siteNumber);
                                if (receivedLamportMessage.siteNumber == Queue.getFirst().siteNumber) {
                                    // Updating Queue
                                    Queue.removeFirst();
                                }
                                break;
                        }
                    } else {
                        this.block();
                    }
                }
            }
        });

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                // Waiting ACK ...
                if (ACK == 2 && !Queue.isEmpty() && Queue.getFirst().siteNumber == 2) {
                    /**
                     * Accessing the critical section
                     * */
                    System.out.println("-----------------------------------");
                    System.out.println("Site 2 Consuming Critical Section");
                    System.out.println("-----------------------------------");
                    //Updating Queue
                    ACLMessage[] rel = Queue.getFirst().sendREL();
                    Queue.removeFirst();
                    //Sending REL
                    System.out.println("REL-Site 2 --> REL Sending ...");
                    send(rel[0]);
                    send(rel[1]);
                    System.out.println("Site 2 Leaving Critical Section---");
                    ACK = 0;
                }
            }
        });
    }

}

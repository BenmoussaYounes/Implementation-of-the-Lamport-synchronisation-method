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

        // Sending REQ
        send(REQ.sendREQ(1));
        send(REQ.sendREQ(3));
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
                            System.out.println("Site 2 Ack number: " + ACK);
                            break;
                        case "REQ":
                            // Updating Queue
                            lamportMessage.checkpriority(Queue, receivedLamportMessage);
                            // Sending ACK
                            send(receivedLamportMessage.sendACK());
                            break;
                        case "REL":
                            if (receivedLamportMessage.siteNumber == Queue.getFirst().siteNumber) {
                                // Updating Queue
                                Queue.removeFirst();
                            }
                            break;
                    }
                } else {
                    System.out.println("Site 2 Waiting ...");
                    this.block();
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
                    send(rel[0]);
                    send(rel[1]);
                    ACK = 0;
                }
            }
        });
    }

}

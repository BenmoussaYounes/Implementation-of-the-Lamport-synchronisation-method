import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.LinkedList;

public class agentThree extends Agent {
    LinkedList<lamportMessage> Queue = new LinkedList<lamportMessage>();
    int ACK = 0;
    lamportMessage REQ = new lamportMessage("REQ", 3, 3);

    public void setup() {
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
                            System.out.println("Site 3 Ack number: " + ACK);
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
                    System.out.println("Site 3 Waiting ...");
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
                    send(rel[0]);
                    send(rel[1]);
                    ACK = 0;
                }
            }
        });
    }
}
